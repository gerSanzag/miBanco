package com.mibanco.serviceEnglish.internal;

import com.mibanco.serviceEnglish.AuditService;
import com.mibanco.serviceEnglish.ClientService;
import com.mibanco.serviceEnglish.AccountService;
import com.mibanco.serviceEnglish.CardService;
import com.mibanco.serviceEnglish.TransactionCrudService;
import com.mibanco.serviceEnglish.TransactionOperationsService;

/**
 * Service factory that exposes implementations
 * This is the only class that has access to internal implementations
 */
public final class ServiceFactory {
    
    private static volatile ServiceFactory instance;
    private final ClientService clientService;
    private final AccountService accountService;
    // private final CardService cardService;
    private final TransactionCrudService transactionCrudService;
    private final TransactionOperationsService transactionOperationsService;
    private final AuditService auditService;
    
    /**
     * Private constructor that initializes all implementations
     * Being in the same package, it has access to package-private classes
     */
    private ServiceFactory() {
        this.clientService = new ClientServiceImpl();
        this.accountService = new AccountServiceImpl();
        // this.cardService = new CardServiceImpl();
        this.transactionCrudService = new TransactionCrudServiceImpl();
        this.transactionOperationsService = new TransactionOperationsServiceImpl();
        this.auditService = new AuditServiceImpl();
    }
    
    /**
     * Gets the single instance of ServiceFactory
     * This is the only access point to service implementations
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * Gets the client service
     * @return Public interface of client service
     */
    public ClientService getClientService() {
        return clientService;
    }
    
    /**
     * Gets the account service
     * @return Public interface of account service
     */
    public AccountService getAccountService() {
        return accountService;
    }
    
    /**
     * Gets the card service
     * @return Public interface of card service
     */
    // public CardService getCardService() {
    //     return cardService;
    // }
    
    /**
     * Gets the transaction CRUD service
     * @return Public interface of transaction CRUD service
     */
    public TransactionCrudService getTransactionCrudService() {
        return transactionCrudService;
    }
    
    /**
     * Gets the transaction operations service
     * @return Public interface of transaction operations service
     */
    public TransactionOperationsService getTransactionOperationsService() {
        return transactionOperationsService;
    }
    
    /**
     * Gets the audit service
     * @return Public interface of audit service
     */
    public AuditService getAuditService() {
        return auditService;
    }
} 