package com.neu.prattle.service;

import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.repository.HashTagRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@ComponentScan("com.neu.prattle")
public class HashTagServiceImpl implements HashTagService {
  private HashTagRepository hashTagRepository;

  @Autowired
  public void setHashTagRepository(HashTagRepository hashTagRepository) {
    this.hashTagRepository = hashTagRepository;
  }

  @Override
  public Optional<HashTag> getHashTag(String hashTag) {
    return hashTagRepository.findByHashtagString(hashTag);
  }

  @Override
  public HashTag createHashTag(String hashTag, Message message) {
    Optional<HashTag> optionalHashTag = getHashTag(hashTag);
    if (optionalHashTag.isPresent()) {
      return optionalHashTag.get();
    }
    HashTag hashTag1 = HashTag.hashTagBuilder().setHashTagValue(hashTag).setHashTagMessages(
            new HashSet<>(Collections.singletonList(message))).build();
    return hashTagRepository.save(hashTag1);
  }

  @Override
  public List<HashTag> getTopHashTags() {
    return hashTagRepository.findTop5ByOrderBySearchHitsDesc();
  }
}
