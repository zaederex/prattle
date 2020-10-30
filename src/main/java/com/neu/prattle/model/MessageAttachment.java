package com.neu.prattle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "msg_attachment_map")
public class MessageAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "file_id")
  private int fileID;

  @ManyToOne
  @JoinColumn(name = "message_id", referencedColumnName = "msg_id", nullable = false)
  @JsonBackReference
  private Message message;

  @Column(name = "web_url")
  private String webUrl;

  public MessageAttachment() {
    // default constructor for jpa
  }

  public int getFileID() {
    return fileID;
  }

  public void setFileID(int fileID) {
    this.fileID = fileID;
  }

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MessageAttachment that = (MessageAttachment) o;
    return Objects.equals(fileID, that.fileID) &&
            Objects.equals(message, that.message) &&
            Objects.equals(webUrl, that.webUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileID, message, webUrl);
  }

  @Override
  public String toString() {
    return "MessageAttachment{" +
            "fileID=" + fileID +
            ", webUrl='" + webUrl + '\'' +
            '}';
  }
}
