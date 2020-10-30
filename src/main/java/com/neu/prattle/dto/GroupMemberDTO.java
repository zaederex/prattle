package com.neu.prattle.dto;

public class GroupMemberDTO {

  private String groupName;

  private String memberName;

  private boolean isModerator;

  private boolean isFollower;

  private boolean isMember;

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public void setIsModerator(boolean moderator) {
    isModerator = moderator;
  }

  public void setIsFollower(boolean follower) {
    isFollower = follower;
  }

  public void setIsMember(boolean member) {
    isMember = member;
  }

  public boolean isModerator() {
    return isModerator;
  }

  public boolean isFollower() {
    return isFollower;
  }

  public boolean isMember() {
    return isMember;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getMemberName() {
    return memberName;
  }
}
