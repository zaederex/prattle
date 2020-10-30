package com.neu.prattle.controller;

import com.neu.prattle.dto.UserDTO;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.UserFeedMapper;
import com.neu.prattle.repository.UserIPMapperRepository;
import com.neu.prattle.service.UserService;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the UserController class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

  private static class MyRuntimeException extends Exception {
    MyRuntimeException(String message) {
      super(message);
    }
  }

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @Mock
  private UserIPMapperRepository userIPMapperRepository;

  @InjectMocks
  private UserController userController;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .build();
  }

  private static final String PASSWORD_101 = "123455678";
  private static final String NAME_101 = "name1";

  /**
   * Test adding user successfully. Expects a 200 OK HTTP response.
   */
  @Test
  public void testAddUserSuccess() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();

    when(userService.addUser(user)).thenReturn(user);

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/create")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(user)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test adding the same user twice has the correct response. Expects an HTTPS 200 OK status even
   * when the resource already exists.
   */
  @Test
  public void testAddUserDuplicateError() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();

    doThrow(UserAlreadyPresentException.class).when(userService).addUser(user);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/create")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(user)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":409"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetUser() {
    userController.setUserService(userService);
    User user = User.getUserBuilder().username(NAME_101).password("password1").build();

    given(userService.findUserByName(NAME_101)).willReturn(Optional.of(user));

    User result = userController.getUser(NAME_101);
    assertEquals(NAME_101, result.getUsername());
    assertEquals("password1", result.getPassword());
  }

  @Test
  public void testUpdateUser() throws UserDoesNotExistException {
    userController.setUserService(userService);
    UserDTO userDTO = mock(UserDTO.class);
    when(userDTO.getUsername()).thenReturn("newName");
    User user = User.getUserBuilder().username(NAME_101).password("password1").build();
    User user1 = User.getUserBuilder().username("newName").password("password1").build();

    given(userService.updateUser(userDTO, "newName")).willReturn(user1);

    User result = userController.updateUser(userDTO, "newName");
    assertEquals("newName", result.getUsername());
    assertEquals("password1", result.getPassword());
  }

  @Test
  public void testGetUserNull() {
    userController.setUserService(userService);
    given(userService.findUserByName(NAME_101)).willReturn(Optional.empty());
    assertNull(userController.getUser(NAME_101));
  }

  @Test
  public void testUserLoginFail() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    doThrow(IllegalStateException.class).when(userService).validateUser(user.getUsername(),
            user.getPassword());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/login")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(user)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Tests deletion of user that exists.
   */
  @Test
  public void testDeleteUser() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/user/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(user)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      mvcResult.getResponse().getContentAsString();
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  /**
   * Tests deletion of user that does not exist.
   */
  @Test
  public void testDeleteNonExistentUser() throws UserDoesNotExistException {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    doThrow(UserDoesNotExistException.class).when(userService).removeUser(user.getUsername());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/user/"
              + user.getUsername()).contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(createPostJsonBody(user)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      mvcResult.getResponse().getContentAsString();
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testUserLoginSuccess() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    when(userService.validateUser(user.getUsername(), user.getPassword())).thenReturn(user);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/login")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(createPostJsonBody(user))
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();

      verify(userService).validateUser(user.getUsername(), user.getPassword());
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      // fail();
    }
  }

  @Test
  public void testUserSecureLoginSuccess() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    when(userService.validateUser(user.getUsername(), user.getPassword())).thenReturn(user);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/securelogin")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(createPostJsonBody(user))
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();

      verify(userService).validateUser(user.getUsername(), user.getPassword());
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertNotNull(mvcResult.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      // fail();
    }
  }

  @Test
  public void testAddFollower() throws UserDoesNotExistException {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    doNothing().when(userService).addFollower(anyString(), anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/user/"
              + user.getUsername() + "/follow/" + "bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      mvcResult.getResponse().getContentAsString();
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testAddInvalidFollower() throws UserDoesNotExistException {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    doThrow(UserDoesNotExistException.class).when(userService).addFollower(anyString(), anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/user/"
              + user.getUsername() + "/follow/" + "bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      mvcResult.getResponse().getContentAsString();
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testUnfollow() throws UserDoesNotExistException {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    doNothing().when(userService).removeFollower(anyString(), anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/user/"
              + user.getUsername() + "/unfollow/" + "bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      mvcResult.getResponse().getContentAsString();
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testInvalidUnfollow() throws UserDoesNotExistException {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    doThrow(UserDoesNotExistException.class).when(userService).removeFollower(anyString(), anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/user/"
              + user.getUsername() + "/unfollow/" + "bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      mvcResult.getResponse().getContentAsString();
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetAllFollower() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    User scoop = User.getUserBuilder().username("Scoop").password(PASSWORD_101).build();
    User mug = User.getUserBuilder().username("Mug").password(PASSWORD_101).build();
    User daisy = User.getUserBuilder().username("Daisy").password(PASSWORD_101).build();
    when(userService.getAllFollowers(bob.getUsername()))
            .thenReturn(Arrays.asList(scoop, mug, daisy));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/"
              + bob.getUsername() + "/followers"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Scoop"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Mug"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Daisy"));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testInvalidGetAllFollower() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    doThrow(IllegalStateException.class).when(userService).getAllFollowers(bob.getUsername());
    try {
      mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/"
              + bob.getUsername() + "/followers"))
              .andReturn();
      fail();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetAllFollowing() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    User scoop = User.getUserBuilder().username("Scoop").password(PASSWORD_101).build();
    User mug = User.getUserBuilder().username("Mug").password(PASSWORD_101).build();
    User daisy = User.getUserBuilder().username("Daisy").password(PASSWORD_101).build();
    when(userService.getAllFollowing(bob.getUsername()))
            .thenReturn(Arrays.asList(scoop, mug, daisy));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/"
              + bob.getUsername() + "/following"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Scoop"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Mug"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Daisy"));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testInvalidGetAllFollowing() throws UserDoesNotExistException {
    User bob = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    doThrow(IllegalStateException.class).when(userService).getAllFollowing(bob.getUsername());
    try {
      mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/"
              + bob.getUsername() + "/following"))
              .andReturn();
      fail();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetAllUsers() {
    User user = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    User user2 = User.getUserBuilder().username("Wendy").password(PASSWORD_101).build();

    when(userService.getAllUsers()).thenReturn(Arrays.asList(user, user2));

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/allusers"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Bob"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Wendy"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testUserSecureLogoutSuccess() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101).build();
    try {
      doNothing().when(userService).logout(anyString());
      MvcResult mvcResult;
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/logout/Test")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertNotNull(mvcResult.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testUserSecureLogoutFailure() {
    try {
      doThrow(UserDoesNotExistException.class).when(userService).logout(anyString());
      MvcResult mvcResult;
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/user/logout/bob")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetUserID() {
    User user = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    when(userService.findUserByName("bob")).thenReturn(Optional.of(user));
    try {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/bob/userID")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }


  @Test
  public void testGetUserName() {
    User user = User.getUserBuilder().username("Bob").password(PASSWORD_101).build();
    when(userService.findUserById(anyInt())).thenReturn(Optional.of(user));
    try {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/1/username")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetUserFeeds() throws UserDoesNotExistException {
    User user = User.getUserBuilder().username("john").password(PASSWORD_101).build();
    User user2 = User.getUserBuilder().username("Wendy").password(PASSWORD_101).build();
    User user3 = User.getUserBuilder().username("Jane").password(PASSWORD_101).build();
    UserFeedMapper userFeedMapper1 = new UserFeedMapper(user, "logged on", Timestamp.valueOf(LocalDateTime.now()));
    UserFeedMapper userFeedMapper2 = new UserFeedMapper(user2, "logged on", Timestamp.valueOf(LocalDateTime.now()));
    UserFeedMapper userFeedMapper3 = new UserFeedMapper(user3, "logged on", Timestamp.valueOf(LocalDateTime.now()));


    when(userService.getUserFeeds("bob")).thenReturn(Arrays.asList(userFeedMapper1, userFeedMapper2, userFeedMapper3));

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/bob/feeds"))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("john"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Wendy"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("Jane"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetAvatar() {
    User user = User.getUserBuilder().username("Test").password(PASSWORD_101)
            .profilePicture("test").build();
    try {
      when(userService.findUserByName(anyString())).thenReturn(Optional.of(user));
      when(userService.getAvatar(user)).thenReturn("test");
      MvcResult mvcResult;
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/user/Test/avatar")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  private String createPostJsonBody(Object o) throws MyRuntimeException {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(o);
    } catch (IOException e) {
      throw new MyRuntimeException("Could not create json string for object");
    }
  }
}
