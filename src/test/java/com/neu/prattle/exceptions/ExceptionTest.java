package com.neu.prattle.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExceptionTest {
  @Test
  public void testNotAMemberException() {
    Exception exception = new NotAMemberException("Not a member");
    assertEquals("Not a member", exception.getMessage());
  }

  @Test
  public void testMessageAlreadyExistsException() {
    Exception exception = new MessageAlreadyExistsException("Exists");
    assertEquals("Exists", exception.getMessage());
  }
}
