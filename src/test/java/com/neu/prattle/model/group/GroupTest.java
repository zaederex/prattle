package com.neu.prattle.model.group;

import com.neu.prattle.model.User;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the Group class.
 */
public class GroupTest {

  private static Group group;
  private static List<GroupUserMapper> mappings;

  @BeforeClass
  public static void setUp() {
    User testUser1 = User.getUserBuilder()
            .username("testName1")
            .password("testpass")
            .firstName("testo")
            .lastName("testee")
            .contactNumber("123456789")
            .timezone("UTC")
            .build();
    User testUser2 = User.getUserBuilder()
            .username("testName2")
            .password("testpass")
            .firstName("testo")
            .lastName("testee")
            .contactNumber("123456789")
            .timezone("UTC")
            .build();
    User testUser3 = User.getUserBuilder()
            .username("testName3")
            .password("testpass")
            .firstName("testo")
            .lastName("testee")
            .contactNumber("123456789")
            .timezone("UTC")
            .build();

    mappings = new ArrayList<>();
    GroupUserMapper groupUserMapper = new GroupUserMapper(null, testUser1,
            new GroupUserCompositeKey(1, 2),
            true, false, false);
    mappings.add(new GroupUserMapper());
    mappings.add(new GroupUserMapper());
    mappings.add(new GroupUserMapper());

    group = Group.getBuilder()
            .name("testGroup")
            .password("testpassgroup")
            .users(mappings)
            .description("test group")
            .email("test@email")
            .build();
  }

  @Test
  public void testCreateGroupFromConstructor() {
    Group group = new Group();
    assertNull(group.getGroupName());
    assertNull(group.getPassword());
    assertNull(group.getDescription());
    assertNull(group.getGroupEmail());
//    assertNull(group.getMembers());
    assertEquals(0, group.getGroupID());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEmptyGroup() {
    Group.getBuilder().build();
    fail();
  }

  @Test(expected = NullPointerException.class)
  public void testCreateGroupNameNull() {
    Group.getBuilder()
            .name(null)
            .password("12345678")
            .users(mappings)
            .description("test")
            .email("testemail")
            .build();
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGroupNameEmpty() {
    Group.getBuilder()
            .name("")
            .password("12345678")
            .users(mappings)
            .description("test")
            .email("testemail")
            .build();
    fail();
  }

  @Test(expected = NullPointerException.class)
  public void testCreateGroupPasswordNull() {
    Group.getBuilder()
            .name("Neel")
            .password(null)
            .users(mappings)
            .description("test")
            .email("testemail")
            .build();
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGroupPasswordEmpty() {
    Group.getBuilder()
            .name("Neel")
            .password("")
            .users(mappings)
            .description("test")
            .email("testemail")
            .build();
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGroupPasswordTooShort() {
    Group.getBuilder()
            .name("Neel")
            .password("1234567")
            .users(mappings)
            .description("test")
            .email("testemail")
            .build();
    fail();
  }

  @Test(expected = NullPointerException.class)
  public void testCreateGroupEmailNull() {
    Group.getBuilder()
            .name("Neel")
            .password("12345678")
            .users(mappings)
            .description("test")
            .email(null)
            .build();
    fail();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateGroupEmailEmpty() {
    Group.getBuilder()
            .name("Neel")
            .password("")
            .users(mappings)
            .description("test")
            .email("")
            .build();
    fail();
  }

  @Test(expected = NullPointerException.class)
  public void testCreateGroupDescriptionNull() {
    Group.getBuilder()
            .name("Neel")
            .password("12345678")
            .users(mappings)
            .description(null)
            .email("testemail")
            .build();
    fail();
  }

  @Test
  public void testGetters() {
    assertEquals("testGroup", group.getGroupName());
    assertEquals("testpassgroup", group.getPassword());
    assertEquals("test@email", group.getGroupEmail());
    assertEquals("test group", group.getDescription());
//    assertEquals(members, group.getMembers());

    Group mockGroup = mock(Group.class);
    when(mockGroup.getGroupID()).thenReturn(1);
    assertEquals(1, mockGroup.getGroupID());
  }

  @Test
  public void testEquals() {
    Group newGroup = Group.getBuilder()
            .name("testGroup")
            .password("testpassgroup2")
            .users(mappings)
            .description("test group")
            .email("test@email")
            .build();
    assertEquals(group, newGroup);

    newGroup = Group.getBuilder()
            .name("testGroup2")
            .password("testpassgroup2")
            .users(mappings)
            .description("test group")
            .email("test@email")
            .build();
    assertNotEquals(group, newGroup);

    assertEquals(group, group);

  }
}
