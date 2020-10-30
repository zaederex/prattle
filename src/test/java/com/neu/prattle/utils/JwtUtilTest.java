package com.neu.prattle.utils;

import com.neu.prattle.model.User;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Contains tests to verify correctness of {@link com.neu.prattle.utils.JwtUtil}.
 */
public class JwtUtilTest {

  @Test
  public void testTokenGeneration() {
    User bob = User.getUserBuilder().username("Bob").password("TheBuilder").build();
    assertNotNull(JwtUtil.generateToken(bob));
  }
}
