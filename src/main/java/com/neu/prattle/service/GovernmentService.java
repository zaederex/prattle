package com.neu.prattle.service;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GovernmentService {
  Government validateAccount(String govUsername, String govPassword);

  Government findByGovName(String govUsername);

  Subpoena createSubpoena(Government government, User user);

  boolean deleteSubpoena(Government government, User user);

  List<Subpoena> findAllSubpoenas(Government government);

  boolean isSubpoenaedUser(Government government, User user);
}
