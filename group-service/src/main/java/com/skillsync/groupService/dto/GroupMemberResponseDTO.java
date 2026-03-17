package com.skillsync.groupService.dto;

import com.skillsync.groupService.entity.Group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GroupMemberResponseDTO {
	private Long id;
	private Group group;
	private	Long userId;
}
