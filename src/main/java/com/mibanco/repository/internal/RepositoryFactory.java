package com.mibanco.repository.internal;

import com.mibanco.repository.AccountRepository;
import com.mibanco.repository.AuditRepository;
import com.mibanco.repository.CardRepository;
import com.mibanco.repository.ClientRepository;
import com.mibanco.repository.TransactionRepository;

/**
 * Centralized repository configuration
 * This is the only class that has access to implementations
 * Being in the same package as implementations, it can instantiate them
 */
public final class RepositoryFactory {
    
    private static volatile RepositoryFactory instance;
    private final AuditRepository auditRepository;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Private constructor that initializes all implementations
     * Being in the same package, it has access to package-private classes
     */
    private RepositoryFactory() {
        this.auditRepository = new AuditRepositoryImpl();
        this.clientRepository = new ClientRepositoryImpl();
        this.accountRepository = new AccountRepositoryImpl();
        this.cardRepository = new CardRepositoryImpl();
        this.transactionRepository = new TransactionRepositoryImpl();
    }
    
    /**
     * Gets the single instance of RepositoryFactory
     * This is the only access point to repository implementations
     */
    public static RepositoryFactory getInstance() {
        if (instance == null) {
            synchronized (RepositoryFactory.class) {
                if (instance == null) {
                    instance = new RepositoryFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * Gets the audit repository
     * @return Public interface of the audit repository
     */
    public AuditRepository getAuditRepository() {
        return auditRepository;
    }
    
    /**
     * Gets the client repository
     * @return Public interface of the client repository
     */
    public ClientRepository getClientRepository() {
        return clientRepository;
    }
    
    /**
     * Gets the account repository
     * @return Public interface of the account repository
     */
    public AccountRepository getAccountRepository() {
        return accountRepository;
    }
    
    /**
     * Gets the card repository
     * @return Public interface of the card repository
     */
    public CardRepository getCardRepository() {
        return cardRepository;
    }
    
    /**
     * Gets the transaction repository
     * @return Public interface of the transaction repository
     */
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }
} 