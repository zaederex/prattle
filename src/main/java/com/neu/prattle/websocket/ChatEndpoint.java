package com.neu.prattle.websocket;

import com.neu.prattle.configuration.SpringContext;
import com.neu.prattle.exceptions.MessageAlreadyExistsException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.service.HashTagService;
import com.neu.prattle.service.HashTagServiceImpl;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceDaoImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceDaoImpl;
import com.neu.prattle.service.group.GroupService;
import com.neu.prattle.service.group.GroupServiceDaoImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


/**
 * The Class ChatEndpoint.
 * <p>
 * This class handles Messages that arrive on the server.
 */
@Component
@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders =
        MessageEncoder.class)
@ComponentScan("com.neu.prattle.websocket")
public class ChatEndpoint {

  /**
   * The account service.
   */
  private UserService userService;
  private GroupService groupService;
  private MessageService messageService;
  private HashTagService hashTagService;

  public ChatEndpoint() {
    userService = SpringContext.getBean(UserServiceDaoImpl.class);
    messageService = SpringContext.getBean(MessageServiceDaoImpl.class);
    groupService = SpringContext.getBean(GroupServiceDaoImpl.class);
    hashTagService = SpringContext.getBean(HashTagServiceImpl.class);
  }

  /**
   * The session.
   */
  private Session session;

  /**
   * The Constant chatEndpoints.
   */
  private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();

  /**
   * The users.
   */
  private static HashMap<String, String> users = new HashMap<>();

