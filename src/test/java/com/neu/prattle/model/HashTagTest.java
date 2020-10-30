package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the HashTag class.
 */
public class HashTagTest {

  private HashTag hashTag;
  private HashTag equalHashTag;
  private HashTag emptyHashTag;

  @Before
  public void setUp() {
    this.emptyHashTag = HashTag.hashTagBuilder().build();
    this.hashTag = HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("hashtag")
            .setHashTagMessages(new HashSet<>(Arrays.asList(Message.messageBuilder().build()))).build();
    this.equalHashTag = HashTag.hashTagBuilder().setHashTagId(1).setHashTagValue("hashtag")
            .setHashTagMessages(new HashSet<>(Arrays.asList(Message.messageBuilder().build()))).build();
  }

  @Test
  public void testCreateHashTag() {
    assertEquals(1, this.hashTag.getHashtagId());
    assertEquals("hashtag", this.hashTag.getHashtagString());
    assertEquals(new HashSet<>(Arrays.asList(Message.messageBuilder().build())), this.hashTag.getMessages());
  }

  @Test
  public void testSetAndGetId() {
    assertEquals(1, this.hashTag.getHashtagId());
    this.hashTag.setHashtagId(4);
    assertEquals(4, this.hashTag.getHashtagId());

    this.equalHashTag.setHashtagId(4);
    assertTrue(this.hashTag.equals(this.equalHashTag));
    this.equalHashTag.setHashtagId(3);
    assertFalse(this.hashTag.equals(this.equalHashTag));

  }

  @Test
  public void testSetAndGetValue() {
    assertEquals("hashtag", this.hashTag.getHashtagString());
    this.hashTag.setHashtagString("hash");
    assertEquals("hash", this.hashTag.getHashtagString());

    this.equalHashTag.setHashtagString("hash");
    assertTrue(this.hashTag.equals(this.equalHashTag));
    this.equalHashTag.setHashtagString("hashtag");
    assertFalse(this.hashTag.equals(this.equalHashTag));
  }

  @Test
  public void testSetAndGetMessages() {
    assertEquals(new HashSet<>(Arrays.asList(Message.messageBuilder().build())), this.hashTag.getMessages());
    this.hashTag.setMessages(new HashSet<>(Arrays.asList(Message.messageBuilder().setSourceMessageId(1).build())));
    assertEquals(new HashSet<>(Arrays.asList(Message.messageBuilder().setSourceMessageId(1).build())),
            this.hashTag.getMessages());

    this.equalHashTag.setMessages(new HashSet<>(Arrays.asList(Message.messageBuilder().setSourceMessageId(1).build())));
    assertEquals(this.hashTag, this.equalHashTag);
    this.equalHashTag.setMessages(null);
    assertTrue(this.hashTag.equals(this.equalHashTag));
  }

  @Test
  public void testEqualsAndHashCode() {
    assertTrue(this.hashTag.equals(this.equalHashTag));
    assertTrue(this.hashTag.hashCode() == this.equalHashTag.hashCode());
    assertFalse(this.hashTag.equals(this.emptyHashTag));
    assertFalse(this.hashTag.hashCode() == this.emptyHashTag.hashCode());
    this.equalHashTag.setHashtagId(4);
    assertFalse(this.hashTag.equals(this.equalHashTag));
    assertFalse(this.hashTag.hashCode() == this.equalHashTag.hashCode());
    assertFalse(this.hashTag.equals(new Object()));
    assertTrue(this.hashTag.equals(hashTag));
    assertFalse(this.hashTag.equals(null));

  }
}
