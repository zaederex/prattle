package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.Filter;
import com.neu.prattle.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FilterService {

  void addFilter(String filterText, String username) throws UserDoesNotExistException;

  void removeFilter(String filterText, String username) throws UserDoesNotExistException;

  List<Filter> getFiltersForUser(User user);
}
