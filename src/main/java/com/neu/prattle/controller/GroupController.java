package com.neu.prattle.controller;

import com.neu.prattle.dto.GroupDTO;
import com.neu.prattle.dto.GroupMemberDTO;
import com.neu.prattle.dto.SubGroupDTO;
import com.neu.prattle.exceptions.GroupNotFoundException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;
import com.neu.prattle.model.group.GroupUserMapper;
import com.neu.prattle.repository.group.GroupRepository;
import com.neu.prattle.repository.group.GroupUserMapperRepository;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.group.GroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping(path = "/rest/group")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com", "http://localhost:3000"})
public class GroupController {

  private GroupService groupService;

  private UserService userService;

  private Logger logger = LoggerFactory.getLogger(GroupController.class);

  private GroupRepository groupRepository;

  private GroupUserMapperRepository groupUserMapperRepository;

  @Autowired
  public void setGroupUserMapperRepository(GroupUserMapperRepository groupUserMapperRepository) {
    this.groupUserMapperRepository = groupUserMapperRepository;
  }

  @Autowired
  public void setGroupRepository(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  @Autowired
  public void setGroupService(GroupService groupService) {
    this.groupService = groupService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  /**
   * Creates a group.
   *
   * @param groupDTO dto representing group
   * @return resultant response
   */
  @PostMapping(value = "/create")
  public Response createGroup(@RequestBody GroupDTO groupDTO) {
    logger.info("Attempting to create group {} ...", groupDTO.getGroupName());
    Group groupToBeAdded = new Group(groupDTO);
    if (groupService.findGroupByName(groupToBeAdded.getGroupName()).isPresent()) {
      logger.error("Group {} already exists", groupDTO.getGroupName());
      return Response.status(500, "Group already exists").build();
    }
    groupService.addGroup(groupToBeAdded);
    Optional<User> optionalModerator = userService.findUserByName(groupDTO.getModeratorName());
    if (optionalModerator.isPresent()) {
      User moderator = optionalModerator.get();
      groupService.addMemberToGroup(groupToBeAdded, moderator, true, true, true);
      logger.info("Group {} created", groupDTO.getGroupName());
      return Response.ok().build();
    }
    logger.error("User {} created", groupDTO.getModeratorName());
    return Response.status(500, "Moderator not found").build();
  }

  /**
   * Adds member to group.
   *
   * @param groupMemberDTO dto representing user
   * @return resultant response
   */
  @PutMapping(value = "/addmember/{requesterName}")
  public Response addMember(@RequestBody GroupMemberDTO groupMemberDTO, @PathVariable String requesterName) {
    logger.info("Attempting to add user {} to group {}", groupMemberDTO.getMemberName(),
            groupMemberDTO.getGroupName());
    Optional<User> optionalUser = this.userService.findUserByName(groupMemberDTO.getMemberName());
    if (!optionalUser.isPresent()) {
      logger.error("User {} not found", groupMemberDTO.getMemberName());
      return Response.status(500, "User being invited does not exist").build();
    }
    User potentialMember = optionalUser.get();
    Optional<User> optionalRequester = userService.findUserByName(requesterName);
    if (!optionalRequester.isPresent()) {
      logger.error("Requester {} not found", groupMemberDTO.getMemberName());
      return Response.status(500, "User initiating the invite does not exist").build();
    }
    User requester = optionalRequester.get();
    Optional<Group> optionalGroup = this.groupService.findGroupByName(groupMemberDTO.getGroupName());
    if (!optionalGroup.isPresent()) {
      logger.error("Group {} not found", groupMemberDTO.getGroupName());
      return Response.status(500, "Group not found").build();
    }
    Group group = optionalGroup.get();
    if (groupService.isModerator(group, requester)) {
      this.groupService.addMemberToGroup(group, potentialMember,
              groupMemberDTO.isModerator(), groupMemberDTO.isFollower(), groupMemberDTO.isMember());
    } else {
      groupService.addMemberToGroup(group, potentialMember, false, false, false);
    }
    logger.info("User {} added to group {}", groupMemberDTO.getMemberName(),
            groupMemberDTO.getGroupName());
    return Response.ok().build();
  }

  /**
   * Returns all members part of specified group.
   *
   * @param groupName name of the group
   * @return list of members
   */
  @GetMapping(value = "/getmembersbygroup/{groupName}")
  public @ResponseBody
  List<User> getMembersByGroupName(@PathVariable("groupName") String groupName) {
    logger.info("Attempting to return members of group {}", groupName);
    return groupService.getMemberTypeByGroupName(groupName, true, false, false);
  }

  /**
   * Returns group.
   *
   * @param group name of group
   * @return resultant group
   */
  @GetMapping(value = "/{group}")
  public Group getGroup(@PathVariable String group) {
    logger.info("Attempting to return group {}", group);
    return groupService.findGroupByName(group).orElse(null);
  }

  /**
   * Returns list of all groups.
   *
   * @return list of all groups
   */
  @GetMapping(value = "/allgroups")
  public List<Group> getAllGroups() {
    logger.info("Attempting to return all groups");
    return groupService.findGroups();
  }

  /**
   * Return groups that the specified user is a part of.
   *
   * @param username name of the user
   * @return list of groups
   */
  @GetMapping(value = "/mygroup/{username}")
  public List<Group> getMyGroups(@PathVariable String username) {
    logger.info("Attempting to return all groups that {} is part of", username);
    Optional<User> optionalUser = userService.findUserByName(username);
    if (!optionalUser.isPresent()) {
      logger.info("{} is not part of any groups", username);
      return new ArrayList<>();
    }
    logger.info("Returned groups for user {}", username);
    return groupService.getGroupsForUser(optionalUser.get());
  }

  /**
   * Updates group.
   *
   * @param groupDTO  dto representing changes
   * @param groupName name of  group
   * @return resultant response
   */
  @PutMapping(value = "/update/{groupName}")
  public Response updateGroup(@RequestBody GroupDTO groupDTO, @PathVariable String groupName) {
    try {
      logger.info("Attempting to update group {}", groupName);
      groupService.updateGroup(groupDTO, groupName);
    } catch (GroupNotFoundException e) {
      logger.error("Group {} which is being attempted to be updated not found", groupName);
      return Response.status(500, e.getMessage()).build();
    }
    logger.info("Group {} updated", groupName);
    return Response.ok().build();
  }

  /**
   * Returns list of moderators.
   *
   * @param groupName name of group
   * @return list of moderators
   */
  @GetMapping(value = "/moderators/{groupName}")
  public List<User> getModerators(@PathVariable String groupName) {
    logger.info("Attempting to get moderators of group {}", groupName);
    Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
    List<User> resultUsers = new ArrayList<>();
    if (optionalGroup.isPresent()) {
      List<GroupUserMapper> mapper = optionalGroup.get().getMappings();
      for (GroupUserMapper mapping : mapper) {
        if (mapping.isModerator()) {
          logger.info("Retrieved moderators of group {}", groupName);
          resultUsers.add(mapping.getUser());
        }
      }
    }
    logger.error("Group {} for which moderators are being requested not found", groupName);
    return resultUsers;
  }

  @PutMapping(value = "/moderators/{groupName}/{username}/{requesterName}")
  public Response addModerator(@PathVariable String groupName,
                               @PathVariable String username, @PathVariable String requesterName) {
    return toggleModeratorStatus(groupName, username, requesterName, true);
  }

  @DeleteMapping(value = "/moderators/{groupName}/{username}/{requesterName}")
  public Response removeModerator(@PathVariable String groupName,
                                  @PathVariable String username, @PathVariable String requesterName) {
    return toggleModeratorStatus(groupName, username, requesterName, false);
  }

  /**
   * Private helper.
   *
   * @param groupName     name of the group
   * @param username      name of the user
   * @param requesterName name of the requester
   * @param isModerator   true if set to moderator, else false
   * @return resultant response
   */
  private Response toggleModeratorStatus(String groupName, String username, String requesterName,
                                         boolean isModerator) {

    Group group = getGroup(groupName);
    if (group == null) {
      return Response.status(400, "Group does not exist").build();
    }
    if (!checkIfModerator(requesterName, groupName)) {
      return Response.status(400, "Requester is not a moderator").build();
    }
    User user = getUser(username);
    if (user == null) {
      return Response.status(400, "User being modified does not exist").build();
    }
    List<GroupUserMapper> mappings = group.getMappings();
    boolean matchFound = false;
    for (GroupUserMapper mapping : mappings) {
      if (mapping.getUser() == user) {
        mapping.setModerator(isModerator);
        groupUserMapperRepository.save(mapping);
        groupRepository.save(group);
        matchFound = true;
        break;
      }
    }
    if (matchFound) {
      return Response.ok().build();
    }
    return Response.status(400, "User does not exist in the group").build();
  }

  private User getUser(String username) {
    Optional<User> optionalUser = userService.findUserByName(username);
    return optionalUser.orElse(null);
  }

  /**
   * Adds users as a follower.
   *
   * @param username  name of the user
   * @param groupName name of group
   * @return resultant response
   */
  @PutMapping(value = "/{username}/follow/{groupName}")
  public Response followGroup(@PathVariable String username, @PathVariable String groupName) {
    try {
      logger.info("User {} requesting to follow group {}", username, groupName);
      Group group = returnGroup(groupName);
      User user = returnUser(username);
      doFollowOrUnfollow(group, user, true);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return Response.status(500, e.getMessage()).build();
    }
    logger.info("User {} now follows group {}", username, groupName);
    return Response.ok().build();
  }

  /**
   * Unfollows user from group
   *
   * @param username  name of the user
   * @param groupName name of group
   * @return resultant response
   */
  @DeleteMapping(value = "/{username}/unfollow/{groupName}")
  public Response unfollowGroup(@PathVariable String username, @PathVariable String groupName) {
    try {
      logger.info("User {} requesting to unfollow group {}", username, groupName);
      Group group = returnGroup(groupName);
      User user = returnUser(username);
      doFollowOrUnfollow(group, user, false);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return Response.status(500, e.getMessage()).build();
    }
    logger.info("User {} no longer follows group {}", username, groupName);
    return Response.ok().build();
  }

  /**
   * Returns followers.
   *
   * @param groupName group name
   * @return list of followers
   */
  @GetMapping(value = "/{groupName}/followers")
  public List<User> getFollowers(@PathVariable String groupName) {
    logger.info("Attempting to retrieve followers of group {}", groupName);

    Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
    if (!optionalGroup.isPresent()) {
      logger.error("Group {} for which followers are requested not found", groupName);
      return new ArrayList<>();
    }
    Group group = optionalGroup.get();
    List<GroupUserMapper> mapperList = group.getMappings();
    List<User> result = new ArrayList<>();
    for (GroupUserMapper mapper : mapperList) {
      if (mapper.isFollower()) {
        result.add(mapper.getUser());
      }
    }
    logger.info("Followers of group {} retrieved successfully", groupName);
    return result;
  }

  @PostMapping(value = "/addSubGroup")
  public Response addSubGroup(@RequestBody SubGroupDTO subGroupDTO) {
    Group parent = groupService.findGroupById(subGroupDTO.getParentId()).orElse(null);
    Group child = groupService.findGroupById(subGroupDTO.getChildId()).orElse(null);
    if (parent == null) {
      return Response.status(500, "Parent group does not exist!").build();
    } else if (child == null) {
      return Response.status(500, "Child group does not exist!").build();
    }
    groupService.addSubGroup(parent, child);
    return Response.ok(parent).build();
  }

  /**
   * Private helper.
   *
   * @param username name of user
   * @throws UserDoesNotExistException when user does not exist
   */
  private User returnUser(String username) throws UserDoesNotExistException {
    Optional<User> optionalUser = userService.findUserByName(username);
    if (!optionalUser.isPresent()) {
      throw new UserDoesNotExistException("User does not exist");
    }
    return optionalUser.get();
  }

  private void doFollowOrUnfollow(Group group, User user, boolean follow) throws UserDoesNotExistException {
    if (follow) {
      followSteps(group, user);
    } else {
      unfollowSteps(group, user);
    }
  }

  /**
   * Private helper
   *
   * @param group group object
   * @param user  user object
   * @throws UserDoesNotExistException when user does not exist
   */
  private void unfollowSteps(Group group, User user) throws UserDoesNotExistException {
    List<GroupUserMapper> mappers = group.getMappings();
    for (GroupUserMapper mapping : mappers) {
      if (mapping.getUser() == user) {
        mapping.setFollower(false);
        groupService.updateFollowersFeed(group.getGroupName(), user.getUsername() + " unfollowed " + group.getGroupName());
        groupUserMapperRepository.save(mapping);
        groupRepository.save(group);
        return;
      }
    }
    throw new UserDoesNotExistException("User does not follow group");
  }

  /**
   * Private helper.
   */
  private void followSteps(Group group, User user) {
    List<GroupUserMapper> mappers = group.getMappings();
    if (groupService.isModerator(group, user) || groupService.isMember(group, user)) {
      for (GroupUserMapper mapping : mappers) {
        if (mapping.getUser() == user) {
          mapping.setFollower(true);
          groupUserMapperRepository.save(mapping);
        }
      }
      groupRepository.save(group);
    } else {
      groupService.addMemberToGroup(group, user, false, true, false);
    }
    groupService.updateFollowersFeed(group.getGroupName(), user.getUsername() + " started following " + group.getGroupName());
  }

  /**
   * Private helper.
   */
  private Group returnGroup(String groupName) {
    Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
    if (!optionalGroup.isPresent()) {
      throw new GroupNotFoundException("Group does not exist");
    }
    return optionalGroup.get();
  }

  @GetMapping(value = "/{groupName}/{username}/isModerator")
  public boolean checkIfModerator(@PathVariable String username, @PathVariable String groupName) {
    Group group;
    User user;
    try {
      group = returnGroup(groupName);
      user = returnUser(username);
    } catch (Exception e) {
      return false;
    }
    return this.groupService.isModerator(group, user);
  }

  @GetMapping(value = "/{groupName}/{username}/isMember")
  public boolean checkIfMember(@PathVariable String username, @PathVariable String groupName) {
    Group group;
    User user;
    try {
      group = returnGroup(groupName);
      user = returnUser(username);
    } catch (Exception e) {
      return false;
    }
    return this.groupService.isMember(group, user);
  }

  @GetMapping(value = "/{groupName}/{username}/isFollower")
  public boolean checkIfFollower(@PathVariable String username, @PathVariable String groupName) {
    Group group;
    User user;
    try {
      group = returnGroup(groupName);
      user = returnUser(username);
    } catch (Exception e) {
      return false;
    }
    return this.groupService.isFollower(group, user);
  }

  @PutMapping(value = "/invite/{groupName}/accept/{username}")
  public Response acceptInvite(@PathVariable String groupName, @PathVariable String username) {
    Group group;
    User user;
    try {
      group = returnGroup(groupName);
      user = returnUser(username);
      groupService.acceptInvite(group, user);
    } catch (Exception e) {
      return Response.status(500, "User or group does not exist").build();
    }
    return Response.ok().build();
  }

  @DeleteMapping(value = "/invite/{groupName}/reject/{username}")
  public Response rejectInvite(@PathVariable String groupName, @PathVariable String username) {
    Group group;
    User user;
    try {
      group = returnGroup(groupName);
      user = returnUser(username);
      groupService.rejectInvite(group, user);
    } catch (Exception e) {
      return Response.status(500, "User or group does not exist").build();
    }
    return Response.ok().build();
  }

  @GetMapping(value = "/{groupName}/invites")
  public List<User> getInvites(@PathVariable String groupName) {
    Group group;
    try {
      group = returnGroup(groupName);
    } catch (Exception e) {
      return new ArrayList<>();
    }
    return groupService.getInvites(group);
  }

  @GetMapping(value = "/{groupName}/groupID")
  public int getGroupID(@PathVariable String groupName) {
    Optional<Group> optionalGroup = groupService.findGroupByName(groupName);
    return optionalGroup.map(Group::getGroupID).orElse(-1);
  }


  @GetMapping(value = "/{groupID}/groupName")
  public String getGroupName(@PathVariable int groupID) {
    Optional<Group> optionalGroup = groupService.findGroupById(groupID);
    return optionalGroup.map(Group::getGroupName).orElse(null);
  }
}
