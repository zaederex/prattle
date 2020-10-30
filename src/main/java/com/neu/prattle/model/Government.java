package com.neu.prattle.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Maps to the government entity in the prattle db.
 */
@Entity
@Table(name = "government")
public class Government {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "gov_id")
  private int governmentID;

  @Column(name = "gov_username")
  private String govUsername;

  @Column(name = "gov_password")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String govPassword;

  @OneToMany(mappedBy = "government")
  @JsonBackReference
  private Set<Subpoena> subpoenas;

  /**
   * Default constructor.
   */
  public Government() {
    // for jpa
  }

  public int getGovernmentID() {
    return governmentID;
  }

  public void setGovernmentID(int governmentID) {
    this.governmentID = governmentID;
  }

  public String getGovUsername() {
    return govUsername;
  }

  public void setGovUsername(String govUsername) {
    this.govUsername = govUsername;
  }

  public String getGovPassword() {
    return govPassword;
  }

  public void setGovPassword(String govPassword) {
    this.govPassword = govPassword;
  }

  public Set<Subpoena> getSubpoenas() {
    return subpoenas;
  }

  public void setSubpoenas(Set<Subpoena> subpoenas) {
    this.subpoenas = subpoenas;
  }
}
