package com.neu.prattle.service;

import com.neu.prattle.dto.UserDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.UserFeedMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/***
 * Acts as an interface between the data layer and the
 * servlet controller.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on user accounts.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 *
 */
@Service
public interface UserService {
  /***
   * Returns an optional object which might be empty or wraps an object
   * if the System contains a {@link User} object having the same name
   * as the parameter.
   *
   * @param name The name of the user
   * @return Optional object.
   */
  Optional<User> findUserByName(String name);

  /***
   * Returns an optional object which might be empty or wraps an object
   * if the System contains a {@link User} object having the same name
   * as the parameter.
   *
   * @param id the id of the user
   * @return Optional object.
   */
  Optional<User> findUserById(int id);

  /***
   * Tries to add a user in the system
   * @param user User object
   *
   */
  User addUser(User user);

  /***
   * Validates that user's credentials belong to an existing user and returns the associated user.
   * Throws an exception if there is no matching user.
   * @param name the name of the user.
   * @param password the password for the user.
   * @return the existing user with the same credentials.
   */
  User validateUser(String name, String password);

  /**
   * Returns a list of all users.
   *
   * @return list of user
   */
  List<User> getAllUsers();

  /**
   * Updates the user details and returns the updated object.
   *
   * @param userDTO object containing updates to the user object
   * @return updated user
   */
  User updateUser(UserDTO userDTO, String username) throws UserDoesNotExistException;

  /**
   * Deletes the user associated with the username.
   *
   * @param username username of the user to be deleted
   */
  void removeUser(String username) throws UserDoesNotExistException;

  /**
   * Adds a follower.
   *
   * @param username     username of user to be followed
   * @param followerName follower's username
   */
  void addFollower(String username, String followerName) throws UserDoesNotExistException;

  /**
   * Removes a follower.
   *
   * @param username     username of the user to be unfollowed
   * @param followerName follower's username
   */
  void removeFollower(String username, String followerName) throws UserDoesNotExistException;

  /**
   * Returns a list of followers.
   *
   * @param username of the user who's follower list is to be returned
   * @return associated list of followers
   */
  List<User> getAllFollowers(String username) throws UserDoesNotExistException;


  /**
   * Returns a list of users followed by the specified user.
   *
   * @param username of the user who's following list is to be returned
   * @return associated list of users followed
   */
  List<User> getAllFollowing(String username) throws UserDoesNotExistException;

  /**
   * Logs the user's last logged in time and logs him out.
   *
   * @param username name of the user
   */
  void logout(String username) throws UserDoesNotExistException;

  /**
   * Notifies all of user's followers about new feeds by the current user.
   *
   * @param username name of the user
   * @param feedText the text to display in the feed
   */
  void updateFollowersFeed(String username, String feedText) throws UserDoesNotExistException;

  /**
   * Get all the user feeds for the current user.
   *
   * @param username the username of the current user.
   * @return the list of the user feeds.
   */
  List<UserFeedMapper> getUserFeeds(String username) throws UserDoesNotExistException;

  /**
   * Returns profile picture of the user.
   *
   * @param user user
   * @return profile picture data
   */
  String getAvatar(User user);
}
