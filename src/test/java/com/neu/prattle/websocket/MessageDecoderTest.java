
package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.MessageStatus;
import com.neu.prattle.model.User;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.websocket.EncodeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * A test class to test the implementation of MessageDecoder class.
 */

public class MessageDecoderTest {

  private static Message message;
  private static Message encryptedMessage;

  private static MessageEncoder messageEncoder;
  private static MessageDecoder messageDecoder;

  @BeforeClass
  public static void setupMessage() {


    User testUser1 = User.getUserBuilder().username("testUserA").password("123456789").build();
    User testUser2 = User.getUserBuilder().username("testUserB").password("123456789").build();

    message = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(false)
            .setMessageGenerationTime(Timestamp.valueOf(LocalDateTime.now()))
            .setFromUserId(1)
            .setToUserId(2)
            .build();

    encryptedMessage = Message.messageBuilder()
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
            .setIsEncryptedMessage(true)
            .build();

    message.setContent("Before Encoding");

    messageEncoder = new MessageEncoder();
    messageDecoder = new MessageDecoder();
  }

  @Test
  public void testWillDecode() throws EncodeException {
    assertTrue(messageDecoder.willDecode(messageEncoder.encode(message)));
    assertFalse(messageDecoder.willDecode(null));
    assertTrue(messageDecoder.willDecode("b"));
  }

  @Test
  public void testDecode() throws EncodeException {
    String encodedMessage = messageEncoder.encode(message);
    Message decodedMessage = messageDecoder.decode(encodedMessage);

    assertEquals(decodedMessage.getFromUserId(), message.getFromUserId());
    assertEquals(decodedMessage.getContent(), message.getContent());
    assertEquals(decodedMessage.getToUserId(), message.getToUserId());
  }

}
