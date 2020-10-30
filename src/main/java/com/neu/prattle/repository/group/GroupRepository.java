package com.neu.prattle.repository.group;

import com.neu.prattle.model.group.Group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends CrudRepository<Group, Integer> {

  Optional<Group> findByGroupName(String groupName);

  Optional<Group> findByGroupID(int id);

  @Query(value = "select * from `groups` where group_id in (select sub_group_id from `groups` g "
          + "join group_group_mapping ggm on g.group_id=ggm.parent_group_id where parent_group_id=?1);"
          , nativeQuery = true)
  List<Group> fetchSubGroups(@Param("group_id") int groupId);

  @Query(value = "select * from `groups` where group_id in (select parent_group_id from `groups` g "
          + "join group_group_mapping ggm on g.group_id=ggm.parent_group_id where sub_group_id=?1);"
          , nativeQuery = true)
  List<Group> fetchParentGroups(@Param("group_id") int groupId);
}