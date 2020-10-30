package com.neu.prattle.repository;

import com.neu.prattle.model.Filter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilterRepository extends CrudRepository<Filter, Integer> {

  Optional<Filter> findByFilterString(String filterString);

  @Query(value = "select f.* from filters f join user_filter_map ufm on f.filter_id = ufm"
          + ".filter_id where user_id = ?1", nativeQuery = true)
  List<Filter> findFiltersByUserId(@Param("userId") int userId);

}
