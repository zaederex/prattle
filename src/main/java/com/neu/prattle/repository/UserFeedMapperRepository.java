package com.neu.prattle.repository;

import com.neu.prattle.model.UserFeedMapper;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFeedMapperRepository extends CrudRepository<UserFeedMapper, Integer> {
}
