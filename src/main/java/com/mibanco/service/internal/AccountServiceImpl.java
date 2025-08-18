package com.mibanco.service.internal;

import com.mibanco.dto.AccountDTO;
import com.mibanco.dto.mappers.AccountMapper;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Account;
import com.mibanco.model.enums.AccountOperationType;
import com.mibanco.model.enums.AccountType;
import com.mibanco.repository.AccountRepository;
import com.mibanco.repository.internal.RepositoryFactory;
import com.mibanco.service.AccountService;
import com.mibanco.service.ClientService;
import com.mibanco.service.TransactionOperationsService;
import com.mibanco.util.ValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Account service implementation
 */
class AccountServiceImpl extends BaseServiceImpl<AccountDTO, Account, Long, AccountOperationType, AccountRepository> 
        implements AccountService {
    
    private static final AccountRepository accountRepository;
    private static ClientService clientService;
    private static final AccountMapper mapper;
    private static final ClientMapper clientMapper;
    private final AccountOperationType updateType = AccountOperationType.UPDATE;
    private static AccountDtoProcessorService accountDtoProcessor;
    
    static {
        accountRepository = RepositoryFactory.getInstance().getAccountRepository();
        clientMapper = new ClientMapper();
        mapper = new AccountMapper(clientMapper);
        clientService = ServiceFactory.getInstance().getClientService();
        accountDtoProcessor = new AccountDtoProcessorService(clientService);
    }

    public AccountServiceImpl() {
        super(accountRepository, mapper);
    }

    @Override
    public Optional<AccountDTO> updateMultipleFields(Long accountId, Optional<AccountDTO> accountDTO) {
        return super.updateMultipleFields(
            accountId,
            accountDTO,
            updateType,
            (existingAccount, newAccount) -> existingAccount.withUpdates(
                Optional.ofNullable(newAccount.getBalance()),
                Optional.ofNullable(newAccount.isActive())
            )
        );
    }

    @Override
    public Optional<AccountDTO> getAccountByNumber(Optional<Long> accountId) {
        return accountId.flatMap(id -> 
            repository.findById(Optional.of(id))
                .flatMap(account -> mapper.toDto(Optional.of(account)))
        );
    }

    @Override
    public Optional<List<AccountDTO>> getAllAccounts() {
        return findAll();
    }

    @Override
    public Optional<AccountDTO> updateAccountBalance(Long accountId, Optional<BigDecimal> newBalance) {
        Optional<AccountDTO> updateBalance = updateField(
            accountId,
            newBalance,
            AccountDTO::getBalance,
            AccountDTO::withBalance
        );
        saveEntity(updateType, updateBalance);
        return updateBalance;
    }

    @Override
    public Optional<AccountDTO> updateAccountStatus(Long accountId, Optional<Boolean> newActive) {
        Optional<AccountDTO> updateStatus = updateField(
            accountId,
            newActive,
            AccountDTO::isActive,
            AccountDTO::withActive
        );
        saveEntity(updateType, updateStatus);
        return updateStatus;
    }

    @Override
    public Optional<AccountDTO> updateAccountHolder(Long accountId, Optional<AccountDTO> newHolder) {
        Optional<AccountDTO> updateHolder = newHolder.flatMap(holderDTO -> 
            clientMapper.toEntity(Optional.of(holderDTO.getHolder()))
                .flatMap(holder -> updateField(
                    accountId,
                    Optional.of(holder),
                    AccountDTO::getHolder,
                    (account, holderEntity) -> account.toBuilder().holder(holderDTO.getHolder() ).build()
                ))
        );
        saveEntity(updateType, updateHolder);
        return updateHolder;
    }

    @Override
    public boolean deleteAccount(Optional<Long> accountId) {
        return deleteById(accountId, AccountOperationType.DELETE);
    }

    @Override
    public Optional<AccountDTO> deleteByNumber(Optional<Long> accountId) {
        return repository.deleteById(accountId, AccountOperationType.DELETE)
            .flatMap(account -> mapper.toDto(Optional.of(account)));
    }

    @Override
    public Optional<AccountDTO> restoreAccount(Optional<Long> accountId) {
        return restore(accountId, AccountOperationType.RESTORE);
    }

    @Override
    public long countAccounts() {
        return countRecords();
    }

    @Override
    public void setCurrentUser(String user) {
        super.setCurrentUser(user);
    }

    @Override
    public Optional<List<AccountDTO>> searchByHolderId(Optional<Long> holderId) {
        return repository.findAllByPredicate(account -> account.getHolder().getId().equals(holderId.get()))        
            .map(accounts -> accounts.stream()
                .map(account -> mapper.toDto(Optional.of(account)).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Optional<List<AccountDTO>> searchByType(Optional<AccountType> type) {
        return repository.findAllByPredicate(account -> account.getType().equals(type.get()))
            .map(accounts -> accounts.stream()
                .map(account -> mapper.toDto(Optional.of(account)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public Optional<List<AccountDTO>> searchActive() {
            return repository.findAllByPredicate(account -> account.isActive())
            .map(accounts -> accounts.stream()
                .map(account -> mapper.toDto(Optional.of(account)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    /**
     * Creates an account DTO with mandatory initial balance using functional programming
     * The account is created once with the initial balance included
     * Eliminates nested ifs using Optional chaining and early returns
     * Guarantees transactional consistency: validates before persisting
     */
    public Optional<AccountDTO> createAccountDto(Map<String, String> rawData, BigDecimal initialAmount, TransactionOperationsService transactionService) {
        try {
            return accountDtoProcessor.processAccountDto(rawData)
                .flatMap(accountDTO -> {
                    // Validate unique account number before saving
                    validateUniqueAccountNumber(accountDTO);
                    return saveEntity(AccountOperationType.CREATE, Optional.of(accountDTO));
                })
                .flatMap(savedAccount -> accountDtoProcessor.processInitialDeposit(savedAccount, initialAmount, transactionService))
                .or(() -> Optional.empty());
        } catch (ValidationException e) {
            // Re-throw the exception for the view to handle
            throw e;
        }
    }
    
    /**
     * Helper method to validate unique account number
     * @param dto Account DTO to validate
     * @throws ValidationException if the account number already exists
     */
    private void validateUniqueAccountNumber(AccountDTO dto) {
        // Only validate if the number is not null (basic validation already done in view)
        if (dto.getAccountNumber() != null) {
            Optional<Account> existingAccount = accountRepository.findByPredicate(
                account -> dto.getAccountNumber().equals(account.getAccountNumber())
            );
            
            if (existingAccount.isPresent()) {
                throw new ValidationException("An account with number already exists: " + dto.getAccountNumber());
            }
        }
    }
} 