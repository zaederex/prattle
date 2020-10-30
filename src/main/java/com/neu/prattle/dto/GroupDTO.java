package com.neu.prattle.dto;

public class GroupDTO {

  private String groupName;
  private String password;
  private String groupEmail;
  private String description;
  private String moderatorName;

  public String getGroupName() {
    return groupName;
  }

  public String getPassword() {
    return password;
  }

  public String getGroupEmail() {
    return groupEmail;
  }

  public String getDescription() {
    return description;
  }

  public String getModeratorName() {
    return moderatorName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setGroupEmail(String groupEmail) {
    this.groupEmail = groupEmail;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setModeratorName(String moderatorName) {
    this.moderatorName = moderatorName;
  }
}
