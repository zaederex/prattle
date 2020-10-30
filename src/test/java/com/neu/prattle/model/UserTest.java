package com.neu.prattle.model;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test class for the User class.
 */
public class UserTest {

  private static final String USER_NAME = "Connor";
  private static final String PASSWORD_1 = "123456789";

  /**
   * Test creating user.
   */
  @Test
  public void testCreateUser() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    long time = new Date().getTime();
    Timestamp timestamp = new Timestamp(time);
    user.setLogOutTimestamp(timestamp);
    assertEquals("Joe", user.getUsername());
    assertEquals(timestamp, user.getLogOutTimestamp());
  }

  /**
   * Test creating user with null argument throws exception.
   */
  @Test(expected = NullPointerException.class)
  public void testCreateUserNullName() {
    User.getUserBuilder().username(null).build();
    fail();
  }

  /**
   * Test creating user with null argument throws exception.
   */
  @Test(expected = NullPointerException.class)
  public void testCreateUserNullPassword() {
    User.getUserBuilder().username("Bob").timezone("GMT").password(null).build();
  }

  /**
   * Test creating user with password with spaces in it.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateUserPasswordWithSpaces() {
    User.getUserBuilder().username("Joe").password("pass word").build();
  }

  /**
   * Test creating user with password that is too short.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateUserPasswordTooShort() {
    User.getUserBuilder().username("Joe").password("pass").build();
  }

  /**
   * Test creating user with null argument throws exception.
   */
  @Test(expected = NullPointerException.class)
  public void testCreateUserNullCredentials() {
    User.getUserBuilder().username(null).password(null).build();
  }

  /**
   * Test set name of user with null argument throws exception.
   */
  @Test(expected = NullPointerException.class)
  public void testSetUserNameNull() {
    User.getUserBuilder().username(null).build();
  }

  /**
   * Test creating user with empty name string argument throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCreateUserEmptyName() {
    User.getUserBuilder().username("").password(PASSWORD_1).build();
  }


  /**
   * Test user set name and get name.
   */
  @Test
  public void testUserSetNameGetName() {
    User user = User.getUserBuilder().username(USER_NAME).password(PASSWORD_1).build();
    assertEquals(USER_NAME, user.getUsername());
    user.setUsername("Joe");
    assertEquals("Joe", user.getUsername());
    user.setUsername("Carol 69u test last name");
    assertEquals("Carol 69u test last name", user.getUsername());
  }

  /**
   * Test user equals method.
   */
  @Test
  public void testUserEqualsMethod() {
    User user = User.getUserBuilder().username(USER_NAME).password(PASSWORD_1).build();

    User userCompareDifferent = User.getUserBuilder().username("John")
            .password(PASSWORD_1).build();
    User userCompareSame = User.getUserBuilder().username(USER_NAME)
            .password(PASSWORD_1).build();
    assertEquals(user, userCompareSame);
    assertEquals(userCompareSame, user);
    assertNotEquals(user, userCompareDifferent);
    assertNotEquals(userCompareSame, userCompareDifferent);
    assertNotNull(user);
    assertNotEquals(user, new Object());
  }

  /**
   * Test user hashCode method.
   */
  @Test
  public void testUserHashCodeMethod() {
    User user = User.getUserBuilder().username(USER_NAME).password(PASSWORD_1).build();
    User userCompareDifferent = User.getUserBuilder().username("John").password(PASSWORD_1).build();
    User userCompareSame = User.getUserBuilder().username(USER_NAME).password(PASSWORD_1).build();
    assertEquals(user.hashCode(), userCompareSame.hashCode());
    assertEquals(userCompareSame.hashCode(), user.hashCode());
    assertNotEquals(user.hashCode(), userCompareDifferent.hashCode());
    assertNotEquals(userCompareSame.hashCode(), userCompareDifferent.hashCode());
  }

  /**
   * Tests the user ip mapper construction.
   */
  @Test
  public void testUserIpMapperConstruction() {
    User user = User.getUserBuilder()
            .username("nawaz")
            .password("nawaz12345")
            .timezone(null)
            .build();
    user.setSubpoenas(null);
    UserIPMapper mapper = new UserIPMapper(user, "192.168.0.160");
    mapper.setUserID(1);
    assertEquals(1, mapper.getUserID());
    assertEquals(user, mapper.getUser());
    assertEquals("192.168.0.160", mapper.getIpAddress());
    assertEquals(TimeZone.getTimeZone("UTC"), user.getTimezone());
    assertNull(user.getSubpoenas());
  }
}
