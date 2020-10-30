package com.neu.prattle.service;

import com.neu.prattle.dto.UserDTO;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.UserFeedMapper;
import com.neu.prattle.repository.UserFeedMapperRepository;
import com.neu.prattle.repository.UserRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A public Junit test class to test UserServiceDaoImpl class, using the mockito mocking
 * frameworks.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceDaoImplTest {

  private static final String PASSWORD_1 = "password1";
  private static final String NAME_1 = "name1";

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserFeedMapperRepository userFeedMapperRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceDaoImpl userService;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testFindUserByUserName() {
    User user1 = User.getUserBuilder().username(NAME_1).password(PASSWORD_1).build();
    userService.setUserRepository(userRepository);

    given(userRepository.findByUsername(NAME_1)).willReturn(Optional.of(user1));

    Optional<User> result = userService.findUserByName(NAME_1);

    result.ifPresent(user -> assertEquals(NAME_1, user1.getUsername()));
  }

  @Test(expected = UserAlreadyPresentException.class)
  public void testAddUserException() {
    User user1 = User.getUserBuilder().username(NAME_1).password(PASSWORD_1).build();
    userService.setUserRepository(userRepository);
    when(userRepository.findByUsername(NAME_1)).thenReturn(Optional.of(user1));

    User returnUser = userService.addUser(user1);
    Assert.assertNull(returnUser);
  }

  @Test
  public void testAddUser() {
    User user1 = User.getUserBuilder().username(NAME_1).password(PASSWORD_1).build();
    userService.setUserRepository(userRepository);
    when(userRepository.save(any(User.class))).thenReturn(user1);
    when(passwordEncoder.encode(any())).thenReturn(PASSWORD_1);
    User result = userService.addUser(user1);

    assertEquals(NAME_1, result.getUsername());
    assertEquals(PASSWORD_1, result.getPassword());
  }

  @Test
  public void testAddUserWithAvatar() {
    User user1 = User.getUserBuilder().username(NAME_1).password(PASSWORD_1)
            .profilePicture("test").build();
    userService.setUserRepository(userRepository);
    when(userRepository.save(any(User.class))).thenReturn(user1);
    when(passwordEncoder.encode(any())).thenReturn(PASSWORD_1);
    User result = userService.addUser(user1);

    assertEquals(NAME_1, result.getUsername());
    assertEquals(PASSWORD_1, result.getPassword());
  }

  @Test
  public void testValidateUser() {
    User user1 = User.getUserBuilder().username(NAME_1).password(PASSWORD_1).build();
    userService.setUserRepository(userRepository);

    given(userService.findUserByName(NAME_1)).willReturn(Optional.of(user1));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    User result = userService.validateUser(NAME_1, PASSWORD_1);

    assertEquals(NAME_1, result.getUsername());
    assertEquals(PASSWORD_1, result.getPassword());
  }

  @Test(expected = IllegalStateException.class)
  public void testInValidateUser() {
    User user2 = User.getUserBuilder().username(NAME_1).password("password2").build();

    userService.setUserRepository(userRepository);

    given(userService.findUserByName(NAME_1)).willReturn(Optional.of(user2));
    userService.validateUser(NAME_1, PASSWORD_1);
  }

  @Test(expected = IllegalStateException.class)
  public void testInValidateUser1() {
    User user2 = User.getUserBuilder().username(NAME_1).password("password2").build();

    userService.setUserRepository(userRepository);

    given(userService.findUserByName("name2")).willReturn(Optional.of(user2));
    userService.validateUser(NAME_1, PASSWORD_1);
  }

  @Test
  public void testGetAllUsers() {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    Set<User> set = new HashSet<>();
    set.add(bob);
    set.add(scoop);
    when(userRepository.findAll()).thenReturn(set);
    List<User> result = userService.getAllUsers();
    assertTrue(result.contains(bob));
    assertTrue(result.contains(scoop));
  }

  @Test
  public void testUpdateUser() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    UserDTO dto = new UserDTO();
    dto.setTimezone(TimeZone.getTimeZone("GMT").getID());
    dto.setFirstName("bobby");
    dto.setLastName("balboa");
    dto.setContactNumber("1234567890");
    dto.setUsername("bobby101");
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userRepository.save(any())).thenReturn(bob);
    assertEquals(bob, userService.updateUser(dto, "bob"));
  }

  @Test
  public void testUpdateUserWithAvatar() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2")
            .profilePicture("test").build();
    UserDTO dto = new UserDTO();
    dto.setTimezone(TimeZone.getTimeZone("GMT").getID());
    dto.setFirstName("bobby");
    dto.setLastName("balboa");
    dto.setContactNumber("1234567890");
    dto.setUsername("bobby101");
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userRepository.save(any())).thenReturn(bob);
    assertEquals(bob, userService.updateUser(dto, "bob"));
  }

  @Test(expected = UserDoesNotExistException.class)
  public void testUpdatedInvalidUser() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    UserDTO dto = mock(UserDTO.class);
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());
    assertEquals(bob, userService.updateUser(dto, "bob"));
  }


  @Test
  public void testRemoveUser() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));

    doAnswer((i) -> {
      assertEquals(bob, i.getArgument(0));
      return null;
    }).when(userRepository).delete(bob);
    userService.removeUser("bob");
  }

  @Test(expected = UserDoesNotExistException.class)
  public void testRemoveInvalidUser() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());
    userService.removeUser("bob");
    assertNull(bob);
  }

  @Test
  public void testAddFollower() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userService.findUserByName("scoop")).thenReturn(Optional.of(scoop));
    userService.addFollower("bob", "scoop");
    assertEquals(1, bob.getFollowers().size());
  }

  @Test
  public void testFindUserById() {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    when(userRepository.findById(1)).thenReturn(Optional.of(bob));
    assertEquals(bob, userService.findUserById(1).get());
  }

  @Test
  public void testRemoveFollower() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userService.findUserByName("scoop")).thenReturn(Optional.of(scoop));
    userService.removeFollower("bob", "scoop");
    assertEquals(0, bob.getFollowers().size());
  }

  @Test
  public void getAllFollowers() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    bob.setFollowers(new HashSet<>(Collections.singletonList(scoop)));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userService.findUserByName("scoop")).thenReturn(Optional.of(scoop));
    assertTrue(userService.getAllFollowers("bob").contains(scoop));
  }

  @Test
  public void getAllFollowing() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    scoop.setFollowees(new HashSet<>(Collections.singletonList(bob)));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userService.findUserByName("scoop")).thenReturn(Optional.of(scoop));
    assertTrue(userService.getAllFollowing("scoop").contains(bob));
  }

  @Test
  public void testLogout() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    userService.logout("bob");
    assertNotNull(bob.getLogOutTimestamp());
  }

  @Test(expected = UserDoesNotExistException.class)
  public void testLogoutFailure() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());
    userService.logout("bob");
    assertNotNull(bob.getLogOutTimestamp());
  }

  @Test
  public void testUpdateFollowers() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    User user = User.getUserBuilder().username("user").password("password3").build();
    user.setFollowers(new HashSet<>(Arrays.asList(bob, scoop)));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(userService.findUserByName("scoop")).thenReturn(Optional.of(scoop));
    when(userService.findUserByName("user")).thenReturn(Optional.of(user));
    userService.updateFollowersFeed("user", "logged on");
    assertNotNull(bob.getUserFeeds());
    assertNotNull(scoop.getUserFeeds());
  }

  @Test
  public void testGetUserFeeds() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("bob").password("password2").build();
    User scoop = User.getUserBuilder().username("scoop").password("password2").build();
    User user = User.getUserBuilder().username("user").password("password3").build();
    UserFeedMapper feed = new UserFeedMapper();
    feed.setUser(bob);
    feed.setFeedText("logged on");
    feed.setFeedTime(new Timestamp(new Date().getTime()));
    user.setFollowers(new HashSet<>(Arrays.asList(bob, scoop)));
    lenient().when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
    lenient().when(userRepository.findByUsername("scoop")).thenReturn(Optional.of(scoop));
    lenient().when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
    lenient().when(userFeedMapperRepository.save(any())).thenReturn(feed);
    userService.updateFollowersFeed("user", "logged on");
    assertNotNull(userService.getUserFeeds("bob"));
  }
}
