package com.neu.prattle.controller;

import com.neu.prattle.dto.MessageDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.MessageAttachment;
import com.neu.prattle.model.MessageStatus;
import com.neu.prattle.model.User;
import com.neu.prattle.repository.UserRepository;
import com.neu.prattle.service.HashTagService;
import com.neu.prattle.service.MessageService;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class MessageControllerTest {

  private static class MyRuntimeException extends Exception {
    MyRuntimeException(String message) {
      super(message);
    }
  }

  private MockMvc mockMvc;

  @Mock
  private MessageService messageService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private HashTagService hashTagService;

  @InjectMocks
  private MessageController messageController;

  private Timestamp timestamp;

  private Message emptyMessage;
  private Message testMessage;
  private Message testMessage1;
  private Message testMessage2;
  private Message testMessage3;
  private Message testMessage4;
  private Message testMessage5;
  private MessageAttachment attachment;

  @Before
  public void setUp() {
    attachment = new MessageAttachment();
    attachment.setFileID(1);
    attachment.setWebUrl("www.googledrive.com");

    mockMvc = MockMvcBuilders
            .standaloneSetup(messageController)
            .build();
    createTestMessages();
    timestamp = new Timestamp(new Date().getTime());
  }

  @Test
  public void testGetChatHistory() throws UserDoesNotExistException {
    Message msg1 = Message.messageBuilder().setMessageContent("Hi! It's me! Mario!")
            .setMessageGenerationTime(timestamp).build();
    Message msg2 = Message.messageBuilder().setMessageContent("And I am Luigi!")
            .setMessageGenerationTime(timestamp).build();
    when(messageService.findMessagesBetweenTwoUsers("bob", "rob", true))
            .thenReturn(Arrays.asList(msg1, msg2));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/bob/chathistory/rob")
      ).andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Mario"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Luigi"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidGetChatHistory() throws UserDoesNotExistException {
    doThrow(UserDoesNotExistException.class).when(messageService)
            .findMessagesBetweenTwoUsers("bob", "rob", true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/bob/chathistory/rob")
      ).andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertFalse(mvcResult.getResponse().getContentAsString().contains("Mario"));
      assertFalse(mvcResult.getResponse().getContentAsString().contains("Luigi"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMessageCount() throws UserDoesNotExistException {
    when(messageService.getNewMessageCount("bob", "rob"))
            .thenReturn(2873243);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/bob/chathistory/rob/newmsgcount")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("2873243"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidGetMessageCount() throws UserDoesNotExistException {
    doThrow(UserDoesNotExistException.class).when(messageService).getNewMessageCount("bob", "rob");
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/bob/chathistory/rob/newmsgcount")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("-1"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testUpdateMessageStatus() {
    Message message = Message.messageBuilder().setMessageStatus(MessageStatus.DELETED).build();
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setMessageStatus(MessageStatus.DELETED);
    when(messageService.updateMessage(any(MessageDTO.class), anyInt()))
            .thenReturn(message);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/message/updatemessagestatus/4")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(messageDTO))).andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("DELETED"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetConversationBetweenUsers() throws UserDoesNotExistException {
    given(messageService.findMessagesBetweenTwoUsers("james", "jamie", false))
            .willReturn(new ArrayList<>(Arrays.asList(testMessage, testMessage1, testMessage2, testMessage3, testMessage4, testMessage5)));

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/conversation/james/jamie"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetConversationNoMessages() throws UserDoesNotExistException {
    given(messageService.findMessagesBetweenTwoUsers("james", "jamie", true))
            .willReturn(new ArrayList<>());

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/conversation/james/jamie"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertEquals("[]", mvcResult.getResponse().getContentAsString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetConversationUserDoesNotExist() throws UserDoesNotExistException {
    given(messageService.findMessagesBetweenTwoUsers("james", "jamie", true))
            .willThrow(UserDoesNotExistException.class);

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/conversation/james/jamie"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertEquals("[]", mvcResult.getResponse().getContentAsString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private String createPostJsonBody(Object o) throws MyRuntimeException {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(o);
    } catch (IOException e) {
      throw new MyRuntimeException("Could not create json string for object");
    }
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
    this.testMessage1 = Message.messageBuilder()
            .setMessageId(2)
            .setSourceMessageId(1)
            .setMessageContent("more content")
            .setFromUserId(2)
            .setToUserId(1)
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
    this.testMessage2 = Message.messageBuilder()
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
    this.testMessage3 = Message.messageBuilder()
            .setMessageId(4)
            .setSourceMessageId(1)
            .setMessageContent("hi back")
            .setFromUserId(2)
            .setToUserId(1)
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
    this.testMessage4 = Message.messageBuilder()
            .setMessageId(5)
            .setSourceMessageId(1)
            .setMessageContent("how are you")
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
    this.testMessage5 = Message.messageBuilder()
            .setMessageId(6)
            .setSourceMessageId(1)
            .setMessageContent("good, and you?")
            .setFromUserId(2)
            .setToUserId(1)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setIsBroadcastMessage(true)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(attachment)))
            .build();

  }

  @Test
  public void testHashtagSearch() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    user.setUserID(1);
    HashTag wfh = HashTag.hashTagBuilder().setHashTagId(1)
            .setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build();
    this.testMessage = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content #wfh")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setIsBroadcastMessage(true)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(wfh)))
            .setAttachments(new HashSet(Arrays.asList(attachment)))
            .build();
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(hashTagService.getHashTag(anyString())).thenReturn(Optional.of(wfh));
    when(messageService.findMessagesByHashtag(anyString(), anyString(), anyBoolean())).thenReturn(Collections.singletonList(testMessage));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/search/wfh/Joe"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("#wfh"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testInvalidHashtagSearch() {
    HashTag wfh = HashTag.hashTagBuilder().setHashTagId(1)
            .setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build();
    this.testMessage = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content #wfh")
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
            .setHashtags(new HashSet(Arrays.asList(wfh)))
            .setAttachments(new HashSet(Arrays.asList(attachment)))
            .build();
    when(hashTagService.getHashTag("wfhs")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/search/wfh/Joe"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertFalse(mvcResult.getResponse().getContentAsString().contains("#wfhs"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testSearchTopHashTags() {
    HashTag wfh = HashTag.hashTagBuilder().setHashTagId(1)
            .setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Collections.singletonList(this.emptyMessage))).build();
    when(hashTagService.getTopHashTags()).thenReturn(Collections.singletonList(wfh));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/search/tophashtags"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("wfh"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
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
    when(messageService.getMessagesForThread(anyInt())).thenReturn(Collections.singletonList(message));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/message/thread/1"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("hi"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
