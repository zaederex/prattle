package com.neu.prattle.main;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Test class for the PrattleApplication class.
 */
public class PrattleApplicationTest {

  /**
   * Test the get classes method of the Prattle Application. To make sure correct classes were
   * loaded in.
   */
  @Test
  public void testGetClasses() {
    List<String> classes = new ArrayList<>(Collections.singletonList("UserController"));
    PrattleApplication prattleApplication = new PrattleApplication();
    Set<String> set = prattleApplication.getClasses().stream().map(Class::getSimpleName).collect(Collectors.toSet());
    for (String setClass : set) {
      assertTrue(classes.contains(setClass));
    }
  }
}
