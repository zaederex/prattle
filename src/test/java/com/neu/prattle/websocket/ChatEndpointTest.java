/*
 * Copyright (c) 2020. Manan Patel
 * All rights reserved
 */

package com.neu.prattle.websocket;

import com.neu.prattle.configuration.SpringContext;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.service.HashTagServiceImpl;
import com.neu.prattle.service.MessageServiceDaoImpl;
import com.neu.prattle.service.UserServiceDaoImpl;
import com.neu.prattle.service.group.GroupServiceDaoImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A test class to test the implementation of ChatEndpoint class.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContext.class, loader = AnnotationConfigContextLoader.class)
public class ChatEndpointTest {

  @Mock
  private static User testUser1 = mock(User.class);
  @Mock
  private static User testUser2 = mock(User.class);
  @Mock
  private static User testUser3 = mock(User.class);

  private Message message;
  private static final String FILEPATH = "src/main/resources/outputfiles/";

  // Mocking Session to connect with websocket
  @Mock
  private Session session1;
  @Mock
  private Session session2;
  @Mock
  private Session session3;
  // Mocking basic which is used by session to send message
  @Mock
  private Basic basic;
  // To capture messages sent by Websockets
  private ArgumentCaptor<Object> valueCapture;
  // ChatEndpoints to test

  @MockBean
  private UserServiceDaoImpl userService;
  @MockBean
  private GroupServiceDaoImpl groupService;
  @MockBean
  private MessageServiceDaoImpl messageService;

  @MockBean
  private HashTagServiceImpl hashTagService;

  private ChatEndpoint chatEndpoint1;
  private ChatEndpoint chatEndpoint2;
  private ChatEndpoint chatEndpoint3;


  @Before
  public void setupBeforeEach() throws IOException, EncodeException {

    userService.addUser(testUser1);
    userService.addUser(testUser2);
    userService.addUser(testUser3);

    MockitoAnnotations.initMocks(this);
    session1 = mock(Session.class);
    session2 = mock(Session.class);
    session3 = mock(Session.class);

    basic = mock(Basic.class);

    message = Message.messageBuilder().setMessageGenerationTime(
            new Timestamp(new Date().getTime())).build();

    chatEndpoint1 = new ChatEndpoint();
    chatEndpoint2 = new ChatEndpoint();
    chatEndpoint3 = new ChatEndpoint();

    // Capturing method calls using when and then
    when(session1.getBasicRemote()).thenReturn(basic);
    when(session2.getBasicRemote()).thenReturn(basic);
    when(session3.getBasicRemote()).thenReturn(basic);

    // Setting up argument captor to capture any Objects
    valueCapture = ArgumentCaptor.forClass(Object.class);
    // Defining argument captor to capture messages emitted by websockets
    doNothing().when(basic).sendObject(valueCapture.capture());
    // Capturing method calls to session.getId() using when and then
    when(session1.getId()).thenReturn("id1");
    when(session2.getId()).thenReturn("id2");
    when(session3.getId()).thenReturn("id3");

    Optional<User> optionalUser = Optional.of(testUser1);
    when(userService.findUserByName("neel101")).thenReturn(optionalUser);
    optionalUser = Optional.of(testUser2);
    when(userService.findUserByName("zoheb101")).thenReturn(optionalUser);
    optionalUser = Optional.of(testUser3);
    when(userService.findUserByName("sameer101")).thenReturn(optionalUser);

    HashTag hashtag = HashTag.hashTagBuilder().setHashTagValue("awesome").build();
    when(userService.findUserByName("neel101")).thenReturn(Optional.of(testUser1));
    when(userService.findUserByName("testName2")).thenReturn(Optional.of(testUser1));
    when(userService.findUserByName("testName3")).thenReturn(Optional.of(testUser1));
    when(hashTagService.createHashTag(anyString(), any())).thenReturn(hashtag);
  }

  private void open() throws IOException, EncodeException, UserDoesNotExistException {
    when(testUser1.getUsername()).thenReturn("neel101");
    when(testUser1.getUserID()).thenReturn(1);
    when(testUser2.getUsername()).thenReturn("zoheb101");
    when(testUser2.getUserID()).thenReturn(2);
    when(testUser3.getUsername()).thenReturn("sameer101");
    when(testUser3.getUserID()).thenReturn(3);

    when(userService.findUserById(1)).thenReturn(Optional.of(testUser1));
    when(userService.findUserById(2)).thenReturn(Optional.of(testUser2));
    when(userService.findUserById(3)).thenReturn(Optional.of(testUser3));

    chatEndpoint1.onOpen(session1, testUser1.getUsername());
    chatEndpoint2.onOpen(session2, testUser2.getUsername());
    chatEndpoint3.onOpen(session3, testUser3.getUsername());
  }

  private void close() {
    chatEndpoint1.onClose(session1);
    chatEndpoint2.onClose(session2);
    chatEndpoint3.onClose(session3);
  }

  @Test
  public void testOnOpen() throws IOException, EncodeException, UserDoesNotExistException {
    when(testUser1.getUsername()).thenReturn("neel101");
    when(testUser1.getUserID()).thenReturn(1);
    chatEndpoint1.onOpen(session1, testUser1.getUsername());

    // Finding the message with content 'Connected!'
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Connected!")).findAny();

    if (m.isPresent()) {
      assertEquals("Connected!", m.get().getContent());
    } else {
      fail();
    }
  }

  @Test
  public void testOnOpenUserDoesNotExist()
          throws IOException, EncodeException, UserDoesNotExistException {
    chatEndpoint1.onOpen(session1, "nonexistentUser");

    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("User nonexistentUser could not be found")).findAny();

