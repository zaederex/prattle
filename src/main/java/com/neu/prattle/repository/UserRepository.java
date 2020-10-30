package com.neu.prattle.repository;

import com.neu.prattle.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface associated with the User table to support basic CRUD operations on the User
 * table.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  /**
   * Searches and returns a user
   *
   * @param username name of the user to be searched
   * @return optional object of user
   */
  Optional<User> findByUsername(String username);
}
