package com.neu.prattle.repository;

import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface associated with the HashTag table to support basic CRUD operations on the
 * HashTag table.
 */
@Repository
public interface HashTagRepository extends CrudRepository<HashTag, Integer> {

  /**
   * Provides a way to check if a hashtag already exists in the table
   *
   * @param hashtagString hastag to be searched
   * @return optional result
   */
  Optional<HashTag> findByHashtagString(String hashtagString);

  /**
   * Return all messages associated with a hashtag string.
   *
   * @param hashTagString hashtag string
   * @return list of messages
   */
  @Query("Select h.messages from HashTag h where h.hashtagString = :hashtagString")
  List<Message> findMessagesByHashtagString(@Param("hashtagString") String hashTagString);

  List<HashTag> findTop5ByOrderBySearchHitsDesc();
}
