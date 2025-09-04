package com.mibanco.controller.internal;

import com.mibanco.controller.AccountController;
import com.mibanco.controller.util.ControllerValidationUtil;
import com.mibanco.dto.AccountDTO;
import com.mibanco.dto.mappers.AccountMapper;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Account;
import com.mibanco.service.AccountService;
import com.mibanco.view.AccountView;

import java.util.Optional;

/**
 * Implementation of AccountController
 * Following the same pattern as ClientControllerImpl
 */
public class AccountControllerImpl implements AccountController {
    
    private final AccountService accountService;
    private final AccountView accountView;
    private final AccountMapper accountMapper;
    
    public AccountControllerImpl(AccountService accountService, AccountView accountView) {
        this.accountService = accountService;
        this.accountView = accountView;
        this.accountMapper = new AccountMapper(new ClientMapper());
    }
    
    @Override
    public boolean createAccount() {
        return accountView.captureDataAccount()
            .filter(data -> ControllerValidationUtil.hasRequiredFields(data, Account.class))
            .flatMap(accountData -> {
                // Extract initial balance from the data
                String balanceStr = accountData.get("initialBalance");
                if (balanceStr == null || balanceStr.trim().isEmpty()) {
                    return Optional.empty();
                }
                
                try {
                    java.math.BigDecimal initialBalance = new java.math.BigDecimal(balanceStr);
                    // The service handles the initial deposit internally without needing TransactionOperationsService
                    return accountService.createAccountDto(accountData, initialBalance, null);
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            })
            .isPresent();
    }
    
    @Override
    public boolean searchAccount() {
        return accountView.searchAccount()
            .map(searchCriteria -> {
                String searchType = searchCriteria.get("searchType");
                String searchValue = searchCriteria.get("searchValue");
                
                Optional<AccountDTO> foundAccount = switch (searchType) {
                    case "accountNumber" -> {
                        try {
                            Long accountId = Long.parseLong(searchValue);
                            yield accountService.getAccountByNumber(Optional.of(accountId));
                        } catch (NumberFormatException e) {
                            yield Optional.empty();
                        }
                    }
                    case "holderId" -> {
                        try {
                            Long holderId = Long.parseLong(searchValue);
                            yield accountService.searchByHolderId(Optional.of(holderId))
                                .flatMap(accounts -> accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0)));
                        } catch (NumberFormatException e) {
                            yield Optional.empty();
                        }
                    }
                    default -> Optional.empty();
                };
                
                // Si se encontró la cuenta, mapearla a entidad y mostrarla
                foundAccount.ifPresent(accountDto -> {
                    Account accountEntity = this.accountMapper.toEntityDirect(accountDto).orElse(null);
                    if (accountEntity != null) {
                        accountView.displayAccount(accountEntity);
                    }
                });
                
                return foundAccount.isPresent();
            })
            .orElse(false);
    }

    // Placeholder methods - will be implemented one by one
    
    @Override
    public boolean updateAccountBalance() {
        return false; // TODO: Implement
    }
    
    @Override
    public boolean updateAccountStatus() {
        return false; // TODO: Implement
    }
    
    @Override
    public boolean updateAccountHolder() {
        return false; // TODO: Implement
    }
    
    @Override
    public boolean updateAccountMultipleFields() {
        return false; // TODO: Implement
    }
    
    @Override
    public boolean deleteAccount() {
        return false; // TODO: Implement
    }
    
    @Override
    public boolean restoreAccount() {
        return false; // TODO: Implement
    }
    
    @Override
    public boolean listAllAccounts() {
        return false; // TODO: Implement
    }
    
    @Override
    public void showAccountMenu() {
        // TODO: Implement
    }
    
    @Override
    public void showUpdateAccountMenu() {
        // TODO: Implement
    }
}
