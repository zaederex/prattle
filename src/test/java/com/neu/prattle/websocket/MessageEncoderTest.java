package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;
import com.neu.prattle.model.MessageAttachment;
import com.neu.prattle.model.MessageStatus;
import com.neu.prattle.model.User;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.websocket.EncodeException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


/**
 * A test class to test the implementation of MessageEncoder class.
 */


public class MessageEncoderTest {
  private static User testUser1;
  private static User testUser2;
  private static Message message;
  private static Message message1;
  private static Message encryptedMessage;

  private static MessageEncoder messageEncoder;
  private static MessageDecoder messageDecoder;
  private static MessageAttachment attachment;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeClass
  public static void setupMessage() {
    attachment = new MessageAttachment();
    attachment.setFileID(1);
    attachment.setWebUrl("www.googledrive.com");

    testUser1 = User.getUserBuilder()
            .username("testUser1")
            .password("123456789")
            .build();
    testUser2 = User.getUserBuilder().username("testUser2").password("123456789").build();

    message = new Message();

    message.setFromUserId(4);
    message.setContent("Before Encoding");
    message.setToUserId(5);
    message1 = Message.messageBuilder()
            .setMessageId(1)
            .setSourceMessageId(1)
            .setMessageContent("content")
            .setFromUserId(1)
            .setToUserId(2)
            .setMessageStatus(MessageStatus.DELIVERED)
            .setMessageSubject("subject")
            .setMessageHasAttachment(true)
            .setIsBroadcastMessage(false)
            .setIsPrivateMessage(false)
            .setIsGroupMessage(false)
            .setIsForwardedMessage(false)
            .setIsSelfDestructMessage(false)
            .setIsEncryptedMessage(false)
            .setAttachments(new HashSet(Arrays.asList(attachment)))
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

    messageEncoder = new MessageEncoder();
    messageDecoder = new MessageDecoder();
  }

  @Test
  public void testEncode() throws EncodeException {
    assertEquals("{\"messageId\":0,\"sourceMessageId\":0,\"content\":\"Before Encoding\","
                    + "\"fromUserId\":4,\"toUserId\":5,\"messageStatus\":null,\"messageSubject\":null,"
                    + "\"generatedTime\":null,\"isBroadcastMessage\":false,\"isPrivateMessage\":false,"
                    + "\"isGroupMessage\":false,\"isForwardedMessage\":false,\"isSelfDestructMessage\":false,"
                    + "\"isEncryptedMessage\":false,\"attachments\":null,\"encryptionString\":null}",
            messageEncoder.encode(message));
  }

  /**
   * Tests invalid encoding.
   *
   * @throws IOException expected
   */

  @Test
  public void testInvalidEncode() throws IOException {
    Message message = mock(Message.class);
    ObjectMapper mapper = mock(ObjectMapper.class);
    MessageEncoder encoder = spy(MessageEncoder.class);
    when(mapper.writeValueAsString(message)).thenThrow(IOException.class);
    assertEquals("{}", encoder.encode(message));
  }
}

