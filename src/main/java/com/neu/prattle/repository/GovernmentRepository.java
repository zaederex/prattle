package com.neu.prattle.repository;

import com.neu.prattle.model.Government;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GovernmentRepository extends CrudRepository<Government, Integer> {
  Optional<Government> findByGovUsername(String username);
}
