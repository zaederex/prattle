package com.neu.prattle.controller;

import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.User;
import com.neu.prattle.service.FilterServiceImpl;
import com.neu.prattle.service.UserServiceDaoImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Tests methods exposed by the filter controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FilterControllerTest {

  private MockMvc mockMvc;

  @Mock
  private FilterServiceImpl filterService;

  @Mock
  private UserServiceDaoImpl userService;

  @InjectMocks
  private FilterController filterController;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders
            .standaloneSetup(filterController)
            .build();
  }

  @Test
  public void addFilter() {
    try {
      doNothing().when(filterService).addFilter(anyString(), anyString());
      MvcResult mvcResult;
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/filter/add/annoy/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void addNegativeFilter() throws Exception {
    doThrow(UserDoesNotExistException.class).when(filterService).addFilter(anyString(), anyString());
    MvcResult mvcResult;
    mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/filter/add/annoy/bob"))
            .andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus());
    assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
  }

  @Test
  public void removeFilter() {
    try {
      doNothing().when(filterService).removeFilter(anyString(), anyString());
      MvcResult mvcResult;
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/filter/remove/annoy/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void removeNegativeFilter() throws Exception {
    doThrow(UserDoesNotExistException.class).when(filterService).removeFilter(anyString(), anyString());
    MvcResult mvcResult;
    mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/filter/remove/annoy/bob"))
            .andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus());
    assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
  }

  @Test
  public void testGetFiltersForUsername() {
    User bob = User.getUserBuilder().username("bob").password("password2").build();

    Filter filter = new Filter();
    filter.setFilterString("spam");
    filter.setFilterID(1);
    filter.setUsers(new HashSet<>(Collections.singletonList(bob)));

    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(filterService.getFiltersForUser(bob)).thenReturn(Collections.singletonList(filter));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/filter/get/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("spam"));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
