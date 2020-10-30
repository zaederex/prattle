package com.neu.prattle.repository;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubpoenaRepository extends CrudRepository<Subpoena, Integer> {

  long deleteByGovernmentAndUser(Government government, User user);

  List<Subpoena> findAllByGovernment(Government government);
}
