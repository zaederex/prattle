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
import com.neu.prattle.model.MessageStatus;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.repository.HashTagRepository;
import com.neu.prattle.repository.MessageRepository;
import com.neu.prattle.repository.UserRepository;
import com.neu.prattle.service.group.GroupServiceDaoImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for the MessageServiceDaoImpl
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class MessageServiceDaoImplTest {

  private Message emptyMessage;
  private Message testMessage;
  private Message testMessage1;
  private Message testMessage2;
  private Message testMessage3;
  private Message testMessage4;
  private Message testMessage5;
  private MessageAttachment attachment;


  @Mock
  private MessageRepository messageRepository;

  @Mock
  private GroupServiceDaoImpl groupService;

  @Mock
  private HashTagRepository hashTagRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private MessageServiceDaoImpl messageService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    messageService.setMessageRepository(messageRepository);
    attachment = new MessageAttachment();
    attachment.setFileID(1);
    attachment.setWebUrl("www.googledrive.com");
    createTestMessages();
  }

  @Test
  public void testSaveMessageWithAttachments() {
    given(messageRepository.saveAndFlush(any(Message.class))).willReturn(testMessage);
    assertEquals(messageService.saveNewMessage(testMessage), testMessage);
  }

  @Test
  public void testSaveMessage() {
    given(messageRepository.saveAndFlush(any(Message.class))).willReturn(testMessage1);
    assertEquals(messageService.saveNewMessage(testMessage1), testMessage1);
  }

  @Test(expected = MessageAlreadyExistsException.class)
  public void testSaveMessageAlreadyexists() {
    given(messageRepository.existsById(anyInt())).willReturn(true);
    messageService.saveNewMessage(testMessage);
  }

  @Test
  public void testUpdateMessage() {
    Mockito.lenient().when(messageRepository.findById(4)).thenReturn(Optional.of(testMessage));
    Mockito.lenient().when(messageRepository.save(any(Message.class)))
            .thenAnswer(s -> s.getArguments()[0]);

    testMessage.setMessageId(4);
    MessageDTO messageDTO = new MessageDTO();

    messageDTO.setMessageStatus(MessageStatus.EXPIRED);
    assertEquals(MessageStatus.EXPIRED,
            this.messageService.updateMessage(messageDTO, 4).getMessageStatus());

    messageDTO.setMessageStatus(MessageStatus.DELETED);
    assertEquals(MessageStatus.DELETED,
            this.messageService.updateMessage(messageDTO, 4).getMessageStatus());

    messageDTO.setMessageStatus(MessageStatus.READ);
    assertEquals(MessageStatus.READ,
            this.messageService.updateMessage(messageDTO, 4).getMessageStatus());
  }

  @Test(expected = MessageDoesNotExistException.class)
  public void testUpdateMessageNotFound() {
    Mockito.lenient().when(messageRepository.findById(4)).thenReturn(Optional.empty());
    testMessage.setMessageId(4);
    MessageDTO messageDTO = new MessageDTO();

    messageDTO.setMessageStatus(MessageStatus.EXPIRED);

    this.messageService.updateMessage(messageDTO, 4);
  }

  @Test
  public void testFindMessageThatExists() {
    given(messageRepository.findById(testMessage.getMessageId()))
            .willReturn(Optional.of(testMessage));
    assertEquals(messageService.findMessageById(testMessage.getMessageId()), testMessage);
  }

  @Test(expected = MessageDoesNotExistException.class)
  public void testFindMessageThatDoesNotExist() {
    given(messageRepository.findById(testMessage.getMessageId())).willReturn(Optional.empty());
    messageService.findMessageById(testMessage.getMessageId());
  }

  @Test
  public void testGetMessagesForRecievingUser() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("123456789").build();
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(bob));
    given(messageRepository.findByToUserId(0))
            .willReturn(Arrays.asList(this.testMessage3, this.testMessage5, this.testMessage1));
    assertEquals(this.messageService.findMessagesForReceivingUser("bob", true),
            Arrays.asList(this.testMessage1, this.testMessage3, this.testMessage5));
  }

  @Test
  public void testGetMessagesForBetweenTwoUsers() throws UserDoesNotExistException {
    given(messageRepository.findByFromUserIdAndToUserId(2, 1)).willReturn(
            new ArrayList<>(Arrays.asList(this.testMessage1, this.testMessage3, this.testMessage5)));
    given(messageRepository.findByFromUserIdAndToUserId(1, 2)).willReturn(
            new ArrayList<>(Arrays.asList(this.testMessage, this.testMessage2, this.testMessage4)));
    User user1 = mock(User.class);
    User user2 = mock(User.class);
    given(user1.getUserID()).willReturn(1);
    given(user2.getUserID()).willReturn(2);
    given(userRepository.findByUsername("james")).willReturn(Optional.of(user1));
    given(userRepository.findByUsername("jamie")).willReturn(Optional.of(user2));

    assertEquals(this.messageService.findMessagesBetweenTwoUsers("james", "jamie", true), new ArrayList<>(
            Arrays.asList(testMessage, testMessage1, testMessage2, testMessage3, testMessage4,
                    testMessage5)));

    given(messageRepository.findByFromUserIdAndToUserId(2, 1)).willReturn(
            new ArrayList<>(Arrays.asList(this.testMessage3, this.testMessage5, this.testMessage1)));
    given(messageRepository.findByFromUserIdAndToUserId(1, 2)).willReturn(
            new ArrayList<>(Arrays.asList(this.testMessage4, this.testMessage, this.testMessage2)));
    assertEquals(this.messageService.findMessagesBetweenTwoUsers("james", "jamie", true), new ArrayList<>(
            Arrays.asList(testMessage, testMessage1, testMessage2, testMessage3, testMessage4,
                    testMessage5)));
  }

  @Test(expected = ConversationNotFoundException.class)
  public void testGetMessagesForBetweenTwoUsersNotFound() throws UserDoesNotExistException {
    User user1 = mock(User.class);
    User user2 = mock(User.class);
    given(user1.getUserID()).willReturn(1);
    given(user2.getUserID()).willReturn(2);
    given(userRepository.findByUsername("james")).willReturn(Optional.of(user1));
    given(userRepository.findByUsername("jamie")).willReturn(Optional.of(user2));
    given(messageRepository.findByFromUserIdAndToUserId(2, 1)).willReturn(new ArrayList<>());
    given(messageRepository.findByFromUserIdAndToUserId(1, 2)).willReturn(new ArrayList<>());
    this.messageService.findMessagesBetweenTwoUsers("james", "jamie", true);
  }

  @Test
  public void getUnsetMessages() throws UserDoesNotExistException {
    Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(3);
    given(user1.getLogOutTimestamp()).willReturn(ts);
    given(userRepository.findByUsername("jamie")).willReturn(Optional.of(user1));
    Message message1 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message2 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message3 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message4 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    given(messageRepository.findByToUserId(3))
            .willReturn(new ArrayList<>(Arrays.asList(message1, message2, message3, message4)));
    given(messageRepository.fetchUnreadMessages(user1.getUserID()))
            .willReturn(new ArrayList<>(Arrays.asList(message1, message2, message3, message4)));
    assertEquals(new ArrayList<>(Arrays.asList(message1, message2, message3, message4)),
            messageService.getUnsentMessages("jamie", true));

  }

  @Test(expected = UserDoesNotExistException.class)
  public void getUnsetMessagesUserNotFound() throws UserDoesNotExistException {
    Message message1 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message2 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message3 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message4 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    given(messageRepository.findByToUserId(3))
            .willReturn(new ArrayList<>(Arrays.asList(message1, message2, message3, message4)));
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(3);
    given(userRepository.findByUsername("jamie")).willReturn(Optional.empty());
    assertEquals(new ArrayList<>(Arrays.asList(message1, message2, message3, message4)),
            messageService.getUnsentMessages("jamie", true));
  }

  @Test
  public void getUnsetMessagesNoMessages() throws UserDoesNotExistException {
    given(messageRepository.findByToUserId(3)).willReturn(new ArrayList<>());
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(3);
    given(userRepository.findByUsername("jamie")).willReturn(Optional.of(user1));
    assertTrue(messageService.getUnsentMessages("jamie", true).isEmpty());
  }

  @Test
  public void getUnsetMessagesNoDeliveredMessages() throws UserDoesNotExistException {
    Message message1 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message2 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message3 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    Message message4 = Message.messageBuilder().setFromUserId(1).setToUserId(3)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now())).build();
    given(messageRepository.findByToUserId(3))
            .willReturn(new ArrayList<>(Arrays.asList(message1, message2, message3, message4)));
    Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(3);
    given(user1.getLogOutTimestamp()).willReturn(ts);
    given(userRepository.findByUsername("jamie")).willReturn(Optional.of(user1));
    assertTrue(messageService.getUnsentMessages("jamie", true).isEmpty());
  }

  @Test
  public void testGetMessagesCount() throws UserDoesNotExistException {
    given(messageRepository.findByFromUserIdAndToUserId(2, 1)).willReturn(
            new ArrayList<>(Arrays.asList(this.testMessage1, this.testMessage3, this.testMessage5)));
    given(messageRepository.findByFromUserIdAndToUserId(1, 2)).willReturn(
            new ArrayList<>(Arrays.asList(this.testMessage, this.testMessage2, this.testMessage4)));
    User user1 = mock(User.class);
    User user2 = mock(User.class);
    given(user1.getUserID()).willReturn(1);
    given(user2.getUserID()).willReturn(2);
    given(userRepository.findByUsername("james")).willReturn(Optional.of(user1));
    given(userRepository.findByUsername("jamie")).willReturn(Optional.of(user2));
    given(messageRepository.findNewMessages(1, 2)).willReturn(new ArrayList<>(
            Arrays.asList(testMessage, testMessage1, testMessage2, testMessage3, testMessage4,
                    testMessage5)));

    assertEquals(6, this.messageService.getNewMessageCount("james", "jamie"));
  }


  private void createTestMessages() {
    this.emptyMessage = Message.messageBuilder().build();
    this.testMessage = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(true)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    this.testMessage1 = Message.messageBuilder()
            .setMessageId(2)
            .setSourceMessageId(1)
            .setMessageContent("more content")
            .setFromUserId(2)
            .setToUserId(1)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    this.testMessage2 = Message.messageBuilder()
            .setMessageId(3)
            .setSourceMessageId(1)
            .setMessageContent("hi")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    this.testMessage3 = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("hi back")
            .setFromUserId(2)
            .setToUserId(1)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    this.testMessage4 = Message.messageBuilder()
            .setMessageId(5)
            .setSourceMessageId(1)
            .setMessageContent("how are you")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    this.testMessage5 = Message.messageBuilder()
            .setMessageId(6)
            .setSourceMessageId(1)
            .setMessageContent("good, and you?")
            .setFromUserId(2)
            .setToUserId(1)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();

    Message testMessageExpired = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.EXPIRED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    Message testMessageRead = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.READ)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();
    Message testMessageDeleted = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELETED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(
                    attachment)))
            .build();

  }

  @Test
  public void testSearchMessagesWithHashtag() {
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(1);
    Message message = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("#wfh")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELETED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(true)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(
                                    new HashSet<>(Arrays.asList(this.emptyMessage)))))).build();
    when(groupService.getGroupsForUser(any())).thenReturn(Collections.emptyList());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
    when(hashTagRepository.findMessagesByHashtagString(anyString())).thenReturn(
            Collections.singletonList(message));
    assertTrue(messageService.findMessagesByHashtag("wfh", "Bob", true).contains(message));
  }


  @Test
  public void testSearchMessagesWithHashtag3() {
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();

    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    Message message = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("#wfh")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELETED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(true)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(true)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(
                                    new HashSet<>(Arrays.asList(this.emptyMessage)))))).build();
    when(groupService.getGroupsForUser(any())).thenReturn(Collections.singletonList(group));
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(bob));
    when(hashTagRepository.findMessagesByHashtagString(anyString())).thenReturn(
            Collections.singletonList(message));
    assertTrue(messageService.findMessagesByHashtag("wfh", "Bob", true).contains(message));
  }

  @Test
  public void testSearchMessagesWithHashtag2() {
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(1);
    when(groupService.getGroupsForUser(any())).thenReturn(Collections.emptyList());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    assertEquals(0,
            messageService.findMessagesByHashtag("wfh", "Bob", true).size());
  }

  @Test
  public void testExcludeExpiredMessages() throws UserDoesNotExistException {
    User user1 = mock(User.class);
    given(user1.getUserID()).willReturn(1);
    Message message = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("#wfh")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELETED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(true)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(true)
            .setIsEncryptedMessage(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.of(2010, Month.APRIL, 21, 12, 30)))
            .setHashtags(new HashSet(Arrays.asList(
                    HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(
                                    new HashSet<>(Arrays.asList(this.emptyMessage)))))).build();

    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
    List<Message> result = new ArrayList<>();
    result.add(message);
    when(messageRepository.fetchUnreadMessages(anyInt())).thenReturn(result);
    assertTrue(messageService.getUnsentMessages("bob", true).isEmpty());
  }

  @Test
  public void testMessageFiltering() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("123456789").build();
    bob.setUserID(1);
    Filter filter = new Filter();
    filter.setFilterString("spam");
    Message message = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("SpAm")
            .setFromUserId(2)
            .setToUserId(1)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setIsBroadcastMessage(true)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(true)
            .setIsEncryptedMessage(false)
            .build();
    bob.getFilters().add(filter);
    when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
    List<Message> messageList = new ArrayList<>();
    messageList.add(message);
    when(messageRepository.findByToUserId(1)).thenReturn(messageList);
    assertTrue(messageService.findMessagesForReceivingUser("bob", false).isEmpty());
  }

  @Test(expected = UserDoesNotExistException.class)
  public void testNegativeMessageFiltering() throws UserDoesNotExistException {
    when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
    messageService.findMessagesForReceivingUser("bob", false);
    fail("Should have thrown an exception");
  }

  @Test
  public void testUpdateHashTagSearchHits() {
    HashTag hashTag = HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
            .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build();
    hashTag.setSearchHits(0);
    when(hashTagRepository.save(hashTag)).thenReturn(hashTag);
    messageService.updateHashTagSearchHits(hashTag);
    assertEquals(1, hashTag.getSearchHits());
  }

  @Test
  public void testGetMessagesInThread() {
    Message message = Message.messageBuilder()
            .setMessageId(3)
            .setSourceMessageId(1)
            .setMessageContent("hi")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(attachment)))
            .build();

    when(messageRepository.findAllBySourceMessageIdOrderByGeneratedTime(anyInt())).thenReturn(Collections.singletonList(message));
    assertEquals(1, messageService.getMessagesForThread(1).size());
    assertTrue(messageService.getMessagesForThread(1).contains(message));
  }
}
