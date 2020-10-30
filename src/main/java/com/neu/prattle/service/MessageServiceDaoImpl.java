package com.neu.prattle.service;

import com.neu.prattle.dto.MessageDTO;
import com.neu.prattle.exceptions.ConversationNotFoundException;
import com.neu.prattle.exceptions.MessageAlreadyExistsException;
import com.neu.prattle.exceptions.MessageDoesNotExistException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.MessageAttachment;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.repository.HashTagRepository;
import com.neu.prattle.repository.MessageRepository;
import com.neu.prattle.repository.UserRepository;
import com.neu.prattle.service.group.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.neu.prattle.utils.FileHandler.createFileForMediaTypeData;
import static com.neu.prattle.utils.FileHandler.getOriginalMediaTypeData;

/**
 * Implements the {@link com.neu.prattle.service.MessageService} interface to persist message
 * objects to a database.
 */
@Service
public class MessageServiceDaoImpl implements MessageService {

  private static String userNotFoundMessage = "User does not exist";

  private MessageRepository messageRepository;
  private UserRepository userRepository;
  private HashTagRepository hashTagRepository;
  private GroupService groupService;

  @Autowired
  public void setGroupService(GroupService groupService) {
    this.groupService = groupService;
  }

  @Autowired
  public void setHashTagRepository(HashTagRepository hashTagRepository) {
    this.hashTagRepository = hashTagRepository;
  }

