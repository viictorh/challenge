package com.hyperativa.challenge.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyperativa.challenge.dto.LoginDTO;
import com.hyperativa.challenge.dto.LoginResponse;
import com.hyperativa.challenge.dto.SignupDTO;
import com.hyperativa.challenge.dto.SignupResponse;
import com.hyperativa.challenge.entity.Role;
import com.hyperativa.challenge.entity.User;
import com.hyperativa.challenge.exception.AlreadyFoundException;
import com.hyperativa.challenge.exception.NotFoundException;
import com.hyperativa.challenge.repository.RoleRepository;
import com.hyperativa.challenge.repository.UserRepository;
import com.hyperativa.challenge.security.UserDetailsImpl;
import com.hyperativa.challenge.util.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private static final String ROLE_USER = "user";
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;

	public LoginResponse authenticate(LoginDTO loginDTO) {
		Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.password()));

		String jwtToken = jwtUtils.generateJwt(authenticate);
		UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
		return new LoginResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getName(), jwtToken);
	}

	public SignupResponse signup(SignupDTO signupDTO) {
		if (userRepository.existsByLogin(signupDTO.login())) {
			throw new AlreadyFoundException();
		}

		User user = User.fromSignupDTO(signupDTO);
		Role userRole = roleRepository.findByName(ROLE_USER).orElseThrow(NotFoundException::new);
		user.setRole(userRole);
		user.setPassword(passwordEncoder.encode(signupDTO.password()));
		userRepository.save(user);
		return new SignupResponse(user.getId(), user.getLogin(), user.getName(), user.getRole());
	}
}
