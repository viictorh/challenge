package com.hyperativa.challenge.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyperativa.challenge.dto.CreditcardDTO;
import com.hyperativa.challenge.entity.Batch;
import com.hyperativa.challenge.entity.Creditcard;
import com.hyperativa.challenge.exception.AlreadyFoundException;
import com.hyperativa.challenge.exception.NotFoundException;
import com.hyperativa.challenge.exception.UploadFailedException;
import com.hyperativa.challenge.repository.BatchRepository;
import com.hyperativa.challenge.repository.CreditcardRepository;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditcardService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final int HEADER_SIZE = 51;
	private static final int FOOTER_SIZE = 14;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

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
	public void saveFromFile(InputStream inputStream) {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			Batch currentBatch = null;
			while ((line = reader.readLine()) != null) {
				currentBatch = processLine(line, currentBatch);
			}
		} catch (IOException e) {
			throw new UploadFailedException("Não foi possível realizar o upload do arquivo", e);
		}
	}

	private Batch processLine(String line, Batch batch) {
		if (StringUtils.isEmpty(line)) {
			return batch;
		}

		int lineLength = line.trim().length();
		if (lineLength == HEADER_SIZE) {
			return createBatchFromHeader(line);
		} else if (lineLength == FOOTER_SIZE) {
			saveBatch(batch);
			return null;
		} else {
			return processCreditCardLine(line, batch);
		}
	}

	private Batch createBatchFromHeader(String line) {
		Batch batch = new Batch();
		batch.setName(line.substring(0, 29).trim());
		batch.setDate(LocalDate.parse(line.substring(29, 37), DATE_FORMATTER));
		batch.setDescription(line.substring(37, 45));
		batch.setCount(Integer.parseInt(line.substring(45).trim()));
		return batch;
	}

	private void saveBatch(Batch batch) {
		if (batch != null) {
			batchRepository.save(batch);
		}
	}

	private Batch processCreditCardLine(String line, Batch batch) {
		Creditcard creditCard = new Creditcard();
		creditCard.setCardNumber(line.substring(7).trim());
		if (batch.getCardsNumber() != null && batch.getCardsNumber().contains(creditCard)
				|| creditcardRepository.existsByCardNumber(creditCard.getCardNumber())) {
			int lengthToMask = creditCard.getCardNumber().length() - 4;
			String maskedPart = "*".repeat(lengthToMask);
			String visiblePart = creditCard.getCardNumber().substring(lengthToMask);
			logger.warn(String.format("Lote: %s - cartão já cadastrado no banco de dados: %s%s", batch.getDescription(),
					maskedPart, visiblePart));
			return batch;
		}

		creditCard.setLineId(line.substring(0, 1));
		creditCard.setNumber(Integer.parseInt(line.substring(1, 7).trim()));
		creditCard.setBatch(batch);

		return batch;
	}
}
