package com.neu.prattle.service.group;

import com.neu.prattle.dto.GroupDTO;
import com.neu.prattle.model.User;
import com.neu.prattle.model.group.Group;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class associated with {@link com.neu.prattle.controller.GroupController}.
 */
@Service
public interface GroupService {

  /**
   * Searches for group by name.
   *
   * @param name name of group
   * @return optional object
   */
  Optional<Group> findGroupByName(String name);

  /**
   * Adds a group.
   *
   * @param group group to be added
   * @return resultant group
   */
  Group addGroup(Group group);

  /**
   * Return a list of members based on specified type.
   *
   * @param groupName   name of group
   * @param isMember    true if member, else false
   * @param isModerator true if moderator, else false
   * @param isFollower  true if follower, else false
   * @return list of matched users
   */
  List<User> getMemberTypeByGroupName(String groupName, boolean isMember, boolean isModerator,
                                      boolean isFollower);

  List<User> getAllUsersInGroupsAndSubGroups(String groupName);

  Group addSubGroup(Group parentGroup, Group subGroup);

  List<Group> getSubgroups(Group group);

  /**
   * Checks if user is member.
   *
   * @param group group
   * @param user  user
   * @return true if member, else false
   */
  boolean isMember(Group group, User user);


  /**
   * Checks if user is moderator.
   *
   * @param group group
   * @param user  user
   * @return true if moderator, else false
   */
  boolean isModerator(Group group, User user);

  /**
   * Checks if user is follower.
   *
   * @param group group
   * @param user  user
   * @return true if follower, else false
   */
  boolean isFollower(Group group, User user);


  /**
   * Checks if specified group exists.
   *
   * @param groupName group name
   * @return true if exist, else false
   */
  boolean isGroup(String groupName);

  /**
   * Adds user to group.
   *
   * @param group       group
   * @param member      member to be added
   * @param isModerator true if moderator, else false
   * @param isFollower  true if follower, else false
   * @param isMember    true if member, else false
   */
  void addMemberToGroup(Group group, User member, boolean isModerator, boolean isFollower,
                        boolean isMember);

  /**
   * Searches for a group.
   *
   * @param id id of the group
   * @return resultant optional object
   */
  Optional<Group> findGroupById(int id);

  /**
   * Returns list of groups.
   *
   * @return list of groups
   */
  List<Group> findGroups();

  /**
   * Gets all groups associated with a user
   *
   * @param user user
   * @return list of groups
   */
  List<Group> getGroupsForUser(User user);

  /**
   * Updates group
   *
   * @param group     group updates
   * @param groupName name of group
   */
  void updateGroup(GroupDTO group, String groupName);

  List<Group> getParentGroups(Group group);

  /**
   * Accepts an invite.
   *
   * @param group group
   * @param user  invited user
   */
  void acceptInvite(Group group, User user);

  /**
   * Rejects an invite.
   *
   * @param group group
   * @param user  invited user
   */
  void rejectInvite(Group group, User user);

  /**
   * Returns a list of users who are invited to the specified group.
   *
   * @param group group
   * @return list of invited users
   */
  List<User> getInvites(Group group);

  /**
   * Notifies all of groups followers about new feeds by the current user.
   *
   * @param groupName name of the group
   * @param feedText  the text to display in the feed
   */
  void updateFollowersFeed(String groupName, String feedText);
}
