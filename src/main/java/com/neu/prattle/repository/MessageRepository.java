package com.neu.prattle.repository;

import com.neu.prattle.model.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface associated with the User table to support basic CRUD operations on the
 * Message table.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

  List<Message> findByToUserId(int id);


  @Query("select m from Message m where m.fromUserId = :from_user_id "
          + "and m.toUserId = :to_user_id ")
  List<Message> findByFromUserIdAndToUserId(@Param("from_user_id") int fromUserId,
                                            @Param("to_user_id") int toUserId);

  @Query("select m from Message m where m.fromUserId = :from_user_id "
          + "and m.toUserId = :to_user_id "
          + "and m.messageStatus = com.neu.prattle.model.MessageStatus.DELIVERED")
  List<Message> findNewMessages(@Param("from_user_id") int fromUserId,
                                @Param("to_user_id") int toUserId);

  @Query(value = "select * from message join user on message.to_user_id = `user`.user_id " +
          "join message_type_details mtd on message.msg_id = mtd.msg_id " +
          "left join message_encryption me on message.msg_id = me.msg_id" +
          " where message.generation_time > `user`.last_log_out_time" +
          " and `user`.user_id = ?1 ", nativeQuery = true)
  List<Message> fetchUnreadMessages(@Param("to_user_id") int toUserId);

  List<Message> findAllBySourceMessageIdOrderByGeneratedTime(int sourceMessageID);
}
