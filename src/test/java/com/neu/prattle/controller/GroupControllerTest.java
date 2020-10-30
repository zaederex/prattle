package com.neu.prattle.controller;

import com.neu.prattle.dto.GroupDTO;
import com.neu.prattle.dto.GroupMemberDTO;
import com.neu.prattle.dto.SubGroupDTO;
import com.neu.prattle.exceptions.GroupNotFoundException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.model.group.GroupUserMapper;
import com.neu.prattle.repository.group.GroupRepository;
import com.neu.prattle.repository.group.GroupUserMapperRepository;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.group.GroupService;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class GroupControllerTest {

  private MockMvc mockMvc;

  @Mock
  private GroupService groupService;

  @Mock
  private UserService userService;

  @Mock
  private GroupUserMapperRepository groupUserMapperRepository;

  @Mock
  private GroupRepository groupRepository;
  @InjectMocks
  private GroupController groupController;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders
            .standaloneSetup(groupController)
            .build();
  }

  @Test
  public void testCreateGroup() {
    User moderator = mock(User.class);
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    GroupDTO groupDTO = new GroupDTO();
    groupDTO.setModeratorName("bob");
    groupDTO.setGroupName("mock");
    groupDTO.setGroupEmail("mock@mock.mock");
    groupDTO.setDescription("mock mock mock");
    groupDTO.setPassword("mockmock");
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.empty());
    when(groupService.addGroup(group)).thenReturn(group);
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(moderator));
    doNothing().when(groupService).addMemberToGroup(group, moderator, true, true, true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/group/create")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(groupDTO)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testCreateGroupNoModerator() {
    User moderator = mock(User.class);
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    GroupDTO groupDTO = new GroupDTO();
    groupDTO.setModeratorName("bob");
    groupDTO.setGroupName("mock");
    groupDTO.setGroupEmail("mock@mock.mock");
    groupDTO.setDescription("mock mock mock");
    groupDTO.setPassword("mockmock");
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.empty());
    when(groupService.addGroup(group)).thenReturn(group);
    when(userService.findUserByName(anyString())).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/group/create")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(groupDTO)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Test
  public void testInvalidCreateGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/group/create")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(group)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddMember() {
    User moderator = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User user = User.getUserBuilder().username("bobby").password("bobbobbob").build();
    GroupMemberDTO userDTO = new GroupMemberDTO();
    userDTO.setMemberName("bobby");
    userDTO.setGroupName("mock");
    userDTO.setIsModerator(false);
    userDTO.setIsMember(true);
    userDTO.setIsFollower(false);
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(userService.findUserByName(moderator.getUsername())).thenReturn(Optional.of(moderator));
    when(userService.findUserByName(userDTO.getMemberName())).thenReturn(Optional.of(user));
    when(groupService.findGroupByName(group.getGroupName())).thenReturn(Optional.of(group));
    when(groupService.isModerator(group, moderator)).thenReturn(true);
    doNothing().when(groupService).addMemberToGroup(group, user, false, true, true);
    MvcResult mvcResult;
    try {
      String body = createPostJsonBody(userDTO);
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/addmember/bob")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddMemberNonModeratorRequester() {
    User moderator = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User user = User.getUserBuilder().username("bobby").password("bobbobbob").build();
    GroupMemberDTO userDTO = new GroupMemberDTO();
    userDTO.setMemberName("bobby");
    userDTO.setGroupName("mock");
    userDTO.setIsModerator(false);
    userDTO.setIsMember(true);
    userDTO.setIsFollower(false);
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(userService.findUserByName(moderator.getUsername())).thenReturn(Optional.of(moderator));
    when(userService.findUserByName(userDTO.getMemberName())).thenReturn(Optional.of(user));
    when(groupService.findGroupByName(group.getGroupName())).thenReturn(Optional.of(group));
    when(groupService.isModerator(group, moderator)).thenReturn(false);
    doNothing().when(groupService).addMemberToGroup(group, user, false, true, true);
    MvcResult mvcResult;
    try {
      String body = createPostJsonBody(userDTO);
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/addmember/bob")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddMemberNoRequester() {
    User moderator = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User user = User.getUserBuilder().username("bobby").password("bobbobbob").build();
    GroupMemberDTO userDTO = new GroupMemberDTO();
    userDTO.setMemberName("bobby");
    userDTO.setGroupName("mock");
    userDTO.setIsModerator(false);
    userDTO.setIsMember(true);
    userDTO.setIsFollower(false);
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(userService.findUserByName(moderator.getUsername())).thenReturn(Optional.of(moderator));
    when(userService.findUserByName(userDTO.getMemberName())).thenReturn(Optional.of(user));
    when(groupService.findGroupByName(group.getGroupName())).thenReturn(Optional.of(group));
    when(groupService.isModerator(group, moderator)).thenReturn(true);
    doNothing().when(groupService).addMemberToGroup(group, user, false, true, true);
    MvcResult mvcResult;
    try {
      String body = createPostJsonBody(userDTO);
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/addmember/sunny")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddMemberNoUser() {
    User moderator = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User user = User.getUserBuilder().username("bobby").password("bobbobbob").build();
    GroupMemberDTO userDTO = new GroupMemberDTO();
    userDTO.setMemberName("bobby");
    userDTO.setGroupName("mock");
    userDTO.setIsModerator(false);
    userDTO.setIsMember(true);
    userDTO.setIsFollower(false);
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(userService.findUserByName(moderator.getUsername())).thenReturn(Optional.of(moderator));
    when(userService.findUserByName(userDTO.getMemberName())).thenReturn(Optional.of(user));
    when(groupService.findGroupByName(group.getGroupName())).thenReturn(Optional.empty());
    doNothing().when(groupService).addMemberToGroup(group, user, false, true, true);
    MvcResult mvcResult;
    try {
      String body = createPostJsonBody(userDTO);
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/addmember/bob")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(body))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidAddMember() {
    User moderator = User.getUserBuilder().username("bob").password("bobbobbob").build();
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(userService.findUserByName(moderator.getUsername())).thenReturn(Optional.empty());
    when(groupService.findGroupByName(group.getGroupName())).thenReturn(Optional.of(group));
    doNothing().when(groupService).addMemberToGroup(group, moderator, true, true, true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/addmember/bob")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(moderator)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMembersByGroupName() {
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User scoop = User.getUserBuilder().username("scoop").password("bobbobbob").build();
    List<User> users = Arrays.asList(bob, scoop);
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(users);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/getmembersbygroup/group")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("bob"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("scoop"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }


  @Test
  public void testGetGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("mock"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }


  @Test
  public void testGetAllGroups() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    Group group2 = Group.getBuilder().name("group2").email("mocks@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(groupService.findGroups()).thenReturn(Arrays.asList(group, group2));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/allgroups"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("mock"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("group2"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMyGroups() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    Group group2 = Group.getBuilder().name("group2").email("mocks@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getGroupsForUser(bob)).thenReturn(Arrays.asList(group, group2));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mygroup/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("mock"));
      assertTrue(mvcResult.getResponse().getContentAsString().contains("group2"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMyGroupsUserDoesNotExist() {
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mygroup/bob"))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("[]"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testUpdateGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    doNothing().when(groupService).updateGroup(mock(GroupDTO.class), "mock");
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/update/mock")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(group)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testUpdateGroupNotFound() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    doThrow(GroupNotFoundException.class).when(groupService).updateGroup(any(), anyString());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/update/mock")
              .contentType(MediaType.APPLICATION_JSON_VALUE).content(createPostJsonBody(group)))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetModerators() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    GroupUserMapper mapper = new GroupUserMapper();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    mapper.setUser(bob);
    mapper.setModerator(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/moderators/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("bob"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidGetModerators() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    GroupUserMapper mapper = new GroupUserMapper();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    mapper.setUser(bob);
    mapper.setModerator(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/moderators/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertFalse(mvcResult.getResponse().getContentAsString().contains("bob"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testFollowGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.emptyList());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/bob/follow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }


  @Test
  public void testFollowGroup3() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.emptyList());
    when(groupService.isModerator(group, bob)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/bob/follow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testFollowGroup2() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.singletonList(bob));
    doNothing().when(groupService).addMemberToGroup(group, bob, false, true, false);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/bob/follow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testFollowGroupFail() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/bob/follow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidFollowGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.singletonList(bob));
    doNothing().when(groupService).addMemberToGroup(group, bob, false, true, false);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/bob/follow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }


  @Test
  public void testUnFollowGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    mapper.setFollower(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.emptyList());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/bob/unfollow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testUnFollowGroupExceptional() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User mock = mock(User.class);
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(mock);
    mapper.setModerator(true);
    mapper.setFollower(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.emptyList());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/bob/unfollow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testValidUnFollowGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    mapper.setFollower(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.getMemberTypeByGroupName(anyString(),
            anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(Collections.emptyList());
    doNothing().when(groupService).updateFollowersFeed(group.getGroupName(), "");
    when(groupUserMapperRepository.save(any())).thenReturn(mapper);
    when(groupRepository.save(any())).thenReturn(group);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/bob/unfollow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testUnFollowGroup2() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    mapper.setFollower(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/bob/unfollow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidUnFollowGroup() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    mapper.setFollower(true);
    group.setMappings(Collections.singletonList(mapper));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/bob/unfollow/mock")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetFollowers() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    User scoop = User.getUserBuilder().username("scoop").password("bobbobbob").build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(bob);
    mapper.setModerator(true);
    mapper.setFollower(true);
    GroupUserMapper mapper1 = new GroupUserMapper();
    mapper1.setUser(scoop);
    mapper1.setModerator(false);
    mapper1.setFollower(false);
    group.setMappings(Arrays.asList(mapper, mapper1));
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/followers")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("bob"));
      assertFalse(mvcResult.getResponse().getContentAsString().contains("scoop"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidGetFollowers() {
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/followers")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertFalse(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testModerator() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(bob));
    when(groupService.isModerator(group, bob)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isModerator")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("true"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvalidModerator() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName(anyString())).thenReturn(Optional.empty());
    when(groupService.isModerator(group, bob)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isModerator")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("false"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }


  @Test
  public void testAcceptInvite() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(bob));
    doNothing().when(groupService).acceptInvite(group, bob);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/invite/mock/accept/bob")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testAcceptNegativeInvite() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    doThrow(GroupNotFoundException.class).when(groupService).acceptInvite(group, bob);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/invite/mock/accept/bob")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("User or group does not exist"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testRejectInvite() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(bob));
    doNothing().when(groupService).rejectInvite(group, bob);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/invite/mock/reject/bob")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testRejectNegativeInvite() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/invite/mock/reject/bob")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("User or group does not exist"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetInvites() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(groupService.getInvites(group)).thenReturn(Collections.singletonList(bob));
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/invites")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("bob"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetNegativeInvites() {
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/invites")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
      assertFalse(mvcResult.getResponse().getContentAsString().contains("bob"));
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }


  @Test
  public void testGetGroupID() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    try {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/groupID")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetGroupName() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    when(groupService.findGroupById(anyInt())).thenReturn(Optional.of(group));
    try {
      MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/1/groupName")
              .accept(MediaType.APPLICATION_JSON))
              .andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testAddModerator() {
    User moderator = User.getUserBuilder().username("mod").password("bobbobbob").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();

    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(moderator);
    mapper.setModerator(true);

    GroupUserMapper mapper2 = new GroupUserMapper();
    mapper2.setUser(bob);
    mapper2.setModerator(false);

    group.setMappings(Arrays.asList(mapper, mapper2));
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName("mod")).thenReturn(Optional.of(moderator));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.isModerator(group, moderator)).thenReturn(true);
    when(groupUserMapperRepository.save(any())).thenReturn(mapper);
    when(groupRepository.save(any())).thenReturn(group);

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/moderators/mock/bob/mod")
              .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddNonExistentModerator() {
    User moderator = User.getUserBuilder().username("mod").password("bobbobbob").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();

    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(moderator);
    mapper.setModerator(true);

    GroupUserMapper mapper2 = new GroupUserMapper();
    mapper2.setUser(bob);
    mapper2.setModerator(false);

    group.setMappings(Arrays.asList(mapper, mapper2));
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName("mod")).thenReturn(Optional.of(moderator));
    when(groupService.isModerator(group, moderator)).thenReturn(true);
    when(userService.findUserByName("bob")).thenReturn(Optional.empty());

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/moderators/mock/bob/mod")
              .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddModeratorNonModeratorRequester() {
    User moderator = User.getUserBuilder().username("mod").password("bobbobbob").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();

    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(moderator);
    mapper.setModerator(true);

    GroupUserMapper mapper2 = new GroupUserMapper();
    mapper2.setUser(bob);
    mapper2.setModerator(false);

    group.setMappings(Arrays.asList(mapper, mapper2));
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName("mod")).thenReturn(Optional.of(moderator));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.isModerator(group, moderator)).thenReturn(false);

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/moderators/mock/bob/mod")
              .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddModeratorNonExistentGroup() {
    User moderator = User.getUserBuilder().username("mod").password("bobbobbob").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();

    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(moderator);
    mapper.setModerator(true);

    GroupUserMapper mapper2 = new GroupUserMapper();
    mapper2.setUser(bob);
    mapper2.setModerator(false);

    group.setMappings(Arrays.asList(mapper, mapper2));
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.empty());

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/group/moderators/mock/bob/mod")
              .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":400"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testRemoveModerator() {
    User moderator = User.getUserBuilder().username("mod").password("bobbobbob").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();

    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(moderator);
    mapper.setModerator(true);

    GroupUserMapper mapper2 = new GroupUserMapper();
    mapper2.setUser(bob);
    mapper2.setModerator(false);

    group.setMappings(Arrays.asList(mapper, mapper2));
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group));
    when(userService.findUserByName("mod")).thenReturn(Optional.of(moderator));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.isModerator(group, moderator)).thenReturn(true);
    when(groupUserMapperRepository.save(any())).thenReturn(mapper);
    when(groupRepository.save(any())).thenReturn(group);

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rest/group/moderators/mock/bob/mod")
              .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddSubGroup() {
    Group parent = Group.getBuilder().name("parent").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    Group child = Group.getBuilder().name("child").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    SubGroupDTO dto = new SubGroupDTO();
    dto.setParentId(1);
    dto.setChildId(2);
    when(groupService.findGroupById(1)).thenReturn(Optional.of(parent));
    when(groupService.findGroupById(2)).thenReturn(Optional.of(child));
    when(groupService.addSubGroup(parent, child)).thenReturn(parent);


    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/group/addSubGroup")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(createPostJsonBody(dto)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":200"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddSubGroupNoParent() {
    SubGroupDTO dto = new SubGroupDTO();
    dto.setParentId(1);
    dto.setChildId(2);
    when(groupService.findGroupById(1)).thenReturn(Optional.empty());

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/group/addSubGroup")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(createPostJsonBody(dto)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddSubGroupNoChild() {
    Group parent = Group.getBuilder().name("parent").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();

    SubGroupDTO dto = new SubGroupDTO();
    dto.setParentId(1);
    dto.setChildId(2);
    when(groupService.findGroupById(1)).thenReturn(Optional.of(parent));
    when(groupService.findGroupById(2)).thenReturn(Optional.empty());

    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/group/addSubGroup")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(createPostJsonBody(dto)))
              .andReturn();

      assertEquals(200, mvcResult.getResponse().getStatus());
      assertTrue(mvcResult.getResponse().getContentAsString().contains("\"status\":500"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testIfMember() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.isMember(group, bob)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isMember")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testIfMemberFail() {
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isMember")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testIfModerator() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.isModerator(group, bob)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isModerator")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testIfModeratorFail() {
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isModerator")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testIfFollower() {
    Group group = Group.getBuilder().name("mock").email("mock@mock.mock").description("mock mock mock")
            .password("mockmock").build();
    User bob = User.getUserBuilder().username("bob").password("bobbobbob").build();
    when(groupService.findGroupByName("mock")).thenReturn(Optional.of(group));
    when(userService.findUserByName("bob")).thenReturn(Optional.of(bob));
    when(groupService.isFollower(group, bob)).thenReturn(true);
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isFollower")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testIfFollowerFail() {
    when(groupService.findGroupByName("mock")).thenReturn(Optional.empty());
    MvcResult mvcResult;
    try {
      mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/group/mock/bob/isFollower")
      ).andReturn();
      assertEquals(200, mvcResult.getResponse().getStatus());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  static String createPostJsonBody(Object o) throws MyRuntimeException {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(o);
    } catch (IOException e) {
      throw new MyRuntimeException("Could not create json string for object");
    }
  }

  static class MyRuntimeException extends Exception {
    MyRuntimeException(String message) {
      super(message);
    }
  }
}
