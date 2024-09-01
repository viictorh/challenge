package com.hyperativa.challenge.entity;

import com.hyperativa.challenge.entity.converter.FieldCryptography;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Creditcard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Exclude
	private Integer id;
	@EqualsAndHashCode.Exclude
	private String lineId;
	@EqualsAndHashCode.Exclude
	private Integer number;
	@Convert(converter = FieldCryptography.class)
	private String cardNumber;
	@ManyToOne
	@JoinColumn(name = "id_batch")
	@EqualsAndHashCode.Exclude
	private Batch batch;

	public void setBatch(Batch batch) {
		if (batch != null) {
			this.batch = batch;
			batch.addCreditcard(this);
		}
	}

}
