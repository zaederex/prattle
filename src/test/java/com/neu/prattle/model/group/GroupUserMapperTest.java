package com.neu.prattle.model.group;

import com.neu.prattle.model.User;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GroupUserMapperTest {

  private static GroupUserMapper mapper;
  private static GroupUserCompositeKey key;
  private static User user;
  private static Group group;

  @BeforeClass
  public static void setup() {
    key = new GroupUserCompositeKey(1, 2);
    group = new Group();
    user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    mapper = new GroupUserMapper(group, user, key, true, true, true);
  }

  @Test
  public void testCreateEmptyMapper() {
    GroupUserMapper mapper = new GroupUserMapper();
    assertNull(mapper.getGroup());
    assertNull(mapper.getId());
    assertNull(mapper.getUser());
  }

  @Test
  public void testCreateMapperFromConstructor() {
    GroupUserCompositeKey key = new GroupUserCompositeKey(1, 1);
    GroupUserMapper mapper = new GroupUserMapper(key, true, true, true);
    assertEquals(key, mapper.getId());
    assertTrue(mapper.isModerator());
    assertTrue(mapper.isFollower());
    assertTrue(mapper.isMember());
  }

  @Test
  public void testGettersAndSetters() {
    mapper.setId(key);
    mapper.setUser(user);
    mapper.setGroup(group);
    mapper.setFollower(false);
    mapper.setMember(true);
    mapper.setModerator(true);
    assertEquals(group, mapper.getGroup());
    assertEquals(user, mapper.getUser());
    assertEquals(key, mapper.getId());
    assertTrue(mapper.isModerator());
    assertTrue(mapper.isMember());
    assertFalse(mapper.isFollower());
  }

  @Test
  public void testEquals() {
    mapper.setFollower(true);
    assertEquals(mapper, new GroupUserMapper(group, user, key, true, true, true));
    GroupUserMapper newMapper = new GroupUserMapper(group, user, key, true, false, true);
    newMapper.setFollower(false);
    assertNotEquals(mapper, newMapper);

    assertEquals(mapper, mapper);
    assertNotEquals(mapper, null);
  }

  @Test
  public void testHashcode() {
    assertEquals(Objects.hash(mapper.getId(), mapper.getGroup().getGroupID(), mapper.getUser().getUserID(),
            mapper.isModerator(), mapper.isFollower(), mapper.isMember()), mapper.hashCode());
  }

}
