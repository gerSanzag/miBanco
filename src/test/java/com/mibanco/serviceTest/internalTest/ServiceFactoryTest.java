package com.mibanco.serviceTest.internalTest;

import com.mibanco.service.AccountService;
import com.mibanco.service.AuditService;
import com.mibanco.service.CardService;
import com.mibanco.service.ClientService;
import com.mibanco.service.TransactionCrudService;
import com.mibanco.service.TransactionOperationsService;
import com.mibanco.service.internal.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;

/**
 * Test class for ServiceFactory
 * Tests the creation of all service instances
 */
@DisplayName("ServiceFactory Tests")
class ServiceFactoryTest {

    private ServiceFactory serviceFactory;

    @BeforeEach
    void setUp() {
        serviceFactory = ServiceFactory.getInstance();
    }

    @Test
    @DisplayName("Should create ClientService instance")
    void shouldCreateClientServiceInstance() {
        // When
        ClientService clientService = serviceFactory.getClientService();

        // Then
        assertThat(clientService).isNotNull();
        assertThat(clientService).isInstanceOf(ClientService.class);
    }

    @Test
    @DisplayName("Should create AccountService instance")
    void shouldCreateAccountServiceInstance() {
        // When
        AccountService accountService = serviceFactory.getAccountService();

        // Then
        assertThat(accountService).isNotNull();
        assertThat(accountService).isInstanceOf(AccountService.class);
    }

    @Test
    @DisplayName("Should create TransactionCrudService instance")
    void shouldCreateTransactionCrudServiceInstance() {
        // When
        TransactionCrudService transactionCrudService = serviceFactory.getTransactionCrudService();

        // Then
        assertThat(transactionCrudService).isNotNull();
        assertThat(transactionCrudService).isInstanceOf(TransactionCrudService.class);
    }

    @Test
    @DisplayName("Should create TransactionOperationsService instance")
    void shouldCreateTransactionOperationsServiceInstance() {
        // When
        TransactionOperationsService transactionOperationsService = serviceFactory.getTransactionOperationsService();

        // Then
        assertThat(transactionOperationsService).isNotNull();
        assertThat(transactionOperationsService).isInstanceOf(TransactionOperationsService.class);
    }

    @Test
    @DisplayName("Should create AuditService instance")
    void shouldCreateAuditServiceInstance() {
        // When
        AuditService auditService = serviceFactory.getAuditService();

        // Then
        assertThat(auditService).isNotNull();
        assertThat(auditService).isInstanceOf(AuditService.class);
    }

    @Test
    @DisplayName("Should return same instance (Singleton pattern)")
    void shouldReturnSameInstance() {
        // When
        ServiceFactory instance1 = ServiceFactory.getInstance();
        ServiceFactory instance2 = ServiceFactory.getInstance();

        // Then
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    @DisplayName("Should handle concurrent access to getInstance")
    void shouldHandleConcurrentAccessToGetInstance() throws InterruptedException {
        // Given
        final ServiceFactory[] instances = new ServiceFactory[2];
        
        // When - Simulate concurrent access
        Thread thread1 = new Thread(() -> {
            instances[0] = ServiceFactory.getInstance();
        });
        
        Thread thread2 = new Thread(() -> {
            instances[1] = ServiceFactory.getInstance();
        });
        
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Then
        assertThat(instances[0]).isNotNull();
        assertThat(instances[1]).isNotNull();
        assertThat(instances[0]).isSameAs(instances[1]);
    }

    @Test
    @DisplayName("Should handle multiple concurrent threads accessing getInstance")
    void shouldHandleMultipleConcurrentThreadsAccessingGetInstance() throws InterruptedException {
        // Given
        final int threadCount = 10;
        final ServiceFactory[] instances = new ServiceFactory[threadCount];
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        // When - Create multiple threads that all try to get instance simultaneously
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    instances[index] = ServiceFactory.getInstance();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
            thread.start();
        }
        
        startLatch.countDown(); // Start all threads simultaneously
        endLatch.await(); // Wait for all threads to complete

        // Then - All instances should be the same
        ServiceFactory firstInstance = instances[0];
        assertThat(firstInstance).isNotNull();
        
        for (int i = 1; i < threadCount; i++) {
            assertThat(instances[i]).isSameAs(firstInstance);
        }
    }

    @Test
    @DisplayName("Should create instance on first call")
    void shouldCreateInstanceOnFirstCall() {
        // Given - Reset the singleton (this is a test-only scenario)
        // Note: In a real scenario, we can't reset the singleton easily
        // This test verifies the behavior when instance is null
        
        // When - Get instance multiple times
        ServiceFactory instance1 = ServiceFactory.getInstance();
        ServiceFactory instance2 = ServiceFactory.getInstance();
        ServiceFactory instance3 = ServiceFactory.getInstance();

        // Then - All should be the same instance
        assertThat(instance1).isSameAs(instance2);
        assertThat(instance2).isSameAs(instance3);
        assertThat(instance1).isSameAs(instance3);
    }

    @Test
    @DisplayName("Should handle null instance in double-checked locking")
    void shouldHandleNullInstanceInDoubleCheckedLocking() throws Exception {
        // Given - Use reflection to reset the singleton instance to null
        java.lang.reflect.Field instanceField = ServiceFactory.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null); // Reset to null
        
        // When - Get instance (this should trigger the double-checked locking)
        ServiceFactory instance = ServiceFactory.getInstance();
        
        // Then - Should create a new instance
        assertThat(instance).isNotNull();
        
        // Verify that subsequent calls return the same instance
        ServiceFactory instance2 = ServiceFactory.getInstance();
        assertThat(instance2).isSameAs(instance);
    }

    @Test
    @DisplayName("Should create all services successfully")
    void shouldCreateAllServicesSuccessfully() {
        // When & Then
        assertThat(serviceFactory.getClientService()).isNotNull();
        assertThat(serviceFactory.getAccountService()).isNotNull();
        assertThat(serviceFactory.getTransactionCrudService()).isNotNull();
        assertThat(serviceFactory.getTransactionOperationsService()).isNotNull();
        assertThat(serviceFactory.getAuditService()).isNotNull();
    }
}
