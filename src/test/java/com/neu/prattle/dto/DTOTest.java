package com.neu.prattle.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DTOTest {

  @Test
  public void testUserDTOMethods() {
    UserDTO dto = new UserDTO();
    dto.setUsername("zoheb");
    dto.setPassword("password");
    dto.setFirstName("zoheb");
    dto.setLastName("nawaz");
    dto.setContactNumber("1234567890");
    dto.setTimezone("GMT");

    assertEquals("zoheb", dto.getUsername());
    assertEquals("password", dto.getPassword());
    assertEquals("zoheb", dto.getFirstName());
    assertEquals("nawaz", dto.getLastName());
    assertEquals("1234567890", dto.getContactNumber());
    assertEquals("GMT", dto.getTimezone());
  }

  @Test
  public void testGroupMemberDTOMethods() {
    GroupMemberDTO dto = new GroupMemberDTO();
    dto.setIsFollower(true);
    dto.setIsMember(true);
    dto.setIsModerator(false);
    dto.setGroupName("group");
    dto.setMemberName("zoheb");
    assertEquals("group", dto.getGroupName());
    assertEquals("zoheb", dto.getMemberName());
    assertTrue(dto.isFollower());
    assertTrue(dto.isMember());
    assertFalse(dto.isModerator());
  }

  @Test
  public void testGovernmentDTO() {
    GovernmentDTO dto = new GovernmentDTO();
    dto.setGovUsername("FBI");
    dto.setGovPassword("topsecret");

    assertEquals("FBI", dto.getGovUsername());
    assertEquals("topsecret", dto.getGovPassword());
  }
}
