package com.neu.prattle.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

/***
 * A Basic POJO for Message.
 *
 * @author Connor Frazier
 * @version dated 2020-06-08
 */
@Entity
@Table(name = "message")
@SecondaryTable(name = "message_type_details", pkJoinColumns = @PrimaryKeyJoinColumn(name = "msg_id"))
@SecondaryTable(name = "message_encryption", pkJoinColumns = @PrimaryKeyJoinColumn(name = "msg_id"))
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "msg_id")
  private int messageId;

  // The first message in the thread that this message is related to
  @Column(name = "source_msg_id")
  private int sourceMessageId;

  // The content of the message
  @Column(name = "content")
  private String content;

  // The id of the of sending user
  @Column(name = "from_user_id")
  private int fromUserId;

  // The id of the receiving user
  @Column(name = "to_user_id")
  private int toUserId;

  // The status of the message
  @Enumerated(EnumType.STRING)
  @Column(name = "message_status")
  private MessageStatus messageStatus;

  // The subject of the message
  @Column(name = "msg_subject")
  private String messageSubject;

  // Boolean flag that represents whether the message has a media attachment or not
  @Column(name = "hasAttachment")
  private boolean hasAttachment;

  // The date/time the message was created.
  @Column(name = "generation_time")
  private Timestamp generatedTime;

  @Column(name = "isBroadcastMsg", table = "message_type_details")
  private boolean isBroadcastMessage;

  @Column(name = "isPrivateMsg", table = "message_type_details")
  private boolean isPrivateMessage;

  @Column(name = "isGroupMsg", table = "message_type_details")
  private boolean isGroupMessage;

  @Column(name = "isForwardedMsg", table = "message_type_details")
  private boolean isForwardedMessage;

  @Column(name = "isSelfDestruct", table = "message_type_details")
  private boolean isSelfDestructMessage;

  @Column(name = "isEncrypyted", table = "message_type_details")
  private boolean isEncryptedMessage;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(
          name = "msg_hashtag_mapping",
          joinColumns = @JoinColumn(name = "msg_id"),
          inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
  @JsonBackReference(value = "hastagReference")
  private Set<HashTag> hashTagSet;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "message", targetEntity = MessageAttachment.class, cascade = CascadeType.ALL)
  @JsonManagedReference
  private Set<MessageAttachment> attachments;

  @Column(name = "enryption_key", table = "message_encryption")
  private String encryptionString;


  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    validateIdNumber(messageId);
    this.messageId = messageId;
  }

  public int getSourceMessageId() {
    return sourceMessageId;
  }

  public void setSourceMessageId(int sourceMessageId) {
    validateIdNumber(sourceMessageId);
    this.sourceMessageId = sourceMessageId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getFromUserId() {
    return fromUserId;
  }

  public void setFromUserId(int fromUserId) {
    validateIdNumber(fromUserId);
    this.fromUserId = fromUserId;
  }

  public int getToUserId() {
    return toUserId;
  }

  public void setToUserId(int toUserId) {
    validateIdNumber(toUserId);
    this.toUserId = toUserId;
  }

  public MessageStatus getMessageStatus() {
    return messageStatus;
  }

  public void setMessageStatus(MessageStatus messageStatus) {
    this.messageStatus = messageStatus;
  }

  public String getMessageSubject() {
    return messageSubject;
  }

  public void setMessageSubject(String messageSubject) {
    this.messageSubject = messageSubject;
  }

  public boolean hasAttachment() {
    return hasAttachment;
  }

  public void setHasAttachment(boolean hasAttachment) {
    this.hasAttachment = hasAttachment;
  }

  public Timestamp getGeneratedTime() {
    return generatedTime;
  }

  public void setGeneratedTime(Timestamp generatedTime) {
    this.generatedTime = generatedTime;
  }

  public boolean getIsBroadcastMessage() {
    return isBroadcastMessage;
  }

  public void setIsBroadcastMessage(boolean broadcastMessage) {
    isBroadcastMessage = broadcastMessage;
  }

  public boolean getIsPrivateMessage() {
    return isPrivateMessage;
  }

  public void setIsPrivateMessage(boolean privateMessage) {
    isPrivateMessage = privateMessage;
  }

  public boolean getIsGroupMessage() {
    return isGroupMessage;
  }

  public void setIsGroupMessage(boolean groupMessage) {
    isGroupMessage = groupMessage;
  }

  public boolean getIsForwardedMessage() {
    return isForwardedMessage;
  }

  public void setIsForwardedMessage(boolean forwardedMessage) {
    isForwardedMessage = forwardedMessage;
  }

  public boolean getIsSelfDestructMessage() {
    return isSelfDestructMessage;
  }

  public void setIsSelfDestructMessage(boolean selfDestructMessage) {
    isSelfDestructMessage = selfDestructMessage;
  }

  public boolean getIsEncryptedMessage() {
    return isEncryptedMessage;
  }

  public void setIsEncryptedMessage(boolean encryptedMessage) {
    isEncryptedMessage = encryptedMessage;
  }

  public Set<HashTag> getHashTagSet() {
    return hashTagSet;
  }

  public void setHashTagSet(Set<HashTag> hashTagSet) {
    this.hashTagSet = hashTagSet;
  }

  public Set<MessageAttachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(Set<MessageAttachment> attachments) {
    this.attachments = attachments;
  }

  public String getEncryptionString() {
    return encryptionString;
  }

  public void setEncryptionString(String encryptionString) {
    this.encryptionString = encryptionString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Message message = (Message) o;
    return messageId == message.messageId &&
            sourceMessageId == message.sourceMessageId &&
            fromUserId == message.fromUserId &&
            toUserId == message.toUserId &&
            hasAttachment == message.hasAttachment &&
            isBroadcastMessage == message.isBroadcastMessage &&
            isPrivateMessage == message.isPrivateMessage &&
            isGroupMessage == message.isGroupMessage &&
            isForwardedMessage == message.isForwardedMessage &&
            isSelfDestructMessage == message.isSelfDestructMessage &&
            isEncryptedMessage == message.isEncryptedMessage &&
            Objects.equals(content, message.content) &&
            messageStatus == message.messageStatus &&
            Objects.equals(messageSubject, message.messageSubject) &&
            Objects.equals(generatedTime, message.generatedTime) &&
            Objects.equals(hashTagSet, message.hashTagSet) &&
            Objects.equals(attachments, message.attachments) &&
            Objects.equals(encryptionString, message.encryptionString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageId, sourceMessageId, content, fromUserId, toUserId, messageStatus, messageSubject, hasAttachment, generatedTime, isBroadcastMessage, isPrivateMessage, isGroupMessage, isForwardedMessage, isSelfDestructMessage, isEncryptedMessage, hashTagSet, encryptionString);
  }

  private void validateIdNumber(int id) {
    if (id < 1) {
      throw new IllegalArgumentException("The id number can not be less than 1");
    }
  }

  @Override
  public String toString() {
    String messageString = new StringBuilder()
            .append("message_id: ").append(messageId).append(", ")
            .append("source_message_id: ").append(sourceMessageId).append(", ")
            .append("content: ").append(content).append(", ")
            .append("from_user_id: ").append(fromUserId).append(", ")
            .append("to_user_id: ").append(toUserId).append(", ")
            .append("message_status: ").append(messageStatus).append(", ")
            .append("message_subject: ").append(messageSubject).append(", ")
            .append("hasAttachement: ").append(hasAttachment).append(", ")
            .append("generatedTime: ").append(generatedTime == null ? generatedTime : generatedTime.toString()).append(", ")
            .append("isBroadcastMessage: ").append(isBroadcastMessage).append(", ")
            .append("isPrivateMessage: ").append(isPrivateMessage).append(", ")
            .append("isGroupMessage: ").append(isGroupMessage).append(", ")
            .append("isForwardedMessage: ").append(isForwardedMessage).append(", ")
            .append("isSelfDestructMessage: ").append(isSelfDestructMessage).append(", ")
            .append("isEncryptedMessage: ").append(isEncryptedMessage).append(", ")
            .append("hashtags: ").append(Objects.isNull(hashTagSet) ? hashTagSet : hashTagSet.toString()).append(", ")
            .append("attachments: ").append(Objects.isNull(attachments) ? attachments : attachments.toString()).append(", ")
            .append("encryptionString: ").append(encryptionString).append(", ")
            .toString();

    return messageString.substring(0, messageString.length() - 2);
  }

  /**
   * A builder to build the message pattern.
   *
   * @return a message builder object
   */
  public static MessageBuilder messageBuilder() {
    return new MessageBuilder();
  }

  /***
   * A Builder helper class to create instances of {@link Message}
   */
  public static class MessageBuilder {
    /***
     * Invoking the build method will return this message object.
     */
    Message message;

    MessageBuilder() {
      message = new Message();
    }

    public MessageBuilder setMessageId(int id) {
      message.setMessageId(id);
      return this;
    }

    public MessageBuilder setSourceMessageId(int id) {
      message.setSourceMessageId(id);
      return this;
    }

    public MessageBuilder setMessageContent(String content) {
      message.setContent(content);
      return this;
    }

    public MessageBuilder setFromUserId(int id) {
      message.setFromUserId(id);
      return this;
    }

    public MessageBuilder setToUserId(int id) {
      message.setToUserId(id);
      return this;
    }

    public MessageBuilder setMessageStatus(MessageStatus status) {
      message.setMessageStatus(status);
      return this;
    }

    public MessageBuilder setMessageSubject(String subject) {
      message.setMessageSubject(subject);
      return this;
    }

    public MessageBuilder setMessageHasAttachment(boolean hasAttachment) {
      message.setHasAttachment(hasAttachment);
      return this;
    }

    public MessageBuilder setMessageGenerationTime(Timestamp calendar) {
      message.setGeneratedTime(calendar);
      return this;
    }

    public MessageBuilder setIsBroadcastMessage(boolean isBroadcastMessage) {
      message.setIsBroadcastMessage(isBroadcastMessage);
      return this;
    }

    public MessageBuilder setIsPrivateMessage(boolean isPrivateMessage) {
      message.setIsPrivateMessage(isPrivateMessage);
      return this;
    }

    public MessageBuilder setIsGroupMessage(boolean isGroupMessage) {
      message.setIsGroupMessage(isGroupMessage);
      return this;
    }

    public MessageBuilder setIsForwardedMessage(boolean isForwardedMessage) {
      message.setIsForwardedMessage(isForwardedMessage);
      return this;
    }

    public MessageBuilder setIsSelfDestructMessage(boolean isSelfDestructMessage) {
      message.setIsSelfDestructMessage(isSelfDestructMessage);
      return this;
    }

    public MessageBuilder setIsEncryptedMessage(boolean isEncryptedMessage) {
      message.setIsEncryptedMessage(isEncryptedMessage);
      return this;
    }

    public MessageBuilder setHashtags(Set<HashTag> hashtags) {
      message.setHashTagSet(hashtags);
      return this;
    }

    public MessageBuilder setAttachments(Set<MessageAttachment> attachments) {
      message.setAttachments(attachments);
      return this;
    }

    public MessageBuilder setEncryptionString(String encryptionString) {
      message.setEncryptionString(encryptionString);
      return this;
    }

    public Message build() {
      return message;
    }
  }
}
