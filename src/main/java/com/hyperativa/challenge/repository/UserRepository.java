package com.hyperativa.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperativa.challenge.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public Optional<User> findByLogin(String login);

	public boolean existsByLogin(String login);
}
