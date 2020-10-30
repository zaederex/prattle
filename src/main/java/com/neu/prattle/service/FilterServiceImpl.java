package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.User;
import com.neu.prattle.repository.FilterRepository;
import com.neu.prattle.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FilterServiceImpl implements FilterService {

  private Logger logger = LoggerFactory.getLogger(FilterServiceImpl.class);

  private FilterRepository filterRepository;

  private UserRepository userRepository;

  @Autowired
  public void setFilterRepository(FilterRepository filterRepository) {
    this.filterRepository = filterRepository;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void addFilter(String filterText, String username) throws UserDoesNotExistException {
    Optional<Filter> optionalFilter = filterRepository.findByFilterString(filterText);
    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (!optionalUser.isPresent()) {
      throw new UserDoesNotExistException("User not found");
    }
    User user = optionalUser.get();
    if (optionalFilter.isPresent()) {
      logger.info("Filter with text {} already exists", filterText);
      Filter filter = optionalFilter.get();
      user.getFilters().add(filter);
    } else {
      logger.info("Creating filter {}", filterText);
      Filter filter = new Filter();
      filter.setFilterString(filterText);
      user.getFilters().add(filter);
      logger.info("Filter created");
    }
    userRepository.save(user);
  }

  @Override
  public void removeFilter(String filterText, String username) throws UserDoesNotExistException {
    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (!optionalUser.isPresent()) {
      logger.info("User not present");
      throw new UserDoesNotExistException("User not found");
    }
    User user = optionalUser.get();
    Set<Filter> filters = user.getFilters();
    List<Filter> matchList = filters.stream()
            .filter(x -> x.getFilterString().equals(filterText)).collect(Collectors.toList());
    if (!matchList.isEmpty()) {
      filters.removeAll(matchList);
      user.setFilters(filters);
      userRepository.save(user);
      logger.info("Filter has been deleted");
    }
  }

  @Override
  public List<Filter> getFiltersForUser(User user) {
    return filterRepository.findFiltersByUserId(user.getUserID());
  }
}
