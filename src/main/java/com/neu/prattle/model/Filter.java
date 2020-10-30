package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "filters")
public class Filter {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "filter_id")
  private int filterID;

  @Column(name = "filter")
  private String filterString;


  @ManyToMany(mappedBy = "filters")
  @JsonBackReference
  private Set<User> users;

  public Filter() {
    //default constructor for jpa
  }

  public int getFilterID() {
    return filterID;
  }

  public void setFilterID(int filterID) {
    this.filterID = filterID;
  }

  public String getFilterString() {
    return filterString;
  }

  public void setFilterString(String filterString) {
    this.filterString = filterString;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }
}
