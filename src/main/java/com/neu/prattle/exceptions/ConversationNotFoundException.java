package com.neu.prattle.exceptions;

/**
 * A custom exception that represents an error when trying to find a conversation between two
 * parties.
 */
public class ConversationNotFoundException extends RuntimeException {

  /**
   * Conversation not found exception constructor.
   *
   * @param message takes a string message.
   */
  public ConversationNotFoundException(String message) {
    super(message);
  }
}
