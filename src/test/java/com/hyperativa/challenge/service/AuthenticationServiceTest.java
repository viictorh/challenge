package com.hyperativa.challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hyperativa.challenge.dto.LoginDTO;
import com.hyperativa.challenge.dto.LoginResponse;
import com.hyperativa.challenge.dto.SignupDTO;
import com.hyperativa.challenge.dto.SignupResponse;
import com.hyperativa.challenge.entity.Role;
import com.hyperativa.challenge.entity.User;
import com.hyperativa.challenge.exception.AlreadyFoundException;
import com.hyperativa.challenge.repository.RoleRepository;
import com.hyperativa.challenge.repository.UserRepository;
import com.hyperativa.challenge.security.UserDetailsImpl;
import com.hyperativa.challenge.util.JwtUtils;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private AuthenticationService authenticationService;

	private LoginDTO loginDTO;
	private SignupDTO signupDTO;

	@BeforeEach
	public void setUp() {
		loginDTO = new LoginDTO("testuser", "password");
		signupDTO = new SignupDTO("newuser", "New User", "password");
	}

	@Test
	public void testAuthenticate_Success() {
		// Mocking authentication process
		Authentication authentication = mock(Authentication.class);
		Role role = new Role();
		role.setId(1);
		role.setName("admin");
		User user = new User();
		user.setLogin("testuser");
		user.setName("Test User");
		user.setPassword("password");
		user.setRole(role);
		user.setEnabled(true);
		user.setId(1);
		UserDetailsImpl userDetails = UserDetailsImpl.build(user);
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(jwtUtils.generateJwt(authentication)).thenReturn("mocked-jwt-token");

		// Calling the authenticate method
		LoginResponse response = authenticationService.authenticate(loginDTO);

		// Assertions
		assertNotNull(response);
		assertEquals(1, response.id());
		assertEquals("testuser", response.login());
		assertEquals("Test User", response.name());
		assertEquals("mocked-jwt-token", response.jwt());

		// Verify interactions
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtUtils, times(1)).generateJwt(authentication);
	}

	@Test
	public void testSignup_Success() {
		Role role = new Role();
		role.setId(2);
		role.setName("user");
		when(userRepository.existsByLogin(signupDTO.login())).thenReturn(false);
		when(roleRepository.findByName("user")).thenReturn(Optional.of(role));
		when(passwordEncoder.encode(signupDTO.password())).thenReturn("encoded-password");

		SignupResponse response = authenticationService.signup(signupDTO);

		assertNotNull(response);
		assertEquals("newuser", response.login());
		assertEquals("New User", response.name());
		assertEquals("user", response.role().getName());
		assertEquals(2, response.role().getId());

		verify(userRepository, times(1)).existsByLogin(signupDTO.login());
		verify(roleRepository, times(1)).findByName("user");
		verify(passwordEncoder, times(1)).encode(signupDTO.password());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
    public void testSignup_AlreadyFoundException() {
        when(userRepository.existsByLogin(signupDTO.login())).thenReturn(true);
        // Asserting exception is thrown
        assertThrows(AlreadyFoundException.class, () -> authenticationService.signup(signupDTO));

        verify(roleRepository, never()).findByName(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

}