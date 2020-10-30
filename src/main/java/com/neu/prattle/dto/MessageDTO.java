package com.neu.prattle.dto;

import com.neu.prattle.model.MessageStatus;

import java.util.Calendar;

public class MessageDTO {
  private String content;

  private String fromUsername;

  private String toUsername;

  private MessageStatus messageStatus;

  private String messageSubject;

  private boolean hasAttachment;

  private Calendar generatedTime;

  private boolean isBroadcastMessage;

  private boolean isPrivateMessage;

  private boolean isGroupMessage;

  private boolean isForwardedMessage;

  private boolean isSelfDestructMessage;

  private boolean isEncryptedMessage;


  public String getFromUsername() {
    return fromUsername;
  }

  public String getToUsername() {
    return toUsername;
  }

  public String getContent() {
    return content;
  }

  public MessageStatus getMessageStatus() {
    return messageStatus;
  }

  public String getMessageSubject() {
    return messageSubject;
  }

  public boolean isHasAttachment() {
    return hasAttachment;
  }

  public Calendar getGeneratedTime() {
    return generatedTime;
  }

  public boolean isBroadcastMessage() {
    return isBroadcastMessage;
  }

  public boolean isPrivateMessage() {
    return isPrivateMessage;
  }

  public boolean isGroupMessage() {
    return isGroupMessage;
  }

  public boolean isForwardedMessage() {
    return isForwardedMessage;
  }

  public boolean isSelfDestructMessage() {
    return isSelfDestructMessage;
  }

  public boolean isEncryptedMessage() {
    return isEncryptedMessage;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setFromUsername(String fromUsername) {
    this.fromUsername = fromUsername;
  }

  public void setToUsername(String toUsername) {
    this.toUsername = toUsername;
  }

  public void setMessageStatus(MessageStatus messageStatus) {
    this.messageStatus = messageStatus;
  }

  public void setMessageSubject(String messageSubject) {
    this.messageSubject = messageSubject;
  }

  public void setHasAttachment(boolean hasAttachment) {
    this.hasAttachment = hasAttachment;
  }

  public void setGeneratedTime(Calendar generatedTime) {
    this.generatedTime = generatedTime;
  }

  public void setBroadcastMessage(boolean broadcastMessage) {
    isBroadcastMessage = broadcastMessage;
  }

  public void setPrivateMessage(boolean privateMessage) {
    isPrivateMessage = privateMessage;
  }

  public void setGroupMessage(boolean groupMessage) {
    isGroupMessage = groupMessage;
  }

  public void setForwardedMessage(boolean forwardedMessage) {
    isForwardedMessage = forwardedMessage;
  }

  public void setSelfDestructMessage(boolean selfDestructMessage) {
    isSelfDestructMessage = selfDestructMessage;
  }

  public void setEncryptedMessage(boolean encryptedMessage) {
    isEncryptedMessage = encryptedMessage;
  }
}
