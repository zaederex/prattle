package com.neu.prattle.service;

import com.neu.prattle.dto.UserDTO;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;
import com.neu.prattle.model.UserFeedMapper;
import com.neu.prattle.repository.UserFeedMapperRepository;
import com.neu.prattle.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.neu.prattle.utils.FileHandler.createFileForMediaTypeData;
import static com.neu.prattle.utils.FileHandler.getOriginalMediaTypeData;

/**
 * Implements the {@link com.neu.prattle.service.UserService} interface to persist user objects to a
 * database.
 */
@Service
public class UserServiceDaoImpl extends AbstractUserService {

  private static final String USER_DOES_NOT_EXIST = "User does not exist";
  private Logger logger = LoggerFactory.getLogger(UserServiceDaoImpl.class);

  private UserRepository userRepository;
  private UserFeedMapperRepository userFeedMapperRepository;

  private PasswordEncoder passwordEncoder;

  @Autowired
  public void setUserFeedMapperRepository(UserFeedMapperRepository userFeedMapperRepository) {
    this.userFeedMapperRepository = userFeedMapperRepository;
  }

  @Autowired
  @Override
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> findUserByName(String name) {
    return userRepository.findByUsername(name);
  }

  @Override
  public Optional<User> findUserById(int id) {
    return userRepository.findById(id);
  }

  @Override
  public User addUser(User user) {
    if (findUserByName(user.getUsername()).isPresent()) {
      throw new UserAlreadyPresentException(String.format("User already present with name: %s",
              user.getUsername()));
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    if (user.getProfilePicturePath() != null) {
      user.setProfilePicturePath(createFileForMediaTypeData(user.getProfilePicturePath()));
    }
    User resultantUser = userRepository.save(user);
    if (resultantUser.getProfilePicturePath() != null) {
      resultantUser.setProfilePicturePath(getOriginalMediaTypeData(user.getProfilePicturePath()));
    }
    return resultantUser;
  }

  @Override
  public User validateUser(String name, String password) {
    Optional<User> existingUser = findUserByName(name);
    if (existingUser.isPresent() && passwordEncoder.matches(password,
            existingUser.get().getPassword())) {
      return existingUser.get();
    }
    throw new IllegalStateException("Invalid username/password.");
  }

  @Override
  public List<User> getAllUsers() {
    return StreamSupport.stream(userRepository.findAll().spliterator(),
            false).collect(Collectors.toList());
  }

  @Override
  public User updateUser(UserDTO userDTO, String username) throws UserDoesNotExistException {
    Optional<User> toUpdateUser = findUserByName(username);
    if (!toUpdateUser.isPresent()) {
      logger.error("User {} does not exist", userDTO.getUsername());
      throw new UserDoesNotExistException("User to be updated does not exist");
    }
    logger.info("User {} updated", userDTO.getUsername());
    updateFollowersFeed(username, username + "'s user details has been updated");
    User resultantUser = userRepository.save(performUpdates(toUpdateUser.get(), userDTO));
    if (resultantUser.getProfilePicturePath() != null) {
      resultantUser.setProfilePicturePath(getOriginalMediaTypeData(resultantUser.getProfilePicturePath()));
    }
    return resultantUser;
  }

  @Override
  public void removeUser(String username) throws UserDoesNotExistException {
    Optional<User> toDeleteUser = findUserByName(username);
    if (!toDeleteUser.isPresent()) {
      throw new UserDoesNotExistException("User to be deleted doesn't exist");
    }
    userRepository.delete(toDeleteUser.get());
  }

  @Override
  public void addFollower(String username, String followerName) throws UserDoesNotExistException {
    User user = returnUser(username, "User to be followed does not exists");
    User follower = returnUser(followerName, "Follower does not exist");
    user.getFollowers().add(follower);
    follower.getFollowees().add(user);
    updateFollowersFeed(username, followerName + " started following user " + username);
    userRepository.save(user);
    userRepository.save(follower);
  }

  @Override
  public void removeFollower(String username, String followerName) throws UserDoesNotExistException {
    User user = returnUser(username, "User to be unfollowed does not exists");
    User follower = returnUser(followerName, "Follower does not exist");
    user.getFollowers().remove(follower);
    follower.getFollowees().remove(user);
    updateFollowersFeed(followerName, followerName + " unfollowed user " + username);
    userRepository.save(user);
    userRepository.save(follower);
  }

  @Override
  public List<User> getAllFollowers(String username) throws UserDoesNotExistException {
    logger.info("Attempting to get followers of {}", username);
    return new ArrayList<>(returnUser(username, USER_DOES_NOT_EXIST).getFollowers());
  }

  @Override
  public List<User> getAllFollowing(String username) throws UserDoesNotExistException {
    logger.info("Attempting to get followees of {}", username);
    return new ArrayList<>(returnUser(username, USER_DOES_NOT_EXIST).getFollowees());
  }

  @Override
  public void logout(String username) throws UserDoesNotExistException {
    User user = returnUser(username, "User to be followed does not exists");
    user.setLogOutTimestamp(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
    updateFollowersFeed(user.getUsername(), user.getUsername() + " has logged out");
    userRepository.save(user);
  }

  @Override
  public void updateFollowersFeed(String username, String feedText) throws UserDoesNotExistException {
    logger.info("Attempting to update the feeds of all followers of {}", username);
    Optional<User> currentUser = findUserByName(username);
    if (currentUser.isPresent()) {
      List<User> followers = getAllFollowers(username);
      for (User follower : followers) {
        UserFeedMapper newFeed = new UserFeedMapper(follower, feedText,
                Timestamp.valueOf(LocalDateTime.now()));
        userFeedMapperRepository.save(newFeed);
      }
    } else {
      throw new UserDoesNotExistException("Can't find a user with this name");
    }
  }

  @Override
  public List<UserFeedMapper> getUserFeeds(String username) throws UserDoesNotExistException {
    logger.info("Attempting to get followees of {}", username);
    return new ArrayList<>(returnUser(username, USER_DOES_NOT_EXIST).getUserFeeds());
  }

  @Override
  public String getAvatar(User user) {
    return getOriginalMediaTypeData(user.getProfilePicturePath());
  }
}
