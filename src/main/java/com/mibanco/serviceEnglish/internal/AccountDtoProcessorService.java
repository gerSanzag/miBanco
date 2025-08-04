package com.mibanco.serviceEnglish.internal;

import com.mibanco.dtoEnglish.ClientDTO;
import com.mibanco.dtoEnglish.AccountDTO;
import com.mibanco.dtoEnglish.mappers.ClientMapper;
import com.mibanco.dtoEnglish.mappers.AccountMapper;
import com.mibanco.serviceEnglish.ClientService;
import com.mibanco.serviceEnglish.TransactionOperationsService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Specialized service for processing AccountDTO creation
 * Applies the Single Responsibility Principle (SRP)
 * Responsible solely for transforming raw data into valid AccountDTOs
 * and processing account-related operations
 */
public class AccountDtoProcessorService {
    
    private final ClientService clientService;
    
    public AccountDtoProcessorService(ClientService clientService) {
        this.clientService = clientService;
    }
    
    /**
     * Processes raw data and creates a valid AccountDTO
     * @param rawData Map with account data
     * @return Optional with processed AccountDTO or empty if there are errors
     */
    public Optional<AccountDTO> processAccountDto(Map<String, String> rawData) {
        return Optional.of(rawData)
            .flatMap(this::getHolderById)
            .flatMap(holder -> buildAccountDTO(rawData, holder));
    }
    
    /**
     * Processes initial deposit and prepares account with balance
     * @param createdAccount The account already created
     * @param initialAmount The initial deposit amount
     * @param transactionService Service for processing transactions
     * @return Optional with account prepared with balance or empty if validation fails
     */
    public Optional<AccountDTO> processInitialDeposit(AccountDTO createdAccount, 
                                                      BigDecimal initialAmount, 
                                                      TransactionOperationsService transactionService) {
        // Convert DTO to entity to get ID
        ClientMapper clientMapper = new ClientMapper();
        AccountMapper accountMapper = new AccountMapper(clientMapper);
        
        return accountMapper.toEntityDirect(createdAccount)
            .flatMap(accountEntity -> {
                Long accountId = accountEntity.getId();
                return transactionService.deposit(
                    Optional.of(accountId),
                    Optional.of(initialAmount),
                    Optional.of("Initial opening deposit")
                )
                .map(transaction -> updateAccountWithBalance(createdAccount, initialAmount));
            })
            .or(() -> Optional.empty());
    }
    
    /**
     * Updates account with initial balance (without persistence)
     * @param account The account to update
     * @param balance The initial balance
     * @return The updated account
     */
    public AccountDTO updateAccountWithBalance(AccountDTO account, BigDecimal balance) {
        return account.toBuilder()
            .initialBalance(balance)
            .balance(balance)
            .build();
    }
    
    /**
     * Extracts holder ID and gets client in a single functional method
     */
    private Optional<ClientDTO> getHolderById(Map<String, String> rawData) {
        return Optional.ofNullable(rawData.get("holderId"))
            .map(Long::parseLong)
            .flatMap(holderId -> clientService.getClientById(Optional.of(holderId)));
    }
    
    /**
     * Builds the AccountDTO from raw data and holder
     */
    private Optional<AccountDTO> buildAccountDTO(Map<String, String> rawData, ClientDTO holder) {
        try {
            AccountDTO.AccountDTOBuilder builder = AccountDTO.builder()
                .holder(holder);

            // Apply functional transformations
            Optional.ofNullable(rawData.get("type"))
                .map(com.mibanco.modelEnglish.enums.AccountType::valueOf)
                .ifPresent(builder::type);

            Optional.ofNullable(rawData.get("creationDate"))
                .map(java.time.LocalDateTime::parse)
                .ifPresent(builder::creationDate);

            // Active status by default
            Optional.ofNullable(rawData.get("active"))
                .map(Boolean::parseBoolean)
                .or(() -> Optional.of(true))
                .ifPresent(builder::active);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 