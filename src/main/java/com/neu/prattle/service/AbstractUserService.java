package com.neu.prattle.service;

import com.neu.prattle.dto.UserDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.TimeZone;

import static com.neu.prattle.utils.FileHandler.createFileForMediaTypeData;

/**
 * Abstraction of user service implementations.
 */
public abstract class AbstractUserService implements UserService {

  private PasswordEncoder passwordEncoder;

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Returns updated user.
   *
   * @param toUpdateUser user to be updated
   * @param userDTO      dto object representing changes
   * @return updated user
   */

  User performUpdates(User toUpdateUser, UserDTO userDTO) {
    updatePassword(userDTO, toUpdateUser);
    updateFirstName(userDTO, toUpdateUser);
    updateLastName(userDTO, toUpdateUser);
    updateContactNumber(userDTO, toUpdateUser);
    updateTimezone(userDTO, toUpdateUser);
    updateProfilePicture(userDTO, toUpdateUser);
    return toUpdateUser;
  }

  /**
   * Updates password.
   *
   * @param userDTO      dto object representing changes
   * @param originalUser user to be updated
   */
  private void updatePassword(UserDTO userDTO, User originalUser) {
    if (userDTO.getPassword() != null) {
      originalUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }
  }

  /**
   * Updates first name.
   *
   * @param userDTO      dto object representing changes
   * @param originalUser user to be updated
   */
  private void updateFirstName(UserDTO userDTO, User originalUser) {
    if (userDTO.getFirstName() != null) {
      originalUser.setFirstName(userDTO.getFirstName());
    }
  }

  /**
   * Updates last name.
   *
   * @param userDTO      dto object representing changes
   * @param originalUser user to be updated
   */
  private void updateLastName(UserDTO userDTO, User originalUser) {
    if (userDTO.getLastName() != null) {
      originalUser.setLastName(userDTO.getLastName());
    }
  }

  /**
   * Updates contact number.
   *
   * @param userDTO      dto object representing changes
   * @param originalUser user to be updated
   */
  private void updateContactNumber(UserDTO userDTO, User originalUser) {
    if (userDTO.getContactNumber() != null) {
      originalUser.setContactNumber(userDTO.getContactNumber());
    }
  }

  /**
   * Updates timezone.
   *
   * @param userDTO      dto object representing changes
   * @param originalUser user to be updated
   */
  private void updateTimezone(UserDTO userDTO, User originalUser) {
    if (userDTO.getTimezone() != null) {
      originalUser.setTimezone(TimeZone.getTimeZone(userDTO.getTimezone()));
    }
  }

  private void updateProfilePicture(UserDTO userDTO, User originalUser) {
    if (userDTO.getProfilePicturePath() != null) {
      originalUser.setProfilePicturePath(createFileForMediaTypeData(userDTO.getProfilePicturePath()));
    }
  }

  /**
   * Returns a user if exists.
   *
   * @param username     username of the user
   * @param errorMessage message to be printed when user doesn't exist
   * @return user object
   */
  User returnUser(String username, String errorMessage) throws UserDoesNotExistException {
    Optional<User> optionalUser = findUserByName(username);
    if (!optionalUser.isPresent()) {
      throw new UserDoesNotExistException(errorMessage);
    }
    return optionalUser.get();
  }
}
