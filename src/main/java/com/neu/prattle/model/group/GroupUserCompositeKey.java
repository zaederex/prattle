package com.neu.prattle.model.group;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GroupUserCompositeKey implements Serializable {

  @Column(name = "group_id")
  private int groupId;

  @Column(name = "user_id")
  private int userId;

  public GroupUserCompositeKey() {

  }

  public GroupUserCompositeKey(int groupId, int userId) {
    this.groupId = groupId;
    this.userId = userId;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupUserCompositeKey that = (GroupUserCompositeKey) o;
    return groupId == that.groupId &&
            userId == that.userId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, userId);
  }
}
