package com.teya.ledger.dto;

import com.teya.ledger.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MovementRequest(@NotNull(message = "Transaction type cannot be null")
                              TransactionType type,
                              @NotNull(message = "Amount cannot be null")
                              @Positive(message = "Amount must be greater than zero")
                              BigDecimal amount)
{}
