package com.neu.prattle.model;


import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the Message class.
 */
public class MessageTest {

  private static final String NOT_SET = "Not set";
  private static final String DAN_SMITH = "Dan Smith";
  private static final String DAVE_SMITH = "Dave Smith";
  private static final String MESSAGE_CONTENT = "Hi Dave!";
  private static final String USER_NAME = "James";

  private Message emptyMessage;
  private Message message;
  private Message equalMessage;
  private MessageAttachment attachment;

  @Before
  public void setUp() {
    Timestamp calendar = Timestamp.valueOf(LocalDateTime.now());
    this.emptyMessage = Message.messageBuilder().build();
    attachment = new MessageAttachment();
    attachment.setMessage(message);
    attachment.setFileID(1);
    attachment.setWebUrl("www.googledrive.com");
    this.message = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(calendar)
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet<>(Collections.singletonList(attachment)))
            .setEncryptionString("password")
            .build();
    this.equalMessage = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(calendar)
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet<>(Collections.singletonList(attachment)))
            .setEncryptionString("password")
            .build();

  }


  /**
   * Test set and get message id.
   */
  @Test
  public void testSetAndGetMessageId() {
    message.setMessageId(1);
    assertEquals(1, message.getMessageId());
    this.equalMessage.setMessageId(1);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setMessageId(2);
    assertFalse(this.message.equals(this.equalMessage));
    for (int i = 0; i < 100; i++) {
      int randomId = new Random().nextInt(Integer.MAX_VALUE);
      message.setMessageId(randomId);
      assertEquals(randomId, message.getMessageId());
    }
    message.setMessageId(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, message.getMessageId());
  }

  /**
   * Test setting message id to invalid number.
   */
  @Test

  public void testSetMessageIdToInvalidId() {
    int exceptionCount = 0;
    try {
      message.setMessageId(0);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    try {
      message.setMessageId(new Random().nextInt(Integer.MAX_VALUE) * -1);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    assertEquals(2, exceptionCount);
  }

  /**
   * Test set and get message source message id.
   */

  @Test
  public void testSetAndGetMessageSourceMessageId() {
    message.setSourceMessageId(1);
    assertEquals(1, message.getSourceMessageId());
    this.equalMessage.setSourceMessageId(1);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setSourceMessageId(2);
    assertFalse(this.message.equals(this.equalMessage));
    for (int i = 0; i < 100; i++) {
      int randomId = new Random().nextInt(Integer.MAX_VALUE);
      message.setSourceMessageId(randomId);
      assertEquals(message.getSourceMessageId(), randomId);
    }
    message.setSourceMessageId(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, message.getSourceMessageId());
  }

  /**
   * Test setting message source message id to invalid number .
   */

  @Test

  public void testSetMessageSourceMessageIdToInvalidId() {
    int exceptionCount = 0;
    try {
      message.setSourceMessageId(0);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    try {
      message.setSourceMessageId(new Random().nextInt(Integer.MAX_VALUE) * -1);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    assertEquals(2, exceptionCount);
  }


  /**
   * Test setContent and getContent.
   */
  @Test
  public void testMessageSetContentGetContent() {
    message.setContent("Content");
    assertEquals("Content", message.getContent());
    this.equalMessage.setContent("Content");
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setContent("2");
    assertFalse(this.message.equals(this.equalMessage));

  }


  /**
   * Test set and get from user id .
   */
  @Test
  public void testSetAndGetFromUserId() {
    message.setFromUserId(1);
    assertEquals(1, message.getFromUserId());
    this.equalMessage.setFromUserId(1);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setFromUserId(2);
    assertFalse(this.message.equals(this.equalMessage));
    for (int i = 0; i < 100; i++) {
      int randomId = new Random().nextInt(Integer.MAX_VALUE);
      message.setFromUserId(randomId);
      assertEquals(message.getFromUserId(), randomId);
    }
    message.setFromUserId(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, message.getFromUserId());
  }

  /**
   * Test setting invalid from user id.
   */
  @Test
  public void testSetInvalidFromUserId() {
    int exceptionCount = 0;
    try {
      message.setFromUserId(0);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    try {
      message.setFromUserId(new Random().nextInt(Integer.MAX_VALUE) * -1);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    assertEquals(2, exceptionCount);
  }

  /**
   * Test set and get to user id .
   */
  @Test
  public void testSetAndGetToUserId() {
    message.setToUserId(1);
    assertEquals(1, message.getToUserId());
    this.equalMessage.setToUserId(1);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setToUserId(2);
    assertFalse(this.message.equals(this.equalMessage));
    for (int i = 0; i < 100; i++) {
      int randomId = new Random().nextInt(Integer.MAX_VALUE);
      message.setToUserId(randomId);
      assertEquals(message.getToUserId(), randomId);
    }
    message.setToUserId(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, message.getToUserId());
  }

  /**
   * Test setting invalid to user id.
   */
  @Test
  public void testSetInvalidToUserId() {
    int exceptionCount = 0;
    try {
      message.setToUserId(0);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    try {
      message.setToUserId(new Random().nextInt(Integer.MAX_VALUE) * -1);
    } catch (IllegalArgumentException ex) {
      exceptionCount++;
    }
    assertEquals(2, exceptionCount);
  }

  /**
   * Test set and get message status.
   */
  @Test
  public void testSetAndGetMessageStatus() {
    message.setMessageStatus(MessageStatus.DELETED);
    assertEquals(MessageStatus.DELETED, message.getMessageStatus());
    this.equalMessage.setMessageStatus(MessageStatus.DELETED);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setMessageStatus(MessageStatus.EXPIRED);
    assertFalse(this.message.equals(this.equalMessage));


    message.setMessageStatus(MessageStatus.DELIVERED);
    assertEquals(MessageStatus.DELIVERED, message.getMessageStatus());
    this.equalMessage.setMessageStatus(MessageStatus.DELIVERED);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setMessageStatus(MessageStatus.EXPIRED);
    assertFalse(this.message.equals(this.equalMessage));


    message.setMessageStatus(MessageStatus.EXPIRED);
    assertEquals(MessageStatus.EXPIRED, message.getMessageStatus());
    this.equalMessage.setMessageStatus(MessageStatus.EXPIRED);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setMessageStatus(MessageStatus.DELETED);
    assertFalse(this.message.equals(this.equalMessage));


    message.setMessageStatus(MessageStatus.READ);
    assertEquals(MessageStatus.READ, message.getMessageStatus());
    this.equalMessage.setMessageStatus(MessageStatus.READ);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setMessageStatus(MessageStatus.DELETED);
    assertFalse(this.message.equals(this.equalMessage));
  }


  /**
   * Test set and get message subject.
   */
  @Test
  public void testSetAndGetMessageSubject() {
    message.setMessageSubject("Subject");
    assertEquals("Subject", message.getMessageSubject());
    this.equalMessage.setMessageSubject("Subject");
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setMessageSubject("content");
    assertFalse(this.message.equals(this.equalMessage));
  }

  /**
   * Test set and get message hasAttachment.
   */
  @Test
  public void testSetAndGetMessageHasAttachment() {
    assertFalse(message.hasAttachment());
    message.setHasAttachment(true);
    assertTrue(message.hasAttachment());
    this.equalMessage.setHasAttachment(true);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setHasAttachment(false);
    assertFalse(this.message.equals(this.equalMessage));


    message.setHasAttachment(false);
    assertFalse(message.hasAttachment());
    this.equalMessage.setHasAttachment(false);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setHasAttachment(true);
    assertFalse(this.message.equals(this.equalMessage));

  }

  /**
   * Test set and get message generatedTime.
   */
  @Test
  public void testSetAndGetMessageGeneratedTime() {
    Timestamp calendar = Timestamp.valueOf(LocalDateTime.now());
    message.setGeneratedTime(calendar);
    assertEquals(calendar, message.getGeneratedTime());
    this.equalMessage.setGeneratedTime(calendar);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setGeneratedTime(null);
    assertFalse(this.message.equals(this.equalMessage));
  }


  /**
   * Test set and get message type details
   */
  @Test
  public void testSetAndGetMessageTypeDetails() {
    assertFalse(this.message.getIsBroadcastMessage());
    this.message.setIsBroadcastMessage(true);
    assertTrue(this.message.getIsBroadcastMessage());
    this.message.setIsBroadcastMessage(false);
    assertFalse(this.message.getIsBroadcastMessage());

    assertFalse(this.message.getIsPrivateMessage());
    this.message.setIsPrivateMessage(true);
    assertTrue(this.message.getIsPrivateMessage());
    this.message.setIsPrivateMessage(false);
    assertFalse(this.message.getIsPrivateMessage());

    assertFalse(this.message.getIsGroupMessage());
    this.message.setIsGroupMessage(true);
    assertTrue(this.message.getIsGroupMessage());
    this.message.setIsGroupMessage(false);
    assertFalse(this.message.getIsGroupMessage());

    assertFalse(this.message.getIsForwardedMessage());
    this.message.setIsForwardedMessage(true);
    assertTrue(this.message.getIsForwardedMessage());
    this.message.setIsForwardedMessage(false);
    assertFalse(this.message.getIsForwardedMessage());

    assertFalse(this.message.getIsSelfDestructMessage());
    this.message.setIsSelfDestructMessage(true);
    assertTrue(this.message.getIsSelfDestructMessage());
    this.message.setIsSelfDestructMessage(false);
    assertFalse(this.message.getIsSelfDestructMessage());

    assertFalse(this.message.getIsEncryptedMessage());
    this.message.setIsEncryptedMessage(true);
    assertTrue(this.message.getIsEncryptedMessage());
    this.message.setIsEncryptedMessage(false);
    assertFalse(this.message.getIsEncryptedMessage());
  }

  /**
   * Test set an get message hashtags.
   */
  @Test
  public void testSetAndGetMessageHashtags() {
    Set<HashTag> hashTags = this.message.getHashTagSet();
    assertTrue(hashTags.toArray()[0].equals(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build()));

    this.equalMessage.setHashTagSet(hashTags);
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setHashTagSet(null);
    assertFalse(this.message.equals(this.equalMessage));

    hashTags.add(HashTag.hashTagBuilder().setHashTagId(2).setHashTagValue("NU").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build());
    this.message.setHashTagSet(hashTags);
    assertTrue(this.message.getHashTagSet().contains(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build()));
    assertTrue(this.message.getHashTagSet().contains(HashTag.hashTagBuilder().setHashTagId(2).setHashTagValue("NU").setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build()));

  }

  @Test
  public void testSetAndGetMessageEncryptionString() {
    message.setEncryptionString("string");
    assertEquals("string", message.getEncryptionString());
    this.equalMessage.setEncryptionString("string");
    assertTrue(this.message.equals(this.equalMessage));
    this.equalMessage.setEncryptionString("newstring");
    assertFalse(this.message.equals(this.equalMessage));
  }


  /**
   * Test message to string.
   */
  @Test
  public void testToString() {
    Timestamp calendar = Timestamp.valueOf(LocalDateTime.now());
    this.message.setGeneratedTime(calendar);
    assertEquals("message_id: 0, source_message_id: 0, content: null, from_user_id: 0, to_user_id: 0, " +
                    "message_status: null, message_subject: null, hasAttachement: false, generatedTime: null, " +
                    "isBroadcastMessage: false, isPrivateMessage: false, isGroupMessage: false, isForwardedMessage: " +
                    "false, isSelfDestructMessage: false, isEncryptedMessage: false, hashtags: null, attachments: " +
                    "null, encryptionString: null",
            this.emptyMessage.toString());

    assertEquals("message_id: 1, source_message_id: 1, content: content, from_user_id: 1, to_user_id: 2, " +
                    "message_status: DELIVERED, message_subject: subject, hasAttachement: false, generatedTime: " +
                    calendar.toString() + ", isBroadcastMessage: false, isPrivateMessage: false, isGroupMessage: " +
                    "false, isForwardedMessage: false, isSelfDestructMessage: false, isEncryptedMessage: false, " +
                    "hashtags: [HashTag{hashtagId=1, hashtagString='wfh', searchHits=0, messages=[message_id: 0, " +
                    "source_message_id: 0, content: null, from_user_id: 0, to_user_id: 0, message_status: null, " +
                    "message_subject: null, hasAttachement: false, generatedTime: null, isBroadcastMessage: false, " +
                    "isPrivateMessage: false, isGroupMessage: false, isForwardedMessage: false, isSelfDestructMessage: " +
                    "false, isEncryptedMessage: false, hashtags: null, attachments: null, encryptionString: null]}], " +
                    "attachments: [MessageAttachment{fileID=1, webUrl='www.googledrive.com'}], encryptionString: " +
                    "password",
            this.message.toString());
  }


  /**
   * Test message equals and hashcode
   */
  @Test
  public void testMessageEqualsAndHashCode() {
    assertFalse(this.emptyMessage.equals(this.message));
    assertFalse(this.message.equals(this.emptyMessage));
    assertTrue(this.message.equals(this.message));
    assertTrue(this.emptyMessage.equals(this.emptyMessage));
    assertFalse(this.emptyMessage.hashCode() == this.message.hashCode());
    assertTrue(this.emptyMessage.hashCode() == this.emptyMessage.hashCode());
    assertTrue(this.message.hashCode() == this.equalMessage.hashCode());
    assertFalse(this.message.hashCode() == this.emptyMessage.hashCode());

    assertFalse(this.message.equals(this.emptyMessage));
    assertTrue(this.message.equals(this.message));
    assertTrue(this.emptyMessage.equals(this.emptyMessage));
    Message testMessageSame = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(this.message.getGeneratedTime())
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setHashtags(new HashSet(Arrays.asList(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                    .setHashTagMessages(new HashSet<>(Arrays.asList(this.emptyMessage))).build())))
            .setAttachments(new HashSet(Arrays.asList(attachment)))
            .setEncryptionString("password")
            .build();
    assertTrue(this.message.equals(testMessageSame));

    assertFalse(this.message.equals(new Object()));
    assertFalse(this.message.equals(null));
  }
}
