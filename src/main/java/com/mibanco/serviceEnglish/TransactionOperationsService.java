package com.mibanco.serviceEnglish;

import java.math.BigDecimal;
import java.util.Optional;

import com.mibanco.dtoEnglish.TransactionDTO;

public interface TransactionOperationsService {

    Optional<TransactionDTO> deposit(Optional<Long> accountId, Optional<BigDecimal> amount, Optional<String> description);

    Optional<TransactionDTO> withdraw(Optional<Long> accountId, Optional<BigDecimal> amount, Optional<String> description);
    
    Optional<TransactionDTO> transfer(Optional<Long> sourceAccountId, Optional<Long> destinationAccountId, Optional<BigDecimal> amount, Optional<String> description);
    /**
     * Creates a cancellation transaction for an existing transaction
     * @param transactionId Optional with the ID of the transaction to cancel
     * @return Optional with the created cancellation transaction DTO
     */
    Optional<TransactionDTO> cancelTransaction(Optional<Long> transactionId);
}
