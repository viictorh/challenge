package com.hyperativa.challenge.dto;

import jakarta.validation.constraints.NotNull;

public record LoginDTO(@NotNull(message = "Informe o login") String login,
		@NotNull(message = "Informe a senha") String password) {

}
