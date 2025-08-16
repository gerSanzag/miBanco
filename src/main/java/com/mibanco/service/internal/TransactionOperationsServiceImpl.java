package com.mibanco.service.internal;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.dto.mappers.TransactionMapper;
import com.mibanco.model.Transaction;
import com.mibanco.model.enums.TransactionOperationType;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.repository.TransactionRepository;
import com.mibanco.repository.internal.RepositoryFactory;
import com.mibanco.service.AccountService;
import com.mibanco.service.TransactionOperationsService;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class TransactionOperationsServiceImpl extends BaseServiceImpl<TransactionDTO, Transaction, Long, TransactionOperationType, TransactionRepository> implements TransactionOperationsService {

    private static final TransactionRepository transactionRepository;
    private static final TransactionMapper mapper;
    private static final AccountService accountService;
    private final Set<Long> blockedAccounts = new HashSet<>();
    private final Object lock = new Object();

    static {
        transactionRepository = RepositoryFactory.getInstance().getTransactionRepository();
        mapper = new TransactionMapper();
        accountService = new AccountServiceImpl();
    }

    public TransactionOperationsServiceImpl() {
        super(transactionRepository, mapper);
    }

    // Static map of transaction type inversions
    private static final Map<TransactionType, TransactionType> TYPE_INVERSIONS = ofEntries(
        entry(TransactionType.DEPOSIT, TransactionType.WITHDRAWAL),
        entry(TransactionType.WITHDRAWAL, TransactionType.DEPOSIT),
        entry(TransactionType.SENT_TRANSFER, TransactionType.RECEIVED_TRANSFER),
        entry(TransactionType.RECEIVED_TRANSFER, TransactionType.SENT_TRANSFER)
    );
 

    private boolean blockAccount(Optional<Long> accountId) {
        return accountId.map(id -> {
            synchronized (lock) {
                if (blockedAccounts.contains(id)) {
                    return false;
                }
                blockedAccounts.add(id);
                return true;
            }
        }).orElse(false);
    }

    private void releaseAccount(Optional<Long> accountId) {
        accountId.ifPresent(id -> {
            synchronized (lock) {
                blockedAccounts.remove(id);
            }
        });
    }

    @Override
    public Optional<TransactionDTO> deposit(Optional<Long> accountId, Optional<BigDecimal> amount, Optional<String> description) {
        return accountId
            .flatMap(id -> {
                // Try to block the account
                if (!blockAccount(Optional.of(id))) {
                    return Optional.empty();
                }
                
                try {
                    // Get the account
                    return accountService.getAccountByNumber(Optional.of(id))
                        .flatMap(accountDTO -> {
                            // If the account is not active, activate it
                            if (!accountDTO.isActive()) {
                                return accountService.updateAccountStatus(id, Optional.of(true));
                            }
                            return Optional.of(accountDTO);
                        })
                        .flatMap(accountDTO -> {
                            // Update the balance
                            BigDecimal newBalance = accountDTO.getBalance().add(amount.get());
                            return accountService.updateAccountBalance(id, Optional.of(newBalance));
                        })
                        .flatMap(updatedAccount -> {
                            // Create the transaction
                            TransactionDTO transactionDTO = TransactionDTO.builder()
                                .accountNumber(updatedAccount.getAccountNumber())
                                .type(TransactionType.DEPOSIT)
                                .amount(amount.get())
                                .description(description.orElse("Deposit"))
                                .date(LocalDateTime.now())
                                .build();
                            
                            return saveEntity(TransactionOperationType.CREATE, Optional.of(transactionDTO));
                        });
                } finally {
                    // Always release the account
                    releaseAccount(Optional.of(id));
                }
            });
    }
    
    @Override
    public Optional<TransactionDTO> withdraw(Optional<Long> accountId, Optional<BigDecimal> amount, Optional<String> description) {
        return accountId
            .flatMap(id -> {
                // Try to block the account
                if (!blockAccount(Optional.of(id))) {
                    return Optional.empty();
                }
                
                try {
                    // Get the account
                    return accountService.getAccountByNumber(Optional.of(id))
                        .flatMap(accountDTO -> {
                            // Check if the account is active
                            if (!accountDTO.isActive()) {
                                return Optional.empty();
                            }
                            
                            // Check if there's enough balance
                            if (accountDTO.getBalance().compareTo(amount.get()) < 0) {
                                return Optional.empty();
                            }
                            
                            return Optional.of(accountDTO);
                        })
                        .flatMap(accountDTO -> {
                            // Update the balance
                            BigDecimal newBalance = accountDTO.getBalance().subtract(amount.get());
                            return accountService.updateAccountBalance(id, Optional.of(newBalance));
                        })
                        .flatMap(updatedAccount -> {
                            // Create the transaction
                            TransactionDTO transactionDTO = TransactionDTO.builder()
                                .accountNumber(updatedAccount.getAccountNumber())
                                .type(TransactionType.WITHDRAWAL)
                                .amount(amount.get())
                                .description(description.orElse("Withdrawal"))
                                .date(LocalDateTime.now())
                                .build();
                            
                            return saveEntity(TransactionOperationType.CREATE, Optional.of(transactionDTO));
                        });
                } finally {
                    // Always release the account
                    releaseAccount(Optional.of(id));
                }
            });
    }
    
    @Override
    public Optional<TransactionDTO> transfer(Optional<Long> sourceAccountId, Optional<Long> destinationAccountId, Optional<BigDecimal> amount, Optional<String> description) {
        return sourceAccountId
            .flatMap(sourceId -> destinationAccountId
                .flatMap(destId -> {
                    // Try to block both accounts
                    if (!blockAccount(Optional.of(sourceId)) || !blockAccount(Optional.of(destId))) {
                        return Optional.empty();
                    }
                    
                    try {
                        // Get the source account
                        return accountService.getAccountByNumber(Optional.of(sourceId))
                            .flatMap(sourceAccount -> {
                                // Check if the source account is active
                                if (!sourceAccount.isActive()) {
                                    return Optional.empty();
                                }
                                
                                // Check if there's enough balance
                                if (sourceAccount.getBalance().compareTo(amount.get()) < 0) {
                                    return Optional.empty();
                                }
                                
                                return Optional.of(sourceAccount);
                            })
                            .flatMap(sourceAccount -> {
                                // Get the destination account
                                return accountService.getAccountByNumber(Optional.of(destinationAccountId.get()))
                                    .flatMap(destAccount -> {
                                        // Check if the destination account is active
                                        if (!destAccount.isActive()) {
                                            return Optional.empty();
                                        }
                                        
                                        return Optional.of(destAccount);
                                    })
                                    .flatMap(destAccount -> {
                                        // Update source account balance
                                        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount.get());
                                        return accountService.updateAccountBalance(sourceId, Optional.of(newSourceBalance))
                                            .flatMap(updatedSourceAccount -> {
                                                // Update destination account balance
                                                BigDecimal newDestBalance = destAccount.getBalance().add(amount.get());
                                                return accountService.updateAccountBalance(destinationAccountId.get(), Optional.of(newDestBalance))
                                                    .flatMap(updatedDestAccount -> {
                                                        // Create the sent transaction
                                                        TransactionDTO sentTransaction = TransactionDTO.builder()
                                                            .accountNumber(updatedSourceAccount.getAccountNumber())
                                                            .destinationAccountNumber(updatedDestAccount.getAccountNumber())
                                                            .type(TransactionType.SENT_TRANSFER)
                                                            .amount(amount.get())
                                                            .description(description.orElse("Transfer sent"))
                                                            .date(LocalDateTime.now())
                                                            .build();
                                                        
                                                        return saveEntity(TransactionOperationType.CREATE, Optional.of(sentTransaction));
                                                    });
                                            });
                                    });
                            });
                    } finally {
                        // Always release both accounts
                        releaseAccount(Optional.of(sourceId));
                        releaseAccount(Optional.of(destId));
                    }
                }));
    }
    
    @Override
    public Optional<TransactionDTO> cancelTransaction(Optional<Long> transactionId) {
        return transactionId
            .flatMap(id -> findById(Optional.of(id))
                .flatMap(transactionDTO -> {
                    // Get the inverse transaction type
                    TransactionType inverseType = TYPE_INVERSIONS.get(transactionDTO.getType());
                    if (inverseType == null) {
                        return Optional.empty();
                    }
                    
                    // Get the account
                    return accountService.getAccountByNumber(Optional.of(Long.parseLong(transactionDTO.getAccountNumber())))
                        .flatMap(accountDTO -> {
                            // Apply the inverse operation
                            BigDecimal newBalance;
                            if (transactionDTO.getType() == TransactionType.DEPOSIT || transactionDTO.getType() == TransactionType.RECEIVED_TRANSFER) {
                                // If it was a deposit/received transfer, we subtract
                                newBalance = accountDTO.getBalance().subtract(transactionDTO.getAmount());
                            } else {
                                // If it was a withdrawal/sent transfer, we add
                                newBalance = accountDTO.getBalance().add(transactionDTO.getAmount());
                            }
                            
                            return accountService.updateAccountBalance(Long.parseLong(transactionDTO.getAccountNumber()), Optional.of(newBalance));
                        })
                        .flatMap(updatedAccount -> {
                            // Create the cancellation transaction
                            TransactionDTO cancellationTransaction = TransactionDTO.builder()
                                .accountNumber(updatedAccount.getAccountNumber())
                                .type(inverseType)
                                .amount(transactionDTO.getAmount())
                                .description("Cancellation of transaction " + id)
                                .date(LocalDateTime.now())
                                .build();
                            
                            return saveEntity(TransactionOperationType.CREATE, Optional.of(cancellationTransaction));
                        });
                }));
    }
}
