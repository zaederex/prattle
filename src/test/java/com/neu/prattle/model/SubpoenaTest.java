package com.neu.prattle.model;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;

public class SubpoenaTest {

  @Test
  public void testSubpoenaCreation() {
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
    assertEquals(ts, subpoena.getExpireTimestamp());
    assertEquals(government, subpoena.getGovernment());
    assertEquals(user, subpoena.getUser());
    assertEquals(1, subpoena.getSubpoenaID());
  }
}
