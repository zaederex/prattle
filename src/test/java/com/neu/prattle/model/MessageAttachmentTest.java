package com.neu.prattle.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class MessageAttachmentTest {

  private static Message message;
  private static MessageAttachment messageAttachment;

  @BeforeClass
  public static void setup() {
    messageAttachment = new MessageAttachment();
    messageAttachment.setWebUrl("test@url.com");
    messageAttachment.setFileID(2);
    User user1 = User.getUserBuilder()
            .username("joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();

    User user2 = User.getUserBuilder()
            .username("dannyb")
            .password("123455678")
            .firstName("Danny")
            .lastName("Boyles")
            .contactNumber("8578578576")
            .build();

    message = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("Hey there!")
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
            .setHashtags(new HashSet(
                    Collections.singletonList(HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("wfh")
                            .setHashTagMessages(new HashSet<>(
                                    Collections.singletonList(Message.messageBuilder().build())))
                            .build())))
            .setAttachments(new HashSet(Collections.singletonList(
                    messageAttachment)))
            .build();
    messageAttachment.setMessage(message);
    assertEquals(1, messageAttachment.getMessage().getMessageId());
    assertEquals(2, messageAttachment.getFileID());
    assertEquals(message, messageAttachment.getMessage());
    assertEquals("test@url.com", messageAttachment.getWebUrl());

  }

  @Test
  public void testCreateMessageAttachment() {
    MessageAttachment messageAttachment = new MessageAttachment();
    messageAttachment = new MessageAttachment();
    messageAttachment.setMessage(message);
    messageAttachment.setWebUrl("test@url.com");
    messageAttachment.setFileID(2);

    messageAttachment.setMessage(message);
    assertEquals(1, messageAttachment.getMessage().getMessageId());
    assertEquals(2, messageAttachment.getFileID());
    assertEquals(message, messageAttachment.getMessage());
    assertEquals("test@url.com", messageAttachment.getWebUrl());
  }

  @Test
  public void testCreateMessageAttachmentFromConstructor() {
    MessageAttachment messageAttachment = new MessageAttachment();
    assertNull(messageAttachment.getMessage());
    assertNull(messageAttachment.getWebUrl());
  }

  @Test
  public void testSetters() {
    MessageAttachment messageAttachment = new MessageAttachment();
    messageAttachment.setFileID(3);
    message.setContent("new content");
    messageAttachment.setMessage(message);
    messageAttachment.setWebUrl("new@url.com");
    messageAttachment = new MessageAttachment();
    messageAttachment.setMessage(message);
    messageAttachment.setWebUrl("test@url.com");
    messageAttachment.setFileID(2);

    assertEquals("new content", messageAttachment.getMessage().getContent());
    assertEquals(1, messageAttachment.getMessage().getMessageId());
    assertEquals(2, messageAttachment.getFileID());
    assertEquals("test@url.com", messageAttachment.getWebUrl());
  }

  @Test
  public void testEquals() {
    MessageAttachment newMessageAttachment =
            new MessageAttachment();

    assertFalse(messageAttachment.equals(newMessageAttachment));
    newMessageAttachment.setWebUrl("test@url.com");
    newMessageAttachment.setMessage(message);
    assertFalse(messageAttachment.equals(newMessageAttachment));
    assertFalse(messageAttachment.equals(new Object()));
    newMessageAttachment.setFileID(2);
    newMessageAttachment.setWebUrl("newUrl.com");
    assertFalse(messageAttachment.equals(newMessageAttachment));
    newMessageAttachment.setFileID(2);
    newMessageAttachment.setWebUrl("test@url.com");
    newMessageAttachment.setMessage(Message.messageBuilder().build());
    assertFalse(messageAttachment.equals(newMessageAttachment));

  }
}
