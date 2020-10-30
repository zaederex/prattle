package com.neu.prattle.dto;

public class SubGroupDTO {

  private int parentId;
  private int childId;

  public int getChildId() {
    return childId;
  }

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public void setChildId(int childId) {
    this.childId = childId;
  }
}
