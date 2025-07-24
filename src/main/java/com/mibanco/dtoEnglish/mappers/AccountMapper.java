package com.mibanco.dtoEnglish.mappers;

import com.mibanco.dtoEnglish.AccountDTO;
import com.mibanco.modelEnglish.Account;
import com.mibanco.modelEnglish.Client;
import com.mibanco.dtoEnglish.ClientDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper implementation for Account using functional approach
 */
public class AccountMapper implements Mapper<Account, AccountDTO> {
    private final Mapper<Client, ClientDTO> clientMapper;
    
    public AccountMapper(Mapper<Client, ClientDTO> clientMapper) {
        this.clientMapper = clientMapper;
    }

    /**
     * Converts an Account to AccountDTO
     * Strictly functional implementation with Optional
     */
    @Override
    public Optional<AccountDTO> toDto(Optional<Account> accountOpt) {
        return accountOpt.map(account -> AccountDTO.builder()
            .accountNumber(account.getAccountNumber()) // numeroCuenta
            .holder(account.getHolder() != null ? 
                clientMapper.toDto(Optional.of(account.getHolder())).orElse(null) : null) // titular
            .type(account.getType()) // tipo
            .initialBalance(account.getInitialBalance()) // saldoInicial
            .balance(account.getBalance()) // saldo
            .creationDate(account.getCreationDate()) // fechaCreacion
            .active(account.isActive()) // activa
            .build());
    }

    /**
     * Converts an AccountDTO to Account
     * Strictly functional implementation with Optional
     */
    @Override
    public Optional<Account> toEntity(Optional<AccountDTO> dtoOpt) {
        return dtoOpt.map(dto -> Account.builder()
            .accountNumber(dto.getAccountNumber()) // numeroCuenta
            .holder(dto.getHolder() != null ? 
                clientMapper.toEntity(Optional.of(dto.getHolder())).orElse(null) : null) // titular
            .type(dto.getType()) // tipo
            .initialBalance(dto.getInitialBalance()) // saldoInicial
            .balance(dto.getBalance()) // saldo
            .creationDate(dto.getCreationDate()) // fechaCreacion
            .active(dto.isActive()) // activa
            .build());
    }
   
    /**
     * Overload that accepts Account directly
     * For convenience in contexts not using Optional
     */
    public Optional<AccountDTO> toDtoDirect(Account account) {
        return toDto(Optional.ofNullable(account));
    }
    
    /**
     * Overload that accepts AccountDTO directly
     * For convenience in contexts not using Optional
     */
    public Optional<Account> toEntityDirect(AccountDTO dto) {
        return toEntity(Optional.ofNullable(dto));
    }
    
    /**
     * Converts a list of Account to a list of AccountDTO
     * Using functional programming with streams
     * @return Optional with the list of DTOs or Optional.empty() if input is null
     */
    public Optional<List<AccountDTO>> toDtoList(Optional<List<Account>> accounts) {
        return accounts.map(list -> list.stream()
                        .map(account -> toDto(Optional.of(account)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<Account> directly
     */
    public Optional<List<AccountDTO>> toDtoList(List<Account> accounts) {
        return toDtoList(Optional.ofNullable(accounts));
    }
    
    /**
     * Converts a list of AccountDTO to a list of Account
     * Using functional programming with streams
     * @return Optional with the list of entities or Optional.empty() if input is null
     */
    public Optional<List<Account>> toEntityList(Optional<List<AccountDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> toEntity(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<AccountDTO> directly
     */
    public Optional<List<Account>> toEntityList(List<AccountDTO> dtos) {
        return toEntityList(Optional.ofNullable(dtos));
    }
} 