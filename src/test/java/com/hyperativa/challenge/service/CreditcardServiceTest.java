package com.hyperativa.challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hyperativa.challenge.dto.CreditcardDTO;
import com.hyperativa.challenge.dto.UploadResponse;
import com.hyperativa.challenge.entity.Batch;
import com.hyperativa.challenge.entity.Creditcard;
import com.hyperativa.challenge.exception.AlreadyFoundException;
import com.hyperativa.challenge.exception.NotFoundException;
import com.hyperativa.challenge.exception.UploadFailedException;
import com.hyperativa.challenge.repository.BatchRepository;
import com.hyperativa.challenge.repository.CreditcardRepository;
import com.hyperativa.challenge.util.LineProcessor;

@ExtendWith(MockitoExtension.class)
public class CreditcardServiceTest {
	@Mock
	private CreditcardRepository creditcardRepository;
	
	@Mock
	private BatchRepository batchRepository;
	@Mock
	private LineProcessor lineProcessorMock;
	@InjectMocks
	private CreditcardService creditcardService;

	private static final String CREDIT_CARD_NUMBER = "1234567890123456";

	@Test
	public void testFindCreditCard_Found() {
		Creditcard creditcard = new Creditcard();
		creditcard.setCardNumber(CREDIT_CARD_NUMBER);

		when(creditcardRepository.findByCardNumber(CREDIT_CARD_NUMBER)).thenReturn(Optional.of(creditcard));

		Creditcard foundCreditcard = creditcardService.findCreditCard(CREDIT_CARD_NUMBER);

		assertEquals(creditcard, foundCreditcard, "The returned credit card should match the mocked credit card");
	}

	@Test
	public void testFindCreditCard_NotFound() {
	        when(creditcardRepository.findByCardNumber(CREDIT_CARD_NUMBER)).thenReturn(Optional.empty());

	        assertThrows(NotFoundException.class, () -> {
	            creditcardService.findCreditCard(CREDIT_CARD_NUMBER);
	        }, "NotFoundException should be thrown when the credit card is not found");
	    }

	@Test
	public void testSave_NewCreditCard() {
		CreditcardDTO creditcardDTO = new CreditcardDTO(null, null, CREDIT_CARD_NUMBER, null);
		when(creditcardRepository.existsByCardNumber(creditcardDTO.cardNumber())).thenReturn(false);

		Creditcard creditcard = new Creditcard();
		creditcard.setId(1);
		creditcard.setCardNumber(creditcardDTO.cardNumber());
		when(creditcardRepository.save(any(Creditcard.class))).thenReturn(creditcard);

		Creditcard savedCreditcard = creditcardService.save(creditcardDTO);

		assertEquals(creditcard.getCardNumber(), savedCreditcard.getCardNumber());
		assertEquals(creditcard.getId(), savedCreditcard.getId());
		verify(creditcardRepository, times(1)).save(any(Creditcard.class));
	}

	@Test
	public void testSave_CreditCardAlreadyExists() {
		CreditcardDTO creditcardDTO = new CreditcardDTO(null, null, CREDIT_CARD_NUMBER, null);
		when(creditcardRepository.existsByCardNumber(creditcardDTO.cardNumber())).thenReturn(true);

		assertThrows(AlreadyFoundException.class, () -> {
			creditcardService.save(creditcardDTO);
		});

		verify(creditcardRepository, never()).save(any(Creditcard.class));
	}

	@Test
	public void testSaveFromFile_IOException() {
		// Simulating IOException scenario
		InputStream inputStream = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("Simulated I/O exception");
			}
		};

		assertThrows(UploadFailedException.class, () -> creditcardService.saveFromFile(inputStream));
	}

	@Test
	public void testSaveFromFile_SuccessfulProcessing() throws IOException {
		InputStream inputStream = CreditcardServiceTest.class.getClassLoader()
				.getResourceAsStream("upload-example/success.txt");

		Batch batch = new Batch();
		batch.setName("DESAFIO-HYPERATIVA");
		batch.setDate(LocalDate.of(2024, 8, 31));
		batch.setDescription("LOTE0001");
		batch.setCount(10);
	
		UploadResponse expectedResp = new UploadResponse(null, null, 1, 1);

		UploadResponse response = creditcardService.saveFromFile(inputStream);

		assertEquals(expectedResp.totalBatches(), response.totalBatches());
		assertEquals(expectedResp.totalSavedCards(), response.totalSavedCards());
		assertEquals(expectedResp.invalidBatch(), response.invalidBatch());
		assertEquals(expectedResp.repeatedCards(), response.repeatedCards());
		verify(batchRepository, times(1)).save(batch);
	}

}
