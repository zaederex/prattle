package com.neu.prattle.repository;

import com.neu.prattle.model.UserIPMapper;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserIPMapperRepository extends CrudRepository<UserIPMapper, Integer> {
  @Transactional
  @Modifying
  @Query("update UserIPMapper uip"
          + " set uip.ipAddress = :ipAddress WHERE uip.userID = :user_id")
  void setIpAddress(@Param("user_id") int userId, @Param("ipAddress") String ipAddress);
}
