package com.hyperativa.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperativa.challenge.entity.Creditcard;

public interface CreditcardRepository extends JpaRepository<Creditcard, Integer> {

	public Optional<Creditcard> findByCardNumber(String creditCard);

	public boolean existsByCardNumber(String creditcard);
}
