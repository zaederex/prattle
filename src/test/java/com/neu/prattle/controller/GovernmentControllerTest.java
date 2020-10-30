package com.neu.prattle.controller;

import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Government;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GovernmentService;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.UserService;

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

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class GovernmentControllerTest {

  private MockMvc mockMvc;

  @Mock
  private GovernmentService governmentService;

  @Mock
  private UserService userService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private GovernmentController governmentController;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders
            .standaloneSetup(governmentController)
            .build();
  }

  @Test
  public void testValidLogin() {
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    when(governmentService.validateAccount(government.getGovUsername(),
            government.getGovPassword())).thenReturn(government);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/government/login")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testLoginFailure() {
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    doThrow(IllegalStateException.class).when(governmentService)
            .validateAccount(government.getGovUsername(),
                    government.getGovPassword());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/government/login")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testCreateSubpoena() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(governmentService.findByGovName(anyString())).thenReturn(government);
    when(governmentService
            .createSubpoena(government, user)).thenReturn(subpoena);
    when(userService.findUserByName("Test")).thenReturn(Optional.of(user));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/government/FBI/subpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testCreateSubpoenaNoUser() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(governmentService.findByGovName(anyString())).thenReturn(government);
    when(governmentService
            .createSubpoena(government, user)).thenReturn(subpoena);
    when(userService.findUserByName("Test")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/government/FBI/subpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testCreateSubpoenaNoGovernment() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    doThrow(IllegalStateException.class).when(governmentService).findByGovName(anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/government/FBI/subpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testDeleteSubpoena() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(governmentService.findByGovName(anyString())).thenReturn(government);
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(user));
    when(governmentService
            .deleteSubpoena(government, user)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/government/FBI/unsubpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testDeleteSubpoenaNoGovernment() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    doThrow(IllegalStateException.class).when(governmentService).findByGovName(anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/government/FBI/unsubpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testDeleteSubpoenaNoUser() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(governmentService.findByGovName(anyString())).thenReturn(government);
    when(userService.findUserByName(anyString())).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/government/FBI/unsubpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testDeleteSubpoenaNoSubpoena() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(governmentService.findByGovName(anyString())).thenReturn(government);
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(user));
    when(governmentService.deleteSubpoena(any(), any())).thenReturn(false);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/government/FBI/unsubpoena/Test")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testGetAllSubpoenas() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(governmentService.findByGovName(anyString())).thenReturn(government);
    when(governmentService.findAllSubpoenas(any())).thenReturn(Collections.singletonList(subpoena));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/government/FBI/subpoenas")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetAllSubpoenasNoGovernment() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    doThrow(IllegalStateException.class).when(governmentService).findByGovName(anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/government/FBI/subpoenas")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetAllSubpoenasInvalid() {
    User user = User.getUserBuilder().username("Test").password("password").build();
    Government government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(user));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/government/FBI/subpoenas")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(GroupControllerTest.createPostJsonBody(government)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMessagesForUser() throws UserDoesNotExistException {
    Message msg1 = Message.messageBuilder().setMessageContent("Hi! It's me! Mario!").build();

    when(messageService.findMessagesForReceivingUser(anyString(),
            anyBoolean())).thenReturn(Collections.singletonList(msg1));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/government/conversation"
              + "/FBI/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMessagesForUserNegative() throws UserDoesNotExistException {
    Message msg1 = Message.messageBuilder().setMessageContent("Hi! It's me! Mario!").build();
    when(governmentService.isSubpoenaedUser(any(), any())).thenReturn(true);
    doThrow(UserDoesNotExistException.class).when(messageService).findMessagesForReceivingUser(anyString(),
            anyBoolean());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/government/conversation/CIA"
              + "/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
