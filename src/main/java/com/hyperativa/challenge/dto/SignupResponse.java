package com.hyperativa.challenge.dto;

import com.hyperativa.challenge.entity.Role;

public record SignupResponse(
Integer id,
		String login, 	
		String name,
		Role role
)
{

}
