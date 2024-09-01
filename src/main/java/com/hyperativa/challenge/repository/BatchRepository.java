package com.hyperativa.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperativa.challenge.entity.Batch;

public interface BatchRepository extends JpaRepository<Batch, Integer> {

}
