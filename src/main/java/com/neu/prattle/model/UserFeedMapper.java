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

/**
 * Represents the mapping between the users and their feeds.
 */
@Entity
@Table(name = "user_feed_mapping")
public class UserFeedMapper {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "feed_id")
  private int feedID;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @JsonManagedReference
  private User user;

  @Column(name = "feed_text")
  private String feedText;

  @Column(name = "feed_time")
  private Timestamp feedTime;

  public UserFeedMapper() {
    // A default empty constructor
  }

  public UserFeedMapper(User user, String feedText, Timestamp timestamp) {
    setUser(user);
    setFeedText(feedText);
    setFeedTime(timestamp);
  }

  public void setFeedID(int feedID) {
    this.feedID = feedID;
  }

  public int getFeedID() {
    return this.feedID;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getFeedText() {
    return feedText;
  }

  public void setFeedText(String feedText) {
    this.feedText = feedText;
  }

  public void setFeedTime(Timestamp time) {
    this.feedTime = time;
  }

  public Timestamp getFeedTime() {
    return feedTime;
  }
}
