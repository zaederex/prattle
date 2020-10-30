package com.neu.prattle.controller;

import com.neu.prattle.dto.MessageDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.service.HashTagService;
import com.neu.prattle.service.MessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/rest/message")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com", "http://localhost:3000"})
public class MessageController {

  private MessageService messageService;
  private Logger logger = LoggerFactory.getLogger(MessageController.class);
  private HashTagService hashTagService;

  @Autowired
  public void setHashTagService(HashTagService hashTagService) {
    this.hashTagService = hashTagService;
  }

  @Autowired
  public void setMessageService(MessageService messageService) {
    this.messageService = messageService;
  }

  @GetMapping(value = "/{firstPersonUsername}/chathistory/{secondPersonUsername}")
  public List<Message> getChatHistory(@PathVariable String firstPersonUsername,
                                      @PathVariable String secondPersonUsername) {
    try {
      logger.info("Attempting to read chat history of {} and {}", firstPersonUsername, secondPersonUsername);
      return messageService.findMessagesBetweenTwoUsers(firstPersonUsername, secondPersonUsername,
              true);
    } catch (UserDoesNotExistException e) {
      logger.error("Either or both users do not exist");
      return new ArrayList<>();
    }
  }

  @GetMapping(value = "/{firstPersonUsername}/chathistory/{secondPersonUsername}/newmsgcount")
  public int getNewMessageCount(@PathVariable String firstPersonUsername,
                                @PathVariable String secondPersonUsername) {
    try {
      logger.info("Returning new message count for {} and {}", firstPersonUsername, secondPersonUsername);
      return messageService.getNewMessageCount(firstPersonUsername, secondPersonUsername);
    } catch (UserDoesNotExistException e) {
      logger.error("Either or both users do not exist");
      return -1;
    }
  }

  /**
   * Updates the messageStatus for the given message id.
   *
   * @param messageDTO the put body that contains the new messageStatus.
   * @param messageId  the message id of the message to update.
   * @return the new message object.
   */
  @PutMapping(value = "/updatemessagestatus/{messageId}")
  public Message updateMesageStatus(@RequestBody MessageDTO messageDTO, @PathVariable int messageId) {
    logger.info("Attempting to Update messageStatus");
    return messageService.updateMessage(messageDTO, messageId);
  }


  @GetMapping(value = "/conversation/{username1}/{username2}")
  public List<Message> getConversationBetweenUsers(@PathVariable String username1, @PathVariable String username2) {
    logger.info("Attempting to get conversation between two users");
    try {
      return messageService.findMessagesBetweenTwoUsers(username1, username2, false);
    } catch (UserDoesNotExistException e) {
      logger.info("One or both users could not be found.");
      return new ArrayList<>();
    }
  }

  @GetMapping(value = "/search/{hashtag}/{username}")
  public List<Message> searchMessagesUsingHashTag(@PathVariable String hashtag,
                                                  @PathVariable String username) {
    Optional<HashTag> optionalHashTag = hashTagService.getHashTag(hashtag);
    if (!optionalHashTag.isPresent()) {
      return new ArrayList<>();
    }
    HashTag hashTagObject = optionalHashTag.get();
    messageService.updateHashTagSearchHits(hashTagObject);
    return messageService.findMessagesByHashtag(hashtag, username, true);
  }

  @GetMapping(value = "/search/tophashtags")
  public List<HashTag> searchTopHashTags() {
    return hashTagService.getTopHashTags();
  }

  @GetMapping(value = "/thread/{sourceMessageID}")
  public List<Message> searchMessagesInThread(@PathVariable int sourceMessageID) {
    return messageService.getMessagesForThread(sourceMessageID);
  }
}
