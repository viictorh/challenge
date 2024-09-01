package com.hyperativa.challenge.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hyperativa.challenge.entity.User;
import com.hyperativa.challenge.repository.UserRepository;
import com.hyperativa.challenge.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String login) {
		User user = userRepository.findByLogin(login)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não localizado"));
		return UserDetailsImpl.build(user);
	}

}
