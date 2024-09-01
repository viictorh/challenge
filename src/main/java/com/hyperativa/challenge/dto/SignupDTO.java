package com.hyperativa.challenge.dto;

import jakarta.validation.constraints.NotNull;

public record SignupDTO(		
		@NotNull(message = "Informe o login")
		String login, 
		@NotNull(message = "Informe o nome")
		String name,
		@NotNull(message = "Informe a senha")
		String password) {

	
	
}
