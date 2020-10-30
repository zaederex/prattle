package com.neu.prattle.service;

import com.neu.prattle.dto.GroupDTO;
import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.exceptions.GroupNotFoundException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.UserFeedMapper;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.model.group.GroupUserCompositeKey;
import com.neu.prattle.model.group.GroupUserMapper;
import com.neu.prattle.repository.UserFeedMapperRepository;
import com.neu.prattle.repository.group.GroupRepository;
import com.neu.prattle.repository.group.GroupUserMapperRepository;
import com.neu.prattle.service.group.GroupServiceDaoImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GroupServiceDaoTest {

  @Mock
  private GroupRepository groupRepository;
  @Mock
  private GroupUserMapperRepository mapperRepository;

  @Mock
  private UserFeedMapperRepository userFeedMapperRepository;
  @InjectMocks
  private GroupServiceDaoImpl groupServiceDao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testAddGroup() {

    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    when(groupRepository.findByGroupName(group.getGroupName())).thenReturn(Optional.empty());
    when(groupRepository.save(group)).thenReturn(group);
    assertEquals(group, groupServiceDao.addGroup(group));
  }


  @Test(expected = GroupAlreadyPresentException.class)
  public void testNegativeAddGroup() {
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    lenient().when(groupRepository.findByGroupName(group.getGroupName())).thenReturn(Optional.of(group));
    lenient().when(groupRepository.save(group)).thenReturn(group);
    assertEquals(group, groupServiceDao.addGroup(group));
  }

  @Test
  public void testGetMemberByGroupName() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    groupServiceDao.addMemberToGroup(group, user, true, true, true);
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(user);
    mapper.setModerator(true);
    mapper.setFollower(true);
    mapper.setMember(true);
    mapperRepository.save(mapper);
    assertEquals("testGroup", group.getGroupName());
  }

  @Test
  public void testIsGroup() {
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    when(groupRepository.findByGroupName(group.getGroupName())).thenReturn(Optional.of(group));
    assertTrue(groupServiceDao.isGroup("testGroup"));
  }

  @Test
  public void TestGetMembersByGroupName() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(user);
    mapper.setModerator(true);
    mapper.setFollower(true);
    mapper.setMember(true);
    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.of(group));
    when(mapperRepository.getMapsByGroupId(anyInt())).thenReturn(Collections.singletonList(mapper));
    assertTrue(groupServiceDao.getMemberTypeByGroupName("testGroup", true, true, true).contains(user));
  }

  @Test
  public void testFindById() {
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    when(groupRepository.findByGroupID(anyInt())).thenReturn(Optional.of(group));
    Optional<Group> optionalGroup = groupServiceDao.findGroupById(1);
    assertTrue(optionalGroup.isPresent());
    assertEquals(group, optionalGroup.get());
  }

  @Test
  public void testGetGroupsForUsr() {
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();

    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setUser(user);
    mapper.setGroup(group);
    mapper.setModerator(true);
    mapper.setFollower(true);
    mapper.setMember(true);
    user.setMappings(Collections.singletonList(mapper));
    assertTrue(groupServiceDao.getGroupsForUser(user).contains(group));
  }

  @Test
  public void testIsMember() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setGroup(group);
    mapper.setMember(true);
    mapper.setUser(user);
    group.setMappings(Collections.singletonList(mapper));
    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.of(group));
    List<GroupUserMapper> users = Collections.singletonList(mapper);
    when(mapperRepository.getMapsByGroupId(anyInt())).thenReturn(users);
    assertTrue(groupServiceDao.isMember(group, user));
  }

  @Test
  public void testIsMemberException() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.empty());
    assertFalse(groupServiceDao.isMember(group, user));
  }

  @Test
  public void testFindGroups() {
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    Group group1 = Group.getBuilder()
            .name("testGroup1")
            .email("testMail1")
            .description("description")
            .password("password")
            .build();

    when(groupRepository.findAll()).thenReturn(Arrays.asList(group, group1));
    assertTrue(groupServiceDao.findGroups().contains(group));
    assertTrue(groupServiceDao.findGroups().contains(group1));
  }

  @Test(expected = GroupNotFoundException.class)
  public void testInvalidUpdateGroup() {
    GroupDTO groupDTO = new GroupDTO();
    groupDTO.setGroupName("Group");
    groupDTO.setDescription("Description");
    groupDTO.setGroupEmail("group@group.group");
    groupDTO.setPassword("password");
    groupDTO.setModeratorName("moderator");
    when(groupRepository.findByGroupName(anyString())).thenReturn(Optional.empty());
    groupServiceDao.updateGroup(groupDTO, "bob");
    fail();
  }

  @Test
  public void testAcceptInvite() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    User dummy1 = User.getUserBuilder().username("Danny").password("dummypassword").build();
    User dummy2 = User.getUserBuilder().username("Daniel").password("dummypassword").build();
    GroupUserMapper dummy1Mapper = new GroupUserMapper();
    dummy1Mapper.setFollower(true);
    dummy1Mapper.setMember(false);
    dummy1Mapper.setModerator(false);
    dummy1Mapper.setUser(dummy1);
    dummy1Mapper.setGroup(group);
    GroupUserMapper dummy2Mapper = new GroupUserMapper();
    dummy2Mapper.setFollower(true);
    dummy2Mapper.setMember(false);
    dummy2Mapper.setModerator(false);
    dummy1Mapper.setUser(dummy2);
    dummy1Mapper.setGroup(group);
    group.setMappings(Arrays.asList(dummy1Mapper, dummy2Mapper));
    UserFeedMapper feedMapper = new UserFeedMapper();
    feedMapper.setFeedID(1);
    feedMapper.setFeedTime(new Timestamp(new Date().getTime()));
    feedMapper.setUser(user);
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setGroup(group);
    mapper.setMember(false);
    mapper.setUser(user);
    group.setMappings(Collections.singletonList(mapper));
    when(mapperRepository.getMapsByGroupId(group.getGroupID()).
            stream().filter(x -> x.getUser() == user).collect(Collectors.toList())).thenReturn(Collections.singletonList(mapper));
    when(groupRepository.findByGroupName(anyString())).thenReturn(Optional.of(group));
    lenient().when(userFeedMapperRepository.save(any())).thenReturn(feedMapper);
    groupServiceDao.acceptInvite(group, user);
    assertTrue(mapper.isMember());
  }

  @Test
  public void testRejectInvite() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setGroup(group);
    mapper.setMember(false);
    mapper.setUser(user);
    group.setMappings(Collections.singletonList(mapper));
    when(mapperRepository.getMapsByGroupId(group.getGroupID()).
            stream().filter(x -> x.getUser() == user).collect(Collectors.toList())).thenReturn(Collections.singletonList(mapper));
    doNothing().when(mapperRepository).delete(mapper);
    groupServiceDao.rejectInvite(group, user);
    assertNotNull(group.getMappings());
  }

  @Test
  public void getInvites() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setGroup(group);
    mapper.setMember(false);
    mapper.setUser(user);
    group.setMappings(Collections.singletonList(mapper));
    when(mapperRepository.getMapsByGroupId(group.getGroupID())
            .stream().filter(x -> !x.isMember()).collect(Collectors.toList())).thenReturn(Collections.singletonList(mapper));
    assertEquals(Collections.singletonList(user), groupServiceDao.getInvites(group));
  }

  @Test
  public void testUpdateGroup() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();

    GroupDTO dto = new GroupDTO();
    dto.setGroupName("group2");
    dto.setModeratorName("mod2");
    dto.setPassword("password2");
    dto.setGroupEmail("new@new.new");
    dto.setDescription("new");

    Group group = Group.getBuilder().name("group1").password("password1")
            .description("old").email("old@old.old").build();
    GroupUserMapper dummy1Mapper = new GroupUserMapper();
    dummy1Mapper.setFollower(true);
    dummy1Mapper.setMember(false);
    dummy1Mapper.setModerator(false);
    dummy1Mapper.setUser(user);
    dummy1Mapper.setGroup(group);
    dummy1Mapper.setGroup(group);
    group.setMappings(Collections.singletonList(dummy1Mapper));
    when(groupRepository.findByGroupName(anyString())).thenReturn(Optional.of(group));

    when(groupRepository.save(any())).thenReturn(group);
    groupServiceDao.updateGroup(dto, "group1");

    assertEquals(dto.getGroupName(), group.getGroupName());
  }

  @Test
  public void testGetSubGroups() {
    Group parent = Group.getBuilder().name("parent").password("password1")
            .description("old").email("old@old.old").build();
    Group child = Group.getBuilder().name("child").password("password1")
            .description("old").email("old@old.old").build();
    when(groupRepository.fetchSubGroups(anyInt())).thenReturn(Collections.singletonList(child));
    assertTrue(groupServiceDao.getSubgroups(parent).contains(child));
  }

  @Test
  public void testAddSubGroups() {
    Group parent = Group.getBuilder().name("parent").password("password1")
            .description("old").email("old@old.old").build();
    Group child = Group.getBuilder().name("child").password("password1")
            .description("old").email("old@old.old").build();
    when(groupRepository.save(parent)).thenReturn(parent);
    when(groupRepository.save(child)).thenReturn(child);
    assertEquals(parent, groupServiceDao.addSubGroup(parent, child));
  }

  @Test
  public void testIsModerator() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setGroup(group);
    mapper.setModerator(false);
    mapper.setMember(false);
    mapper.setUser(user);
    group.setMappings(Collections.singletonList(mapper));

    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.of(group));
    when(mapperRepository.getMapsByGroupId(anyInt())).thenReturn(Collections.singletonList(mapper));
    assertFalse(groupServiceDao.isModerator(group, user));
  }

  @Test
  public void testIsModeratorException() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.empty());
    assertFalse(groupServiceDao.isModerator(group, user));
  }

  @Test
  public void testIsFollower() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    GroupUserMapper mapper = new GroupUserMapper();
    mapper.setGroup(group);
    mapper.setModerator(false);
    mapper.setMember(false);
    mapper.setFollower(false);
    mapper.setUser(user);
    group.setMappings(Collections.singletonList(mapper));

    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.of(group));
    when(mapperRepository.getMapsByGroupId(anyInt())).thenReturn(Collections.singletonList(mapper));
    assertFalse(groupServiceDao.isFollower(group, user));
  }

  @Test
  public void testIsFollowerException() {
    User user = User.getUserBuilder().username("Dan").password("dummypassword").build();
    Group group = Group.getBuilder()
            .name("testGroup")
            .email("testMail")
            .description("description")
            .password("password")
            .build();
    when(groupRepository.findByGroupName("testGroup")).thenReturn(Optional.empty());
    assertFalse(groupServiceDao.isFollower(group, user));
  }

  @Test(expected = GroupNotFoundException.class)
  public void testInvalidUpdatedFeed() {
    when(groupRepository.findByGroupName(anyString())).thenReturn(Optional.empty());
    groupServiceDao.updateFollowersFeed("mock", "mock message");
    fail("Should not have reached this line");
  }

  @Test
  public void testGetAllUsersForGroupAndSubGroup() {
    Group group1 = mock(Group.class);
    Group group2 = mock(Group.class);
    when(group2.getGroupName()).thenReturn("group2");
    GroupUserCompositeKey key = new GroupUserCompositeKey(1, 1);
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    GroupUserMapper mapper = new GroupUserMapper(group1, user, key, true, true, true);

    key = new GroupUserCompositeKey(2, 2);
    User user2 = User.getUserBuilder()
            .username("Jane")
            .password("123455678")
            .firstName("Jane")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    GroupUserMapper mapper2 = new GroupUserMapper(group2, user2, key, true, true, true);
    when(group1.getSubGroups()).thenReturn(new ArrayList<>(Arrays.asList(group2)));

    when(group1.getGroupID()).thenReturn(1);
    when(group2.getGroupID()).thenReturn(2);
    when(groupRepository.findByGroupName("group1")).thenReturn(Optional.of(group1));
    when(groupRepository.findByGroupName("group2")).thenReturn(Optional.of(group2));
    when(mapperRepository.getMapsByGroupId(1)).thenReturn(new ArrayList(Arrays.asList(mapper)));
    when(mapperRepository.getMapsByGroupId(2)).thenReturn(new ArrayList(Arrays.asList(mapper2)));

    when(groupRepository.findByGroupName("group")).thenReturn(Optional.of(group1));

    assertEquals(2, groupServiceDao.getAllUsersInGroupsAndSubGroups("group1").size());
  }
}
