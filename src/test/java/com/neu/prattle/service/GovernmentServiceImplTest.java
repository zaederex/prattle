package com.neu.prattle.service;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.Subpoena;
import com.neu.prattle.model.User;
import com.neu.prattle.repository.GovernmentRepository;
import com.neu.prattle.repository.SubpoenaRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GovernmentServiceImplTest {

  @Mock
  private GovernmentRepository governmentRepository;

  @Mock
  private SubpoenaRepository subpoenaRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private GovernmentServiceImpl governmentService;

  private Government government;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    government = new Government();
    government.setGovernmentID(1);
    government.setGovUsername("FBI");
    government.setGovPassword("supersecretpassword");
  }

  @Test
  public void validateAccount() {
    when(governmentRepository.findByGovUsername(anyString())).thenReturn(Optional.of(government));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    assertEquals(government, governmentService.validateAccount("FBI", "superSecretPassword"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNegativeValidateAccount() {
    when(governmentRepository.findByGovUsername(anyString())).thenReturn(Optional.empty());
    assertNotEquals(government, governmentService.validateAccount("FBI", "superSecretPassword"));
  }

  @Test
  public void testFindGovernment() {
    when(governmentRepository.findByGovUsername("FBI")).thenReturn(Optional.of(government));
    assertEquals(government, governmentService.findByGovName("FBI"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNegativeFindGovernment() {
    when(governmentRepository.findByGovUsername("FBI")).thenReturn(Optional.empty());
    assertEquals(government, governmentService.findByGovName("FBI"));
  }

  @Test
  public void testCreateSubpoena() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Government government = new Government();
    government.setGovUsername("CIA");
    government.setGovernmentID(1);
    government.setGovPassword("password");
    government.setSubpoenas(new HashSet<>());
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);

    when(subpoenaRepository.save(any())).thenReturn(subpoena);
    assertEquals(subpoena, governmentService.createSubpoena(government, user));
  }

  @Test
  public void testSubpoenaDelete() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Government government = new Government();
    government.setGovUsername("CIA");
    government.setGovernmentID(1);
    government.setGovPassword("password");
    government.setSubpoenas(new HashSet<>());
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);

    when(subpoenaRepository.deleteByGovernmentAndUser(any(), any())).thenReturn(1L);
    assertTrue(governmentService.deleteSubpoena(government, user));
  }

  @Test
  public void testNegativeSubpoenaDelete() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Government government = new Government();
    government.setGovUsername("CIA");
    government.setGovernmentID(1);
    government.setGovPassword("password");
    government.setSubpoenas(new HashSet<>());
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);

    when(subpoenaRepository.deleteByGovernmentAndUser(any(), any())).thenReturn(0L);
    assertFalse(governmentService.deleteSubpoena(government, user));
  }

  @Test
  public void tesGetAllSubpoenas() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Government government = new Government();
    government.setGovUsername("CIA");
    government.setGovernmentID(1);
    government.setGovPassword("password");
    government.setSubpoenas(new HashSet<>());
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);

    when(subpoenaRepository.findAllByGovernment(government)).thenReturn(Collections.singletonList(subpoena));
    assertTrue(governmentService.findAllSubpoenas(government).contains(subpoena));
  }

  @Test
  public void testIsSubpoenad() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Government government = new Government();
    government.setGovUsername("CIA");
    government.setGovernmentID(1);
    government.setGovPassword("password");
    government.setSubpoenas(new HashSet<>());
    Subpoena subpoena = new Subpoena();
    subpoena.setSubpoenaID(1);
    Timestamp ts = new Timestamp(new Date().getTime());
    subpoena.setExpireTimestamp(ts);
    subpoena.setUser(user);
    subpoena.setGovernment(government);

    when(governmentService.findAllSubpoenas(government)).thenReturn(Collections.singletonList(subpoena));
    assertTrue(governmentService.isSubpoenaedUser(government, user));
  }
}
