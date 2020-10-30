package com.neu.prattle.service.group;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@ComponentScan("com.neu.prattle")
@Transactional
public class GroupServiceDaoImpl implements GroupService {

  private GroupRepository groupRepository;
  private GroupUserMapperRepository mapperRepository;
  private UserFeedMapperRepository userFeedMapperRepository;

  private Logger logger = LoggerFactory.getLogger(GroupServiceDaoImpl.class);

  @Autowired
  public void setUserFeedMapperRepository(UserFeedMapperRepository userFeedMapperRepository) {
    this.userFeedMapperRepository = userFeedMapperRepository;
  }

  @Autowired
  public void setGroupRepository(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  @Autowired
  public void setGroupUserMapperRepository(GroupUserMapperRepository mapperRepository) {
    this.mapperRepository = mapperRepository;
  }

  @Override
  public Optional<Group> findGroupByName(String name) {
    return groupRepository.findByGroupName(name);
  }

  @Override
  public Group addGroup(Group group) {
    logger.info("Attempting to create group {}", group.getGroupName());
    if (groupRepository.findByGroupName(group.getGroupName()).isPresent()) {
      logger.error("Group {} already exists", group.getGroupName());
      throw new GroupAlreadyPresentException(String.format("Group already present with name: %s",
              group.getGroupName()));
    }
    logger.info("Group {} created", group.getGroupName());
    return groupRepository.save(group);
  }

  @Override
  public Group addSubGroup(Group parentGroup, Group subGroup) {
    parentGroup.getSubGroups().add(subGroup);
    subGroup.getParentGroups().add(parentGroup);
    groupRepository.save(parentGroup);
    groupRepository.save(subGroup);
    logger.info("Group {} added to group {}", subGroup.getGroupName(), parentGroup.getGroupName());
    return parentGroup;
  }

  @Override
  public List<Group> getSubgroups(Group group) {
    logger.info("Getting subgroups of group {}", group.getGroupName());
    return groupRepository.fetchSubGroups(group.getGroupID());
  }

  @Override
  public List<User> getMemberTypeByGroupName(String groupName, boolean isMember,
                                             boolean isModerator,
                                             boolean isFollower) {
    logger.info("Getting users in group {}", groupName);
    Optional<Group> optionalGroup = groupRepository.findByGroupName(groupName);
    if (optionalGroup.isPresent()) {
      Group targetGroup = optionalGroup.get();
      List<GroupUserMapper> gums = mapperRepository.getMapsByGroupId(targetGroup.getGroupID());
      if (isModerator) {
        gums = gums.stream().filter(GroupUserMapper::isModerator).collect(Collectors.toList());
      }
      if (isMember) {
        gums = gums.stream().filter(GroupUserMapper::isMember).collect(Collectors.toList());
      }
      if (isFollower) {
        gums = gums.stream().filter(GroupUserMapper::isFollower).collect(Collectors.toList());
      }
      List<User> targetGroupUsers = gums.stream().map(GroupUserMapper::getUser).collect(Collectors.toList());
      for (Group subgroup : targetGroup.getSubGroups()) {
        targetGroupUsers.addAll(getMemberTypeByGroupName(subgroup.getGroupName(), isMember, isModerator, isFollower));
      }
      return targetGroupUsers;
    }
    logger.error("Group {} not found", groupName);
    throw new GroupNotFoundException(String.format("Group with name: %s does not exist!",
            groupName));
  }

  @Override
  public List<User> getAllUsersInGroupsAndSubGroups(String groupName) {
    Optional<Group> optionalGroup = groupRepository.findByGroupName(groupName);
    if (optionalGroup.isPresent()) {
      Group group = optionalGroup.get();
      List<GroupUserMapper> gums = mapperRepository.getMapsByGroupId(group.getGroupID());
      List<User> targetGroupUsers = gums.stream().map(GroupUserMapper::getUser).collect(Collectors.toList());
      for (Group subGroup : group.getSubGroups()) {
        targetGroupUsers.addAll(getAllUsersInGroupsAndSubGroups(subGroup.getGroupName()));
      }
      return targetGroupUsers;
    }
    throw new GroupNotFoundException("Couldnt find a group by that name");
  }

  @Override
  public boolean isMember(Group group, User user) {
    List<Group> parentGroups = getParentGroups(group);
    List<User> members = new ArrayList<>();
    try {
      parentGroups.forEach(g -> members.addAll(getMemberTypeByGroupName(g.getGroupName(), true,
              false, false)));
      return this.getMemberTypeByGroupName(group.getGroupName(), true, false, false).contains(user)
              || members.contains(user);
    } catch (GroupNotFoundException e) {
      return false;
    }
  }

  @Override
  public boolean isModerator(Group group, User user) {
    try {
      return this.getMemberTypeByGroupName(group.getGroupName(), false, true, false).contains(user);
    } catch (GroupNotFoundException e) {
      return false;
    }
  }

  @Override
  public boolean isFollower(Group group, User user) {
    try {
      return this.getMemberTypeByGroupName(group.getGroupName(), false, false, true).contains(user);
    } catch (GroupNotFoundException e) {
      return false;
    }
  }

  @Override
  public boolean isGroup(String groupName) {
    return findGroupByName(groupName).isPresent();
  }

  @Override
  public void addMemberToGroup(Group group, User member, boolean isModerator, boolean isFollower,
                               boolean isMember) {
    GroupUserMapper map = new GroupUserMapper(group, member,
            new GroupUserCompositeKey(group.getGroupID(), member.getUserID()), isModerator, isFollower,
            isMember);
    mapperRepository.save(map);
  }

  @Override
  public Optional<Group> findGroupById(int id) {
    return groupRepository.findByGroupID(id);
  }

  @Override
  public List<Group> findGroups() {
    return StreamSupport.stream(groupRepository.findAll().spliterator(),
            false).collect(Collectors.toList());
  }

  @Override
  public List<Group> getGroupsForUser(User user) {
    List<Group> result = new ArrayList<>();
    List<GroupUserMapper> mapping = user.getMappings();
    for (GroupUserMapper gum : mapping) {
      if (gum.getUser() == user && gum.isMember()) {
        result.add(gum.getGroup());
      }
    }
    return result;
  }

  @Override
  public void updateGroup(GroupDTO groupDTO, String groupName) {
    Optional<Group> optionalGroup = findGroupByName(groupName);
    if (!optionalGroup.isPresent()) {
      logger.error("Group {} not found", groupName);
      throw new GroupNotFoundException("Group not found");
    }
    Group groupToBeUpdated = optionalGroup.get();
    updateGroupName(groupToBeUpdated, groupDTO);
    updateGroupPassword(groupToBeUpdated, groupDTO);
    updateGroupDescription(groupToBeUpdated, groupDTO);
    groupRepository.save(groupToBeUpdated);
  }

  @Override
  public List<Group> getParentGroups(Group group) {
    return groupRepository.fetchParentGroups(group.getGroupID());
  }

  @Override
  public void acceptInvite(Group group, User user) {
    List<GroupUserMapper> list = mapperRepository.getMapsByGroupId(group.getGroupID()).
            stream().filter(x -> x.getUser() == user).collect(Collectors.toList());
    for (GroupUserMapper mapper : list) {
      mapper.setMember(true);
      mapperRepository.save(mapper);
    }
    logger.info("Invite for user {} has been accepted", user.getUsername());
    updateFollowersFeed(group.getGroupName(), "User "
            + user + " has been added to group " + group.getGroupName());
  }

  @Override
  public void rejectInvite(Group group, User user) {
    List<GroupUserMapper> list = mapperRepository.getMapsByGroupId(group.getGroupID()).
            stream().filter(x -> x.getUser() == user).collect(Collectors.toList());
    for (GroupUserMapper mapper : list) {
      mapperRepository.delete(mapper);
    }
    logger.info("Invite for user {} has been rejected", user.getUsername());
  }

  @Override
  public List<User> getInvites(Group group) {
    List<GroupUserMapper> gums = mapperRepository.getMapsByGroupId(group.getGroupID())
            .stream().filter(x -> !x.isMember()).collect(Collectors.toList());
    List<User> invites = new ArrayList<>();
    for (GroupUserMapper mapper : gums) {
      invites.add(mapper.getUser());
    }
    logger.info("Retrieved {} invites", invites.size());
    return invites;
  }

  @Override
  public void updateFollowersFeed(String groupName, String feedText) {
    Optional<Group> currentGroup = findGroupByName(groupName);
    if (currentGroup.isPresent()) {
      List<GroupUserMapper> followers = currentGroup.get().getMappings();
      followers = followers.stream().filter(GroupUserMapper::isFollower).collect(Collectors.toList());
      for (GroupUserMapper follower : followers) {
        UserFeedMapper newFeed = new UserFeedMapper(follower.getUser(), feedText,
                Timestamp.valueOf(LocalDateTime.now()));
        userFeedMapperRepository.save(newFeed);
      }
    } else {
      throw new GroupNotFoundException("Can't find a group with that name");
    }
  }

  /**
   * Helper to update group description.
   *
   * @param groupToBeUpdated group to be updated
   * @param groupDTO         dto representing updates
   */
  private void updateGroupDescription(Group groupToBeUpdated, GroupDTO groupDTO) {
    if (groupDTO.getDescription() != null) {
      groupToBeUpdated.setDescription(groupDTO.getDescription());
      updateFollowersFeed(groupToBeUpdated.getGroupName(),
              "Description of group " + groupToBeUpdated +
                      " has been updated to \"" + groupDTO.getDescription() + "\"");
    }
  }

  /**
   * Helper to update group password.
   *
   * @param groupToBeUpdated group to be updated
   * @param groupDTO         dto representing updates
   */
  private void updateGroupPassword(Group groupToBeUpdated, GroupDTO groupDTO) {
    if (groupDTO.getPassword() != null) {
      groupToBeUpdated.setPassword(groupDTO.getPassword());
      updateFollowersFeed(groupToBeUpdated.getGroupName(),
              "Password of group " + groupToBeUpdated +
                      " has been changed. Contact the moderators for the new password. ");
    }
  }

  /**
   * Helper to update group name.
   *
   * @param groupToBeUpdated group to be updated
   * @param groupDTO         dto representing updates
   */
  private void updateGroupName(Group groupToBeUpdated, GroupDTO groupDTO) {
    if (groupDTO.getGroupName() != null) {
      groupToBeUpdated.setGroupName(groupDTO.getGroupName());
      updateFollowersFeed(groupDTO.getGroupName(),
              "Group " + groupToBeUpdated + " has been renamed to " + groupDTO.getGroupName());
    }
  }
}