    if (m.isPresent()) {
      assertEquals("User nonexistentUser could not be found", m.get().getContent());
      assertEquals(0, m.get().getFromUserId());
    } else {
      fail();
    }
  }

  @Test
  public void testOnClose() throws IOException, EncodeException, UserDoesNotExistException {
    when(testUser1.getUsername()).thenReturn("neel101");
    when(testUser1.getUserID()).thenReturn(1);
    when(testUser2.getUsername()).thenReturn("zoheb101");
    when(testUser2.getUserID()).thenReturn(2);
    chatEndpoint1.onOpen(session1, testUser1.getUsername());
    chatEndpoint2.onOpen(session2, testUser2.getUsername());

    chatEndpoint1.onClose(session1);

    // Finding the message with content 'Disconnected!'
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Disconnected!")).findAny();
    assertTrue(m.isPresent());
  }

  @Test
  public void testOnMessageUndefinedTarget()
          throws IOException, EncodeException, UserDoesNotExistException {
    open();
    message.setFromUserId(testUser1.getUserID());
    message.setContent("This message will never make it to the intended target :'(");
    message.setToUserId(10);

    when(messageService.saveNewMessage(message)).thenReturn(message);

    chatEndpoint1.onMessage(message);

    assertEquals("The target recipient is not a registered user!", message.getContent());
  }

  @Test
  public void testOnMessagePointToPoint()
          throws IOException, EncodeException, UserDoesNotExistException {
    open();

    message.setFromUserId(testUser1.getUserID());
    message.setContent("Hey");
    message.setToUserId(testUser2.getUserID());

    when(messageService.saveNewMessage(message)).thenReturn(message);
    // Sending a message using onMessage method
    chatEndpoint1.onMessage(message);

    // Finding messages with content hey
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Hey")).findAny();

    if (m.isPresent()) {
      assertEquals("Hey", m.get().getContent());
      assertEquals(testUser1.getUsername(),
              Objects.requireNonNull(userService.findUserById(m.get().getFromUserId()).orElse(null))
                      .getUsername());
      assertEquals(testUser2.getUsername(),
              Objects.requireNonNull(userService.findUserById(m.get().getToUserId()).orElse(null))
                      .getUsername());
    } else {
      fail();
    }

    close();
  }

  private String getLastLine(String filePath) throws IOException {
    List<String> allLines =
            Files.readAllLines(Paths.get(filePath));
    return allLines.get(allLines.size() - 1);
  }

  @Test
  public void testOnMessagePointToPointReply()
          throws IOException, EncodeException, UserDoesNotExistException {
    open();
    message.setFromUserId(testUser1.getUserID());
    message.setContent("Hi, testUser3! #awesome");
    message.setToUserId(testUser3.getUserID());

    when(messageService.saveNewMessage(message)).thenReturn(message);
    chatEndpoint1.onMessage(message);

    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Hi, testUser3! #awesome")).findAny();

    if (m.isPresent()) {
      assertEquals("Hi, testUser3! #awesome", m.get().getContent());
      assertEquals(testUser1.getUsername(),
              Objects.requireNonNull(userService.findUserById(m.get().getFromUserId()).orElse(null))
                      .getUsername());
      assertEquals(testUser3.getUsername(),
              Objects.requireNonNull(userService.findUserById(m.get().getToUserId()).orElse(null))
                      .getUsername());
    } else {
      fail();
    }

    message = Message.messageBuilder().build();
    message.setFromUserId(testUser3.getUserID());
    message.setContent("Hey there, testUser1! How you doin'?");
    message.setToUserId(testUser1.getUserID());
    close();
  }

  @Test
  public void testGroupMessage() throws UserDoesNotExistException, EncodeException, IOException {
    open();

    Group group = mock(Group.class);
    when(group.getGroupID()).thenReturn(6);
    when(group.getGroupName()).thenReturn("testGroup");
    when(messageService.saveNewMessage(message)).thenReturn(message);
    when(groupService.findGroupById(6)).thenReturn(Optional.of(group));
    when(groupService.findGroupByName(group.getGroupName())).thenReturn(Optional.of(group));

    message.setFromUserId(testUser2.getUserID());
    message.setIsGroupMessage(true);
    message.setToUserId(6);
    message.setContent("Hi Group!");


    when(groupService.isGroup("testGroup")).thenReturn(true);
    when(groupService.isMember(group, testUser2)).thenReturn(true);
    when(groupService.isMember(group, testUser3)).thenReturn(true);
    List<User> members = new ArrayList<>();
    members.add(testUser2);
    members.add(testUser3);

    when(groupService.getAllUsersInGroupsAndSubGroups(anyString())).thenReturn(members);

    chatEndpoint2.onMessage(message);
    System.out.println(valueCapture.getAllValues());
    List<Message> messages = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Hi Group!")).collect(Collectors.toList());

    assertTrue(messages.size() == 2);


  }

  @Test
  public void testGetStashedMessages()
          throws UserDoesNotExistException, EncodeException, IOException {
    when(testUser1.getUsername()).thenReturn("neel101");
    when(testUser1.getUserID()).thenReturn(3);

    message.setContent("This is a stashed message.");
    List<Message> stashedMessages = new ArrayList<>();
    stashedMessages.add(message);
    when(userService.findUserById(3)).thenReturn(Optional.of(testUser1));
    when(messageService.getUnsentMessages(testUser1.getUsername(), true)).thenReturn(stashedMessages);

    chatEndpoint1.onOpen(session1, testUser1.getUsername());

    List<Message> messages = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals(message.getContent())).collect(Collectors.toList());

    assertEquals(1, messages.size());

  }
}
