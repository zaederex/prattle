package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/***
 * A Basic POJO for Hashtag.
 *
 * @author Connor Frazier
 * @version dated 2020-06-09
 */
@Entity
@Table(name = "hashtag")
public class HashTag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "hashtag_id")
  private int hashtagId;

  @Column(name = "hashtag")
  private String hashtagString;

  @Column(name = "search_hits")
  private int searchHits;

  @ManyToMany(mappedBy = "hashTagSet")
  @JsonManagedReference
  private Set<Message> messages;

  public int getHashtagId() {
    return hashtagId;
  }

  public void setHashtagId(int hashtagId) {
    this.hashtagId = hashtagId;
  }

  public String getHashtagString() {
    return hashtagString;
  }

  public void setHashtagString(String hashtagString) {
    this.hashtagString = hashtagString;
  }

  public Set<Message> getMessages() {
    return messages;
  }

  public void setMessages(Set<Message> messages) {
    this.messages = messages;
  }

  public int getSearchHits() {
    return searchHits;
  }

  public void setSearchHits(int searchHits) {
    this.searchHits = searchHits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HashTag hashTag = (HashTag) o;
    return hashtagId == hashTag.hashtagId && searchHits == hashTag.searchHits &&
            Objects.equals(hashtagString, hashTag.hashtagString);
  }


  @Override
  public int hashCode() {
    return Objects.hash(hashtagId, hashtagString, searchHits);
  }

  @Override
  public String toString() {
    return "HashTag{" +
            "hashtagId=" + hashtagId +
            ", hashtagString='" + hashtagString + '\'' +
            ", searchHits=" + searchHits +
            ", messages=" + messages +
            '}';
  }

  public static HashTagBuilder hashTagBuilder() {
    return new HashTagBuilder();
  }

  public static class HashTagBuilder {

    HashTag hashTag;

    HashTagBuilder() {
      hashTag = new HashTag();
    }

    public HashTagBuilder setHashTagId(int id) {
      hashTag.setHashtagId(id);
      return this;
    }

    public HashTagBuilder setHashTagValue(String value) {
      hashTag.setHashtagString(value);
      return this;
    }

    public HashTagBuilder setHashTagMessages(Set<Message> messages) {
      hashTag.setMessages(messages);
      return this;
    }

    public HashTag build() {
      return hashTag;
    }
  }
}
