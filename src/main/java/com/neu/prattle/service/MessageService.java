package com.neu.prattle.service;

import com.neu.prattle.dto.MessageDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;

import org.springframework.stereotype.Service;

import java.util.List;

/***
 * Acts as an interface between the data layer and the
 * servlet controller.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on message objects.
 *
 * @author Connor Frazier
 * @version dated 2020-06-09
 *
 */
@Service
public interface MessageService {

  /**
   * Save a message in the database.
   *
   * @param message the message to save.
   * @return the message that was saved.
   */
  Message saveNewMessage(Message message);

  /**
   * Updates saves changes to a message.
   *
   * @param messageDTO the updated message dat to save.
   * @param messageId  the id of the message to update.
   * @return the message that was updated.
   */
  Message updateMessage(MessageDTO messageDTO, int messageId);

  /**
   * Finds a message by the id of the message.
   *
   * @param id the id of the message to find.
   * @return Optional message of the matching id.
   */
  Message findMessageById(int id);

  /**
   * Finds the messages that sent to a user.
   *
   * @param username the user name for the receiving messages.
   * @return the messages that t he user has been sent.
   */
  List<Message> findMessagesForReceivingUser(String username, boolean excludeExpired)
          throws UserDoesNotExistException;

  /**
   * Returns an ordered list of messages between two users.
   *
   * @param currentUsername    the current user.
   * @param conversingUsername the user that the current user conversed with.
   * @return the list of messages in order between the users.
   */
  List<Message> findMessagesBetweenTwoUsers(String currentUsername, String conversingUsername,
                                            boolean excludeExpired)
          throws UserDoesNotExistException;

  /**
   * Returns the count of new messages between users.
   *
   * @param firstPersonUsername  receiver of messages
   * @param secondPersonUsername sender of messages
   * @return number of new messages
   */
  int getNewMessageCount(String firstPersonUsername, String secondPersonUsername)
          throws UserDoesNotExistException;

  /**
   * Returns the messages that were sent to a user after their last log out.
   *
   * @param userName the username to get the messages for.
   * @return the list of messages that have been delivered but not read.
   * @throws UserDoesNotExistException thrown if there is no user with the username provided.
   */
  List<Message> getUnsentMessages(String userName, boolean excludeExpired) throws UserDoesNotExistException;

  /**
   * Returns a list of messages based on hashtag.
   *
   * @param hashtag  hashtag associated with the expected messages
   * @param username
   * @return list of messages matching the search criteria
   */
  List<Message> findMessagesByHashtag(String hashtag, String username, boolean excludeExpired);

  /**
   * Updates the search hits for a particular hash tag.
   *
   * @param hashTag hash tag being searched
   */
  void updateHashTagSearchHits(HashTag hashTag);

  /**
   * Return all messages part of the same thread.
   *
   * @param sourceMessageID ID of the source message from which the thread was created
   * @return list of messages
   */
  List<Message> getMessagesForThread(int sourceMessageID);
}
