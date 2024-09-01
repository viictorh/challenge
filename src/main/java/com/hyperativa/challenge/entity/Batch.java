package com.hyperativa.challenge.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Batch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private LocalDate date;
	private String description;
	private Integer count;
	@OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Creditcard> cardsNumber;

	public void addCreditcard(Creditcard creditcard) {
		if (cardsNumber == null) {
			cardsNumber = new HashSet<Creditcard>();
		}
		cardsNumber.add(creditcard);
	}
}
