package com.mibanco.repository.internal;

import com.mibanco.dto.AccountDTO;
import com.mibanco.dto.mappers.AccountMapper;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Account;
import com.mibanco.model.enums.AccountOperationType;
import com.mibanco.model.enums.AccountType;
import com.mibanco.repository.AccountRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of Account repository
 * Visibility restricted to internal package
 */
class AccountRepositoryImpl extends BaseRepositoryImpl<Account, Long, AccountOperationType> implements AccountRepository {
    
    /**
     * Constructor with package visibility
     */
    AccountRepositoryImpl() {
        super();
    }
    
    /**
     * Specific implementation to assign new ID to Account
     * ALWAYS generates a new random account number
     * This method is only called from createRecord() when the entity has no ID
     * Generates account numbers in Spanish IBAN format: ES34 + 20 random digits
     * Uses DTOs to maintain entity immutability
     * Pure functional approach with Optional
     * @param account Account without assigned ID
     * @return Account with new assigned ID
     */
    @Override
    protected Account createWithNewId(Account account) {
        ClientMapper clientMapper = new ClientMapper();
        AccountMapper mapper = new AccountMapper(clientMapper);
        
        return mapper.toDtoDirect(account)
            .map(dto -> {
                // ALWAYS generate a new random account number
                StringBuilder randomNumber = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    randomNumber.append((int) (Math.random() * 10));
                }
                String accountNumber = "ES34" + randomNumber.toString();
                
                return dto.toBuilder()
                    .accountNumber(accountNumber)
                    .build();
            })
            .flatMap(mapper::toEntityDirect)
            .orElseThrow(() -> new IllegalStateException("Could not process Account entity"));
    }
    
    /**
     * Specific implementation to get repository configuration
     * @return Map with necessary configuration
     */
    @Override
    protected Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", "src/main/resources/data/cuenta.json");
        config.put("classType", Account.class);
        config.put("idExtractor", (Function<Account, Long>) Account::getId);
        return config;
    }
} 