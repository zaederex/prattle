package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.neu.prattle.model.group.GroupUserMapper;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/***
 * A User object represents a basic account information for a user.
 */
@Entity
@Table(name = "user")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userID")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  private int userID;

  @Column(name = "username")
  private String username;

  @Column(name = "hashPassword")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "contact_num")
  private String contactNumber;

  @Column(name = "timezone")
  private TimeZone timezone;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private UserIPMapper userIPMapper;

  @ManyToMany
  @JoinTable(
          name = "user_follows",
          joinColumns = @JoinColumn(name = "follower_id"),
          inverseJoinColumns = @JoinColumn(name = "followee_id")
  )
  @JsonManagedReference
  @Fetch(FetchMode.JOIN)
  private Set<User> followers;

  @ManyToMany(mappedBy = "followers")
  @JsonBackReference
  private Set<User> followees;

  @OneToMany(mappedBy = "user")
  @JsonBackReference
  private List<GroupUserMapper> mappings;

  @OneToMany(mappedBy = "user")
  @JsonBackReference
  @Fetch(FetchMode.JOIN)
  private List<UserFeedMapper> userFeeds;

  @Column(name = "last_log_out_time")
  private Timestamp logOutTimestamp;

  @OneToMany(mappedBy = "user")
  @JsonBackReference
  private Set<Subpoena> subpoenas;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
          name = "user_filter_map",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "filter_id"))
  private Set<Filter> filters;

  @Column(name = "profile_picture_url")
  private String profilePicturePath;

  /**
   * Default constructor.
   */
  public User() {
    super();
  }

  /**
   * Constructs the user using the builder.
   *
   * @param builder builder that builds the user object
   */
  public User(UserBuilder builder) {
    setUsername(builder.userName);
    setPassword(builder.password);
    setFirstName(builder.firstName);
    setLastName(builder.lastName);
    setContactNumber(builder.contactNumber);
    setTimezone(builder.timezone);
    setFollowers(builder.followers);
    setFollowees(builder.followees);
    setLogOutTimestamp(null);
    setUserFeeds(builder.userFeeds);
    setProfilePicturePath(builder.profilePicture);
    setFilters(new HashSet<>());
  }

  /**
   * Return username.
   *
   * @return a string name
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username takes the argument
   */
  public void setUsername(String username) {
    if (username == null) {
      throw new NullPointerException("The user's name cannot be null");
    } else if (username.trim().equals("")) {
      throw new IllegalArgumentException("The user's name cannot be empty");
    }
    this.username = username;
  }

  /**
   * fetches password.
   *
   * @return a string password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password password to be set
   */
  public void setPassword(String password) {
    if (password == null) {
      throw new NullPointerException("The user's password cannot be null");
    } else if (password.contains(" ")) {
      throw new IllegalArgumentException("The user's password cannot contain empty characters");
    } else if (password.length() < 8) {
      throw new IllegalArgumentException("Specified password is weak");
    }
    this.password = password;
  }

  /**
   * Returns the id of the user.
   *
   * @return user id
   */
  public int getUserID() {
    return userID;
  }

  /**
   * Sets the id of the user, utilized by JPA.
   *
   * @param userID id to be set
   */
  public void setUserID(int userID) {
    this.userID = userID;
  }

  /**
   * Return the user's first name.
   *
   * @return first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the user's first name.
   *
   * @param firstName first name to be set to
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Return the user's last name.
   *
   * @return last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the user's last name.
   *
   * @param lastName last name to be set to
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Returns contact number of the user.
   *
   * @return contact number
   */
  public String getContactNumber() {
    return contactNumber;
  }

  /**
   * Sets the contact number of the user.
   *
   * @param contactNumber contact number to be set to
   */
  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }


  /**
   * Returns the timezone that has been set.
   *
   * @return timezone of the user
   */
  public TimeZone getTimezone() {
    return timezone;
  }

  /**
   * Sets the user's timezone.
   *
   * @param timezone timezone to be set for the user
   */
  public void setTimezone(TimeZone timezone) {
    this.timezone = timezone;
  }

  /**
   * Returns the IP mapping.
   *
   * @return IP mapping
   */
  public UserIPMapper getUserIPMapper() {
    return userIPMapper;
  }

  /**
   * Sets the ip mapping for this user.
   *
   * @param userIPMapper mapping to be set
   */
  public void setUserIPMapper(UserIPMapper userIPMapper) {
    this.userIPMapper = userIPMapper;
  }

  /**
   * Returns a set of followers.
   *
   * @return set of followers
   */
  public Set<User> getFollowers() {
    return followers;
  }

  /**
   * Assigns a set of followers.
   *
   * @param followers followers to be assigned
   */
  public void setFollowers(Set<User> followers) {
    this.followers = followers;
  }

  /**
   * Returns a set of followees.
   *
   * @return set of followees
   */
  public Set<User> getFollowees() {
    return followees;
  }

  /**
   * Assigns a set of followees.
   *
   * @param followees set of followees
   */
  public void setFollowees(Set<User> followees) {
    this.followees = followees;
  }


  public Timestamp getLogOutTimestamp() {
    return logOutTimestamp;
  }

  public void setLogOutTimestamp(Timestamp logOutTimestamp) {
    this.logOutTimestamp = logOutTimestamp;
  }

  public List<GroupUserMapper> getMappings() {
    return mappings;
  }

  public void setMappings(List<GroupUserMapper> mappings) {
    this.mappings = mappings;
  }

  public List<UserFeedMapper> getUserFeeds() {
    return userFeeds;
  }

  public void setUserFeeds(List<UserFeedMapper> userFeeds) {
    this.userFeeds = userFeeds;
  }

  public Set<Subpoena> getSubpoenas() {
    return subpoenas;
  }

  public void setSubpoenas(Set<Subpoena> subpoenas) {
    this.subpoenas = subpoenas;
  }

  public Set<Filter> getFilters() {
    return filters;
  }

  public void setFilters(Set<Filter> filters) {
    this.filters = filters;
  }

  public String getProfilePicturePath() {
    return profilePicturePath;
  }

  public void setProfilePicturePath(String profilePicturePath) {
    this.profilePicturePath = profilePicturePath;
  }

  /***
   * Makes comparison between two user accounts.
   *
   * Two user objects are equal if their name are equal ( names are case-sensitive )
   *
   * @param obj Object to compare
   * @return a predicate value for the comparison.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof User))
      return false;

    User user = (User) obj;
    return user.username.equals(this.username);
  }

  /***
   * Returns the hashCode of this object.
   *
   * As name can be treated as a sort of identifier for
   * this instance, we can use the hashCode of "name"
   * for the complete object.
   *
   *
   * @return hashCode of "this"
   */
  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  /**
   * Returns the builder to construct the User object.
   *
   * @return builder object
   */
  public static UserBuilder getUserBuilder() {
    return new UserBuilder();
  }

  /**
   * Represents the builder for constructing the {@link com.neu.prattle.model.User} object.
   */
  public static class UserBuilder {

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private TimeZone timezone;
    private Set<User> followers;
    private Set<User> followees;
    private List<UserFeedMapper> userFeeds;
    private String profilePicture;

    UserBuilder() {
      userName = "";
      password = "";
      firstName = "";
      lastName = "";
      contactNumber = "";
      timezone = TimeZone.getTimeZone("UTC");
      followers = new HashSet<>();
      followees = new HashSet<>();
      userFeeds = new ArrayList<>();
      profilePicture = null;
    }

    /**
     * Returns builder after setting username.
     *
     * @param userName user name to be set
     * @return resultant builder
     */
    public UserBuilder username(String userName) {
      this.userName = userName;
      return this;
    }

    /**
     * Returns builder after setting password.
     *
     * @param password password to be set
     * @return resultant builder
     */
    public UserBuilder password(String password) {
      this.password = password;
      return this;
    }

    /**
     * Returns builder after setting first name.
     *
     * @param firstName first name to be set
     * @return resultant builder
     */
    public UserBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    /**
     * Returns builder after setting last name.
     *
     * @param lastName user name to be set
     * @return resultant builder
     */
    public UserBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     * Returns builder after setting contact number.
     *
     * @param contactNumber contact number to be set
     * @return resultant builder
     */
    public UserBuilder contactNumber(String contactNumber) {
      this.contactNumber = contactNumber;
      return this;
    }

    /**
     * Returns the builder after setting the timezone.
     *
     * @param timezone timezone to be set
     * @return resultant builder
     */
    public UserBuilder timezone(String timezone) {
      if (timezone == null) {
        timezone = "UTC";
      }
      this.timezone = TimeZone.getTimeZone(timezone);
      return this;
    }

    public UserBuilder profilePicture(String profilePicture) {
      this.profilePicture = profilePicture;
      return this;
    }

    /**
     * Builds the User object.
     *
     * @return resultant user object
     */
    public User build() {
      return new User(this);
    }
  }
}
