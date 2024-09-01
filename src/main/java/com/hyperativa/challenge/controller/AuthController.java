package com.hyperativa.challenge.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyperativa.challenge.dto.LoginDTO;
import com.hyperativa.challenge.dto.LoginResponse;
import com.hyperativa.challenge.dto.SignupDTO;
import com.hyperativa.challenge.dto.SignupResponse;
import com.hyperativa.challenge.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginDTO loginDTO) {
		return authenticationService.authenticate(loginDTO);
	}

	@PostMapping("/signup")
	public SignupResponse singup(@RequestBody SignupDTO signupDTO) {
		return authenticationService.signup(signupDTO);
	}
}
