package com.neu.prattle.model.group;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.neu.prattle.dto.GroupDTO;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Neel Deshpande
 */
@Entity
@Table(name = "`groups`")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "groupID")
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "group_id")
  private int groupID;

  @Column(name = "hashPassword")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @Column(name = "`name`")
  private String groupName;

  @Column(name = "group_email")
  private String groupEmail;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "group")
  @JsonManagedReference
  @Fetch(FetchMode.JOIN)
  private List<GroupUserMapper> mappings;

  @ManyToMany
  @JoinTable(
          name = "group_group_mapping",
          joinColumns = @JoinColumn(name = "sub_group_id"),
          inverseJoinColumns = @JoinColumn(name = "parent_group_id")
  )
  @JsonManagedReference
  private List<Group> parentGroups;

  @ManyToMany(mappedBy = "parentGroups")
  @JsonBackReference
  private List<Group> subGroups;

  /**
   * Public no-arg constructor required by JPA
   */
  public Group() {
    super();
  }

  /**
   * Constructor that build Group object from a GroupBuilder
   *
   * @param groupBuilder the builder object
   */
  public Group(GroupBuilder groupBuilder) {
    setGroupName(groupBuilder.name);
    setPassword(groupBuilder.password);
    setMappings(groupBuilder.mappings);
    setGroupEmail(groupBuilder.email);
    setDescription(groupBuilder.description);
    this.subGroups = new ArrayList<>();
    this.parentGroups = new ArrayList<>();
  }

  public Group(GroupDTO dto) {
    setGroupName(dto.getGroupName());
    setDescription(dto.getDescription());
    setGroupEmail(dto.getGroupEmail());
    setPassword(dto.getPassword());
    this.subGroups = new ArrayList<>();
    this.parentGroups = new ArrayList<>();
  }

  /**
   * Simple getter for Group ID
   *
   * @return the group ID
   */
  public int getGroupID() {
    return groupID;
  }

  /**
   * Simple getter for the password
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Simple setter for the password
   *
   * @param password the password
   */
  public void setPassword(String password) {
    validateString(password);
    if (password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters long!");
    }
    this.password = password;
  }

  /**
   * Simple getter for the group name
   *
   * @return the group name
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Simple setter for the group name
   *
   * @param name the group name
   */
  public void setGroupName(String name) {
    validateString(name);
    this.groupName = name;
  }

  /**
   * Simple getter for the group email
   *
   * @return the group email
   */
  public String getGroupEmail() {
    return groupEmail;
  }

  /**
   * Simple setter for the group email
   *
   * @param groupEmail the group email
   */
  public void setGroupEmail(String groupEmail) {
    validateString(groupEmail);
    this.groupEmail = groupEmail;
  }

  /**
   * Simple getter for the group description
   *
   * @return the group description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Simple setter for the group description
   *
   * @param description the group description
   */
  public void setDescription(String description) {
    validateString(description);
    this.description = description;
  }

  /**
   * Simple getter for the members
   *
   * @return the list of members
   */
  public List<GroupUserMapper> getMappings() {
    return mappings;
  }

  /**
   * Simple setter for the list of members
   *
   * @param mappings the list of members
   */
  public void setMappings(List<GroupUserMapper> mappings) {
    this.mappings = mappings;
  }

  public List<Group> getParentGroups() {
    return parentGroups;
  }

  public List<Group> getSubGroups() {
    return subGroups;
  }

  /**
   * Get the builder for Group
   *
   * @return an instance of GroupBuilder
   */
  public static GroupBuilder getBuilder() {
    return new GroupBuilder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Group group = (Group) o;
    return Objects.equals(groupName, group.groupName) &&
            Objects.equals(groupEmail, group.groupEmail) &&
            Objects.equals(description, group.description) &&
            Objects.equals(mappings, group.mappings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, groupEmail, description, mappings);
  }

  private void validateString(String candidate) {
    if (candidate == null) {
      throw new NullPointerException("Candidate cannot be null!");
    }
    if (candidate.equals("")) {
      throw new IllegalArgumentException(candidate + " cannot be empty!");
    }
  }

  public static class GroupBuilder {

    private String name;
    private String password;
    private String email;
    private String description;
    private List<GroupUserMapper> mappings;

    GroupBuilder() {
      name = "";
      password = "";
      email = "";
      description = "";
      mappings = new ArrayList<>();
    }

    /**
     * Set the name
     *
     * @param groupName the name of the group
     * @return the builder with the name set
     */
    public GroupBuilder name(String groupName) {
      this.name = groupName;
      return this;
    }

    /**
     * Set the password
     *
     * @param password the password for the group
     * @return the builder with the password set
     */
    public GroupBuilder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * Set the members
     *
     * @param members the list of group members
     * @return the builder with the member list set
     */
    public GroupBuilder users(List<GroupUserMapper> members) {
      this.mappings = members;
      return this;
    }

    /**
     * Set the email of the group
     *
     * @param email the email
     * @return the builder with the email set
     */
    public GroupBuilder email(String email) {
      this.email = email;
      return this;
    }

    /**
     * Set the description
     *
     * @param description the description of the group
     * @return the builder with the description set
     */
    public GroupBuilder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Build the Group instance and return it
     *
     * @return the built Group
     */
    public Group build() {
      return new Group(this);
    }
  }
}
