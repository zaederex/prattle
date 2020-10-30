package com.neu.prattle.repository.group;


import com.neu.prattle.model.group.GroupUserCompositeKey;
import com.neu.prattle.model.group.GroupUserMapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupUserMapperRepository extends JpaRepository<GroupUserMapper, GroupUserCompositeKey> {

  @Query("select gum from GroupUserMapper gum where gum.group.groupID= :groupId")
  List<GroupUserMapper> getMapsByGroupId(@Param("groupId") int groupId);
}
