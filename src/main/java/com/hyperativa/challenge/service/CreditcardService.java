package com.hyperativa.challenge.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyperativa.challenge.dto.CreditcardDTO;
import com.hyperativa.challenge.dto.UploadResponse;
import com.hyperativa.challenge.entity.Batch;
import com.hyperativa.challenge.entity.Creditcard;
import com.hyperativa.challenge.exception.AlreadyFoundException;
import com.hyperativa.challenge.exception.NotFoundException;
import com.hyperativa.challenge.exception.UploadFailedException;
import com.hyperativa.challenge.repository.BatchRepository;
import com.hyperativa.challenge.repository.CreditcardRepository;
import com.hyperativa.challenge.util.BatchProcessHolder;
import com.hyperativa.challenge.util.LineProcessor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditcardService {

	private final CreditcardRepository creditcardRepository;
	private final BatchRepository batchRepository;

	public Creditcard findCreditCard(String creditcard) {
		return creditcardRepository.findByCardNumber(creditcard).orElseThrow(NotFoundException::new);
	}

	public Creditcard save(CreditcardDTO creditcardDTO) {
		if (creditcardRepository.existsByCardNumber(creditcardDTO.cardNumber())) {
			throw new AlreadyFoundException();
		}

		var creditcard = creditcardDTO.toEntity();
		return creditcardRepository.save(creditcard);
	}

	@Transactional
	public UploadResponse saveFromFile(InputStream inputStream) {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			LineProcessor lineProcessor = new LineProcessor(checkCreditcardExists());
			String line;
			BatchProcessHolder batchHolder = new BatchProcessHolder();
			while ((line = reader.readLine()) != null) {
				lineProcessor.processLine(line, batchHolder);

				if (batchHolder.isReadyToSave()) {
					saveBatch(batchHolder.getCurrentBatch());
					batchHolder.clearBatch();
				}
			}
			return batchHolder.getStats();
		} catch (IOException e) {
			throw new UploadFailedException("Não foi possível realizar o upload do arquivo", e);
		}
	}

	private Function<Creditcard, Boolean> checkCreditcardExists() {
		return (card) -> creditcardRepository.existsByCardNumber(card.getCardNumber());
	}

	private void saveBatch(Batch batch) {
		if (batch != null) {
			batchRepository.save(batch);
		}
	}

}
