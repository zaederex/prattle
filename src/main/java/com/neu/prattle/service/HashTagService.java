package com.neu.prattle.service;

import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface HashTagService {

  Optional<HashTag> getHashTag(String hashTag);

  HashTag createHashTag(String hashTag, Message message);

  List<HashTag> getTopHashTags();
}
