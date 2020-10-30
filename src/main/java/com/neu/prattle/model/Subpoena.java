package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subpoena")
public class Subpoena {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "subpoena_id")
  private int subpoenaID;

  @ManyToOne
  @JoinColumn(name = "watched_user_id", referencedColumnName = "user_id")
  @JsonManagedReference("subpoena_user_reference")
  private User user;

  @ManyToOne
  @JoinColumn(name = "gov_user_id", referencedColumnName = "gov_id")
  @JsonManagedReference("subpoena_gov_reference")
  private Government government;

  @Column(name = "expire_timestamp")
  private Timestamp expireTimestamp;

  public Subpoena() {
    // default constructor for jpa
  }

  public int getSubpoenaID() {
    return subpoenaID;
  }

  public void setSubpoenaID(int subpoenaID) {
    this.subpoenaID = subpoenaID;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Government getGovernment() {
    return government;
  }

  public void setGovernment(Government government) {
    this.government = government;
  }

  public Timestamp getExpireTimestamp() {
    return expireTimestamp;
  }

  public void setExpireTimestamp(Timestamp expireTimestamp) {
    this.expireTimestamp = expireTimestamp;
  }
}
