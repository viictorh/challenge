package com.hyperativa.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperativa.challenge.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	public Optional<Role> findByName(String name);

}
