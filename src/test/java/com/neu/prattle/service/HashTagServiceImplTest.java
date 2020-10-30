package com.neu.prattle.service;

import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.repository.HashTagRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HashTagServiceImplTest {

  @Mock
  private HashTagRepository hashTagRepository;

  @InjectMocks
  private HashTagServiceImpl hashTagService;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getHashTag() {
    HashTag hashTag = HashTag.hashTagBuilder().setHashTagValue("awesomeness").build();
    when(hashTagRepository.findByHashtagString("awesomeness")).thenReturn(Optional.of(hashTag));
    Optional<HashTag> optionalHashTag = hashTagService.getHashTag("awesomeness");
    assertTrue(optionalHashTag.isPresent());
    assertEquals(hashTag, optionalHashTag.get());
  }

  @Test
  public void getNegativeHashTag() {
    when(hashTagRepository.findByHashtagString("awesomeness")).thenReturn(Optional.empty());
    Optional<HashTag> optionalHashTag = hashTagService.getHashTag("awesomeness");
    assertFalse(optionalHashTag.isPresent());
  }

  @Test
  public void createHashTagNotExists() {
    Message message = Message.messageBuilder().setMessageContent("#awesomeness").setIsBroadcastMessage(true).build();
    HashTag hashTag = HashTag.hashTagBuilder().setHashTagValue("awesomeness").build();
    when(hashTagRepository.findByHashtagString("awesomeness")).thenReturn(Optional.empty());
    when(hashTagRepository.save(any())).thenReturn(hashTag);
    assertEquals(hashTag, hashTagService.createHashTag("awesomeness", message));
  }

  @Test
  public void createHashTagExists() {
    HashTag hashTag = HashTag.hashTagBuilder().setHashTagValue("awesomeness").build();
    Message message = Message.messageBuilder().setMessageContent("#awesomeness")
            .setIsBroadcastMessage(true)
            .setHashtags(new HashSet<>(Collections.singletonList(hashTag))).build();
    when(hashTagRepository.findByHashtagString("awesomeness")).thenReturn(Optional.of(hashTag));
    assertEquals(hashTag, hashTagService.createHashTag("awesomeness", message));
  }

  @Test
  public void testGetTopHashTags() {
    when(hashTagRepository.findTop5ByOrderBySearchHitsDesc()).thenReturn(Collections.emptyList());
    assertEquals(0, hashTagService.getTopHashTags().size());
  }
}
