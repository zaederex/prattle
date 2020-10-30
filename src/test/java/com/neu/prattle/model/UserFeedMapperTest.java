package com.neu.prattle.model;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class UserFeedMapperTest {

  private UserFeedMapper userFeed;
  private User user1;

  @Before
  public void setUp() throws Exception {
    user1 = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    userFeed = new UserFeedMapper(user1, "the feed text", Timestamp.valueOf(LocalDateTime.now()));
  }

  @Test
  public void testSetAndGetFeedID() {
    userFeed.setFeedID(1);
    assertEquals(1, userFeed.getFeedID());
  }

  @Test
  public void testSetAndGetUser() {
    assertEquals(user1, userFeed.getUser());
    User user2 = User.getUserBuilder()
            .username("John")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    userFeed.setUser(user2);
    assertEquals(user2, userFeed.getUser());
  }

  @Test
  public void testSetAndGetFeedText() {
    assertEquals("the feed text", userFeed.getFeedText());
    userFeed.setFeedText("new feed text");
    assertEquals("new feed text", userFeed.getFeedText());
  }


  @Test
  public void testSetAndGetFeedTime() {
    Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
    userFeed.setFeedTime(ts);
    assertEquals(ts, userFeed.getFeedTime());
  }
}