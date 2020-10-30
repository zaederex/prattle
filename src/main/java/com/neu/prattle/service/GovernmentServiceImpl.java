package com.neu.prattle.service;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;
import com.neu.prattle.repository.GovernmentRepository;
import com.neu.prattle.repository.SubpoenaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GovernmentServiceImpl implements GovernmentService {

  private GovernmentRepository governmentRepository;
  private PasswordEncoder passwordEncoder;
  private SubpoenaRepository subpoenaRepository;

  @Autowired
  public void setSubpoenaRepository(SubpoenaRepository subpoenaRepository) {
    this.subpoenaRepository = subpoenaRepository;
  }

  @Autowired
  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Autowired
  public void setGovernmentRepository(GovernmentRepository governmentRepository) {
    this.governmentRepository = governmentRepository;
  }

  @Override
  public Government validateAccount(String govUsername, String govPassword) {
    Optional<Government> optionalGovernment = governmentRepository.findByGovUsername(govUsername);
    if (optionalGovernment.isPresent() && passwordEncoder.matches(govPassword,
            optionalGovernment.get().getGovPassword())) {
      return optionalGovernment.get();
    }
    throw new IllegalStateException("Invalid username/password.");
  }

  @Override
  public Government findByGovName(String govUsername) {
    Optional<Government> optionalGovernment = governmentRepository.findByGovUsername(govUsername);
    if (optionalGovernment.isPresent()) {
      return optionalGovernment.get();
    }
    throw new IllegalStateException("Government associated with the specified username does not exist");
  }

  @Override
  public Subpoena createSubpoena(Government government, User user) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, 1);
    Subpoena subpoena = new Subpoena();
    subpoena.setGovernment(government);
    subpoena.setUser(user);
    subpoena.setExpireTimestamp(new Timestamp(cal.getTime().getTime()));
    return subpoenaRepository.save(subpoena);
  }

  @Override
  public boolean deleteSubpoena(Government government, User user) {
    return subpoenaRepository.deleteByGovernmentAndUser(government, user) != 0;
  }

  @Override
  public List<Subpoena> findAllSubpoenas(Government government) {
    return subpoenaRepository.findAllByGovernment(government);
  }

  @Override
  public boolean isSubpoenaedUser(Government government, User user) {
    List<Subpoena> subpoenas = findAllSubpoenas(government);
    return subpoenas.stream().map(Subpoena::getUser).collect(Collectors.toList())
            .contains(user);
  }
}