  /**
   * The logger.
   */
  private Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);

  /**
   * On open.
   * <p>
   * Handles opening a new session (websocket connection). If the user is a known user (user
   * management), the session added to the pool of sessions and an announcement to that pool is made
   * informing them of the new user.
   * <p>
   * If the user is not known, the pool is not augmented and an error is sent to the originator.
   *
   * @param session  the web-socket (the connection)
   * @param username the name of the user (String) used to find the associated UserService object
   * @throws IOException     Signals that an I/O exception has occurred.
   * @throws EncodeException the encode exception
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username)
          throws IOException, EncodeException, UserDoesNotExistException {
    session.setMaxTextMessageBufferSize(50000000);
    Optional<User> user = userService.findUserByName(username);
    if (!user.isPresent()) {
      Message error = Message.messageBuilder()
              .setMessageContent(String.format("User %s could not be found", username))
              .build();

      session.getBasicRemote().sendObject(error);
      logger.error("Cannot create session for non existent user {}", username);
      return;
    }

    //register this endpoint
    addEndpoint(session, username);
    Message message = createConnectedMessage(user.get().getUserID());
    broadcast(message);

    //fetch list of undelivered messages to this recipient
    List<Message> stashedMessages = getStashedMessages(user.get());
    for (Message stashedMessage : stashedMessages) {
      echo(stashedMessage);
    }
    logger.info("Stashed messages delivered for user {}", username);
  }

  /**
   * Creates a Message that some user is now connected - that is, a Session was opened
   * successfully.
   *
   * @param userID user ID
   * @return Message
   */
  private Message createConnectedMessage(int userID) {
    logger.info("Creating message for user with ID={}", userID);
    return Message.messageBuilder()
            .setFromUserId(userID)
            .setMessageContent("Connected!")
            .build();
  }

  /**
   * Adds a newly opened session to the pool of sessions.
   *
   * @param session  the newly opened session
   * @param username the user who connected
   */
  private void addEndpoint(Session session, String username) {
    this.session = session;
    chatEndpoints.add(this);
    /* users is a hashmap between session ids and users */
    users.put(session.getId(), username);
    logger.info("Created endpoint for user {}", username);
  }

  /**
   * On message.
   * <p>
   * When a message arrives, broadcast it to all connected users.
   *
   * @param messageDTO the inbound message
   */
  @OnMessage
  public void onMessage(Message messageDTO) {
    Set<HashTag> hashtags = createHashTags(messageDTO.getContent(), messageDTO);
    messageDTO.setHashTagSet(hashtags);
    if (messageDTO.hasAttachment()) {
      messageDTO.getAttachments().forEach(a -> a.setMessage(messageDTO));
    }
    //save this message to DB
    messageDTO.setGeneratedTime(Timestamp.valueOf(LocalDateTime.now()));
    try {
      Message message = messageService.saveNewMessage(messageDTO);
      logger.info("Message saved");
      sendMessageByType(message);
      logger.info("Message sent to user with ID {}", messageDTO.getToUserId());
    } catch (MessageAlreadyExistsException e) {
      logger.error(e.getMessage());
    }
  }

  private void sendMessageByType(Message message) {
    if (message.getIsBroadcastMessage()) {
      broadcast(message);
    } else if (message.getIsGroupMessage()) {
      Optional<Group> to = groupService.findGroupById(message.getToUserId());
      to.ifPresent(group -> sendToGroup(group.getGroupName(), message));
    } else {
      Optional<User> optionalUser = userService.findUserById(message.getToUserId());
      if (optionalUser.isPresent()) {
        User recipient = optionalUser.get();
        sendToTarget(recipient, message);
        logger.info("Message sent to user {}", recipient.getUsername());
      } else {
        logger.error("Receiver with ID {} does not exist", message.getToUserId());
        message.setContent("The target recipient is not a registered user!");
        echo(message);
      }
    }
  }

  private void sendToGroup(String groupName, Message message) {
    Optional<Group> testGroup = groupService.findGroupByName(groupName);
    Group targetGroup;
    if (testGroup.isPresent()) {
      targetGroup = testGroup.get();
      List<User> allUsersInGroupsAndSubGroups = groupService
              .getAllUsersInGroupsAndSubGroups(targetGroup.getGroupName());
      for (User member : allUsersInGroupsAndSubGroups) {
        if (getFilterMatch(message, member)) {
          continue;
        }
        chatEndpoints.forEach(endpoint -> {
          String userSessionId = getSessionIdByUser(member);
          String endpointSessionId = endpoint.session.getId();
          if (endpointSessionId.equals(userSessionId)) {
            //write at destination
            logger.info("Sending message to user {}", member.getUsername());
            try {
              endpoint.session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
              logger.error(e.getMessage());
            }
          }
        });
      }
      logger.info("Message delivered to group {}", groupName);
    }
    logger.info("Could not find group");
  }

  private boolean getFilterMatch(Message message, User member) {
    boolean filterMatched = false;
    for (Filter filter : member.getFilters()) {
      if (message.getContent().toLowerCase().contains(filter.getFilterString().toLowerCase())) {
        filterMatched = true;
        break;
      }
    }
    return filterMatched;
  }

  /**
   * Broadcast.
   * <p>
   * Send a Message to each session in the pool of sessions. The Message sending action is
   * synchronized.  That is, if another Message tries to be sent at the same time to the same
   * endpoint, it is blocked until this Message finishes being sent..
   *
   * @param message the message object with to, content and from.
   */
  private void broadcast(Message message) {
    chatEndpoints.forEach(endpoint ->
            executeSend(message, endpoint)
    );
    logger.info("Message successfully broadcasted to all users");
  }

  private void echo(Message message) {
    executeSend(message, this);
  }

  private void sendToTarget(User target, Message message) {

    //write at source
    echo(message);
    if (!getFilterMatch(message, target)) {
      chatEndpoints.forEach(endpoint -> {
        if (endpoint.session.getId().equals(getSessionIdByUser(target))) {
          //write at destination
          executeSend(message, endpoint);
        }
      });
    }
  }

  private void executeSend(Message message, ChatEndpoint endpoint) {
    try {
      endpoint.session.getBasicRemote().sendObject(message);
    } catch (IOException | EncodeException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * On close.
   * <p>
   * Closes the session by removing it from the pool of sessions and broadcasting the news to
   * everyone else.
   *
   * @param session the session
   */
  @OnClose
  public void onClose(Session session) {
    chatEndpoints.remove(this);
    users.remove(session.getId());
    Message message = new Message();
    message.setContent("Disconnected!");
    broadcast(message);
    logger.info("{} has disconnected", session.getId());
  }

  /**
   * On error.
   * <p>
   * Handles situations when an error occurs.  Not implemented.
   *
   * @param session   the session with the problem
   * @param throwable the action to be taken.
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    // Do error handling here
  }

  private String getSessionIdByUser(User user) {
    String name = user.getUsername();
    for (Entry<String, String> entry : users.entrySet()) {
      if (name.equals(entry.getValue())) {
        logger.info("Session if for user {} has been retrieved", user.getUsername());
        return entry.getKey();
      }
    }
    logger.error("Session not found");
    return "";
  }

  private List<Message> getStashedMessages(User user) throws UserDoesNotExistException {
    return messageService.getUnsentMessages(user.getUsername(), true);
  }


  private Set<HashTag> createHashTags(String content, Message message) {
    Set<HashTag> result = new HashSet<>();
    Pattern hashtagPattern = Pattern.compile("#(\\w+)");
    Matcher mat = hashtagPattern.matcher(content);
    while (mat.find()) {
      result.add(hashTagService.createHashTag(mat.group(1), message));
    }
    return result;
  }
}
