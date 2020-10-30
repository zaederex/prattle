package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.User;
import com.neu.prattle.repository.FilterRepository;
import com.neu.prattle.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests the method part of the {@link com.neu.prattle.service.FilterService} interface.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterServiceImplTest {

  @Mock
  private FilterRepository filterRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private FilterServiceImpl filterService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void addFilter() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Filter filter = new Filter();
    filter.setFilterID(1);
    filter.setFilterString("spam");
    when(filterRepository.findByFilterString(anyString())).thenReturn(Optional.of(filter));
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    try {
      filterService.addFilter("spam", "Joe");
    } catch (UserDoesNotExistException e) {
      fail("Should not have failed");
    }
  }

  @Test
  public void addFilter2() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Filter filter = new Filter();
    filter.setFilterID(1);
    filter.setFilterString("spam");
    when(filterRepository.findByFilterString(anyString())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    try {
      filterService.addFilter("spam", "Joe");
    } catch (UserDoesNotExistException e) {
      fail("Should not have failed");
    }
  }

  @Test(expected = UserDoesNotExistException.class)
  public void addNegativeFilter() throws UserDoesNotExistException {
    Filter filter = new Filter();
    filter.setFilterID(1);
    filter.setFilterString("spam");
    when(filterRepository.findByFilterString(anyString())).thenReturn(Optional.of(filter));
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    filterService.addFilter("spam", "Joe");
  }

  @Test
  public void removeFilter() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Filter filter = new Filter();
    filter.setFilterID(1);
    filter.setFilterString("spam");
    user.getFilters().add(filter);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    try {
      filterService.removeFilter("spam", "Joe");
    } catch (UserDoesNotExistException e) {
      fail("Should not have failed");
    }
  }

  @Test(expected = UserDoesNotExistException.class)
  public void removeNegativeFilter() throws UserDoesNotExistException {
    Filter filter = new Filter();
    filter.setFilterID(1);
    filter.setFilterString("spam");
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    filterService.removeFilter("spam", "Joe");
    fail("Shouldn't have reached this line");
  }

  @Test
  public void testGetFiltersForUser() {
    User user = User.getUserBuilder()
            .username("Joe")
            .password("123455678")
            .firstName("Joe")
            .lastName("Caputo")
            .contactNumber("8578578576")
            .build();
    Filter filter = new Filter();
    filter.setFilterID(1);
    filter.setFilterString("spam");
    when(filterRepository.findFiltersByUserId(anyInt())).thenReturn(Collections.singletonList(filter));
    assertEquals(1, filterService.getFiltersForUser(user).size());
  }
}
