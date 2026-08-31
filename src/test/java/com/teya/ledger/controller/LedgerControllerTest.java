package com.teya.ledger.controller;

import com.teya.ledger.model.Transaction;
import com.teya.ledger.model.TransactionType;
import com.teya.ledger.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LedgerController.class)
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LedgerService ledgerService;

    private static final String UUID_STR = "123e4567-e89b-12d3-a456-426614174000";
    private final UUID uuid = UUID.fromString(UUID_STR);

    @Test
    void shouldReturnBalanceFromService() throws Exception {
        Mockito.when(ledgerService.getBalance()).thenReturn(new BigDecimal("123.45"));

        mockMvc.perform(get("/api/ledger/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(123.45));
    }

    @Test
    void shouldSuccessfullyPostValidMovement() throws Exception {
        Transaction mockTx = new Transaction(uuid, TransactionType.DEPOSIT, new BigDecimal("100.00"), Instant.now());
        Mockito.when(ledgerService.recordMovement(TransactionType.DEPOSIT, new BigDecimal("100.00"))).thenReturn(mockTx);

        String jsonPayload = "{\"type\":\"DEPOSIT\",\"amount\":100.00}";

        mockMvc.perform(post("/api/ledger/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void shouldReturnHistoryFromService() throws Exception {
        List<Transaction> mockHistory = List.of(
                new Transaction(uuid, TransactionType.DEPOSIT, BigDecimal.TEN, Instant.now())
        );
        Mockito.when(ledgerService.getHistory()).thenReturn(mockHistory);

        mockMvc.perform(get("/api/ledger/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(uuid.toString()));
    }

    @Test
    void shouldReturnCustomErrorWhenTypeIsInvalidString() throws Exception {
        String invalidJsonPayload = "{\"type\":\"BURRITO\",\"amount\":100.00}";

        mockMvc.perform(post("/api/ledger/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid transaction type. Allowed values are: DEPOSIT, WITHDRAWAL"));

        // Verify the service was never even called because Jackson blocked the bad request at the web layer
        Mockito.verifyNoInteractions(ledgerService);
    }
}