  @Autowired
  public void setMessageRepository(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Message saveNewMessage(Message message) {
    if (messageRepository.existsById(message.getMessageId())) {
      throw new MessageAlreadyExistsException
              ("The message already exists");
    }

    storeAttachmentFilesIfNeeded(message);

    message = saveMessageToRepository(message);

    restoreOriginalAttachmentDataIfNeeded(message);

    return message;
  }

  private Message saveMessageToRepository(Message message) {
    return messageRepository.saveAndFlush(message);
  }

  @Override
  public Message updateMessage(MessageDTO messageDTO, int messageId) {
    Message message = findMessageById(messageId);
    message.setMessageStatus(messageDTO.getMessageStatus());
    storeAttachmentFilesIfNeeded(message);
    message = messageRepository.save(message);
    restoreOriginalAttachmentDataIfNeeded(message);
    return message;
  }

  @Override
  public Message findMessageById(int id) {
    Optional<Message> foundMessage = messageRepository.findById(id);
    if (!foundMessage.isPresent()) {
      throw new MessageDoesNotExistException("This message does not exist");
    }
    return restoreOriginalAttachmentDataIfNeeded(foundMessage.get());
  }

  @Override
  public List<Message> findMessagesForReceivingUser(String username, boolean excludeExpired)
          throws UserDoesNotExistException {
    Optional<User> currUser = userRepository.findByUsername(username);
    if (!currUser.isPresent()) {
      throw new UserDoesNotExistException(userNotFoundMessage);
    }
    List<Message> allMessages = this.messageRepository.findByToUserId(currUser.get().getUserID()).stream().sorted(
            Comparator.comparingInt(Message::getMessageId))
            .collect(Collectors.toList());
    if (excludeExpired) {
      List<Message> expiredMessages = findExpiredMessages(allMessages);
      allMessages.removeAll(expiredMessages);
    }
    return restoreAttachmentDataForManyMessages(filterMessages(allMessages, currUser.get()));
  }

  private List<Message> filterMessages(List<Message> allMessages, User user) {
    List<Message> result = new ArrayList<>();
    for (Message message : allMessages) {
      boolean containsKeyword = false;
      for (Filter filter : user.getFilters()) {
        if (message.getContent().toLowerCase().contains(filter.getFilterString().toLowerCase())) {
          containsKeyword = true;
          break;
        }
      }
      if (!containsKeyword) {
        result.add(message);
      }
    }
    return restoreAttachmentDataForManyMessages(result);
  }

  private List<Message> findExpiredMessages(List<Message> allMessages) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, -1);
    return restoreAttachmentDataForManyMessages(allMessages.stream().filter(x -> x.getIsSelfDestructMessage()
            && x.getGeneratedTime().before(new Timestamp(cal.getTime().getTime())))
            .collect(Collectors.toList()));
  }

  @Override
  public List<Message> findMessagesBetweenTwoUsers(
          String currentUsername, String conversingUsername, boolean excludeExpired)
          throws UserDoesNotExistException {
    int currUserID = returnUserIDIfExists(currentUsername);
    int conversingUserID = returnUserIDIfExists(conversingUsername);
    List<Message> conversation = this.messageRepository
            .findByFromUserIdAndToUserId(currUserID, conversingUserID);
    conversation.addAll(this.messageRepository
            .findByFromUserIdAndToUserId(conversingUserID, currUserID));
    if (excludeExpired) {
      List<Message> excludedMessages = findExpiredMessages(conversation);
      conversation.removeAll(excludedMessages);
    }
    if (conversation.isEmpty()) {
      throw new ConversationNotFoundException(
              "The conversation between these users could not be found"
      );
    }
    return restoreAttachmentDataForManyMessages(conversation.stream()
            .sorted(Comparator.comparingInt(Message::getMessageId))
            .collect(Collectors.toList()));
  }

  @Override
  public int getNewMessageCount(String firstPersonUsername,
                                String secondPersonUsername) throws UserDoesNotExistException {
    int currUserID = returnUserIDIfExists(firstPersonUsername);
    int conversingUserID = returnUserIDIfExists(secondPersonUsername);
    List<Message> unreadMessages = messageRepository.findNewMessages(currUserID, conversingUserID);
    return unreadMessages.size();
  }

  @Override
  public List<Message> getUnsentMessages(String userName, boolean excludeExpired) throws UserDoesNotExistException {
    Optional<User> optionalUser = userRepository.findByUsername(userName);
    if (!optionalUser.isPresent()) {
      throw new UserDoesNotExistException(userNotFoundMessage);
    }

    User user = optionalUser.get();
    List<Message> unsentMessages = messageRepository.fetchUnreadMessages(user.getUserID());
    if (excludeExpired) {
      List<Message> excludedMessages = findExpiredMessages(unsentMessages);
      unsentMessages.removeAll(excludedMessages);
    }
    return restoreAttachmentDataForManyMessages(filterMessages(unsentMessages, user));
  }

  @Override
  public List<Message> findMessagesByHashtag(String hashtag, String username,
                                             boolean excludeExpired) {
    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (!optionalUser.isPresent()) {
      return new ArrayList<>();
    }
    User user = optionalUser.get();
    List<Message> allMessages = hashTagRepository.findMessagesByHashtagString(hashtag);
    if (excludeExpired) {
      List<Message> excludedMessages = findExpiredMessages(allMessages);
      allMessages.removeAll(excludedMessages);
    }
    List<Message> filteredPrivateMessages = allMessages.stream().filter(
            (x -> x.getIsPrivateMessage() && (x.getToUserId() == user.getUserID()
                    || x.getFromUserId() == user.getUserID()))).collect(Collectors.toList());
    List<Group> groups = groupService.getGroupsForUser(user);
    List<Message> groupMessages = allMessages.stream().filter(
            Message::getIsGroupMessage).collect(Collectors.toList());
    List<Message> filteredGroupMessages = new ArrayList<>();
    for (Group group : groups) {
      filteredGroupMessages.addAll(groupMessages.stream().filter(
              x -> x.getToUserId() == group.getGroupID()).collect(Collectors.toList())
      );
    }

    Set<Message> resultMatch =
            allMessages.stream().filter(Message::getIsBroadcastMessage).collect(Collectors.toSet());
    resultMatch.addAll(filteredGroupMessages);
    resultMatch.addAll(filteredPrivateMessages);
    Comparator<Message> recencyComparator = (m1, m2) ->
            m2.getGeneratedTime().compareTo(m1.getGeneratedTime());
    return restoreAttachmentDataForManyMessages(resultMatch.stream().sorted(recencyComparator).collect(Collectors.toList()));
  }

  @Override
  public void updateHashTagSearchHits(HashTag hashTag) {
    hashTag.setSearchHits(hashTag.getSearchHits() + 1);
    hashTagRepository.save(hashTag);
  }

  @Override
  public List<Message> getMessagesForThread(int sourceMessageID) {
    return messageRepository.findAllBySourceMessageIdOrderByGeneratedTime(sourceMessageID);
  }

  /**
   * Return id of the user if exists.
   *
   * @param username username of the user
   * @return user id
   * @throws UserDoesNotExistException when user does not exist
   */
  private int returnUserIDIfExists(String username) throws UserDoesNotExistException {
    Optional<User> user = userRepository.findByUsername(username);
    if (!user.isPresent()) {
      throw new UserDoesNotExistException("From user does not exist");
    }
    return user.get().getUserID();
  }

  private void storeAttachmentFilesIfNeeded(Message message) {
    if (message.hasAttachment()) {
      for (MessageAttachment attachment : message.getAttachments()) {
        attachment.setWebUrl(createFileForMediaTypeData(attachment.getWebUrl()));
      }
    }
  }

  private List<Message> restoreAttachmentDataForManyMessages(List<Message> list) {
    for (Message message : list) {
      restoreOriginalAttachmentDataIfNeeded(message);
    }
    return list;
  }

  private Message restoreOriginalAttachmentDataIfNeeded(Message message) {
    if (message.hasAttachment()) {
      for (MessageAttachment attachment : message.getAttachments()) {
        attachment.setWebUrl(getOriginalMediaTypeData(attachment.getWebUrl()));
      }
    }
    return message;
  }
}
