package com.neu.prattle.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests filter model class.
 */
public class FilterTest {

  @Test
  public void testFilterCreation() {
    Filter filter = new Filter();
    filter.setFilterString("annoy");
    filter.setFilterID(1);
    assertEquals(1, filter.getFilterID());
    assertEquals("annoy", filter.getFilterString());
    assertNull(filter.getUsers());
  }
}
