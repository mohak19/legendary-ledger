package com.teya.ledger.dto;

import com.teya.ledger.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id,
                                  TransactionType type,
                                  BigDecimal amount,
                                  Instant timestamp)
{}
