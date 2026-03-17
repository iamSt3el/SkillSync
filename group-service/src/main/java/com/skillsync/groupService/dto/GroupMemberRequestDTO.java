package com.skillsync.groupService.dto;

import com.skillsync.groupService.entity.Group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GroupMemberRequestDTO {
	@NotNull(message = "Group details is required")
	private Group group;
	
	@NotNull(message = "User id required")
	private Long userId;
}
