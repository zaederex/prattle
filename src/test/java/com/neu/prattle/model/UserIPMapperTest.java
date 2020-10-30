package com.neu.prattle.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserIPMapperTest {

  @Test
  public void testModel() {
    UserIPMapper mapper = new UserIPMapper();
    mapper.setIpAddress("192.186.0.100");
    assertEquals("192.186.0.100", mapper.getIpAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidModel() {
    UserIPMapper mapper = new UserIPMapper();
    mapper.setIpAddress("192.186.0.100.8394629846329864239846");
    assertEquals("192.186.0.100", mapper.getIpAddress());
  }
}
