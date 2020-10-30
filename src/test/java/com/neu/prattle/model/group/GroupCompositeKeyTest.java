package com.neu.prattle.model.group;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GroupCompositeKeyTest {

  @Test
  public void testCreateEmptyKey() {
    GroupUserCompositeKey key = new GroupUserCompositeKey();
    assertEquals(0, key.getGroupId());
    assertEquals(0, key.getUserId());
  }

  @Test
  public void testCreateKeyFromConstructor() {
    GroupUserCompositeKey key = new GroupUserCompositeKey(1, 2);
    assertEquals(2, key.getUserId());
    assertEquals(1, key.getGroupId());
  }

  @Test
  public void testGettersAndSetters() {
    GroupUserCompositeKey key = new GroupUserCompositeKey();
    key.setGroupId(3);
    key.setUserId(1);

    assertEquals(1, key.getUserId());
    assertEquals(3, key.getGroupId());
  }

  @Test
  public void testEquals() {
    GroupUserCompositeKey key1 = new GroupUserCompositeKey(1, 2);
    GroupUserCompositeKey key2 = new GroupUserCompositeKey(1, 2);
    GroupUserCompositeKey key3 = new GroupUserCompositeKey(2, 4);

    assertEquals(key1, key2);
    assertEquals(key2, key2);
    assertNotEquals(key3, key1);
    assertNotEquals(key1, null);
  }

}
