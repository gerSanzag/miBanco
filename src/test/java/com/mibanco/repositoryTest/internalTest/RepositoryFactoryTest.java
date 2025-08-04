package com.mibanco.repositoryTest.internalTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.repository.AccountRepository;
import com.mibanco.repository.AuditRepository;
import com.mibanco.repository.CardRepository;
import com.mibanco.repository.ClientRepository;
import com.mibanco.repository.TransactionRepository;
import com.mibanco.repository.internal.RepositoryFactory;

import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RepositoryFactory
 * Verifies the Singleton pattern with Double-Checked Locking
 */
@DisplayName("RepositoryFactory Tests")
class RepositoryFactoryTest {

    @Nested
    @DisplayName("Tests for getInstance")
    class GetInstanceTests {

        @Test
        @DisplayName("Should return the same instance in consecutive calls")
        void shouldReturnSameInstance() {
            // Act
            RepositoryFactory instance1 = RepositoryFactory.getInstance();
            RepositoryFactory instance2 = RepositoryFactory.getInstance();
            RepositoryFactory instance3 = RepositoryFactory.getInstance();

            // Assert
            assertNotNull(instance1);
            assertSame(instance1, instance2);
            assertSame(instance2, instance3);
        }

        @Test
        @DisplayName("Should handle concurrency correctly (cover the second if)")
        void shouldHandleConcurrencyCorrectly() throws InterruptedException {
            // Arrange
            int numThreads = 10;
            int numCallsPerThread = 100;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);
            List<RepositoryFactory> instances = new ArrayList<>();
            AtomicInteger creationCounter = new AtomicInteger(0);

            // Act - Create multiple threads that call getInstance simultaneously
            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < numCallsPerThread; j++) {
                            RepositoryFactory instance = RepositoryFactory.getInstance();
                            synchronized (instances) {
                                instances.add(instance);
                            }
                            // Small pause to increase probability of race condition
                            Thread.sleep(1);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for all threads to finish
            latch.await();
            executor.shutdown();

            // Assert
            assertFalse(instances.isEmpty());
            
            // All instances must be the same (Singleton)
            RepositoryFactory firstInstance = instances.get(0);
            for (RepositoryFactory instance : instances) {
                assertSame(firstInstance, instance);
            }

            // Verify that only one instance was created
            assertEquals(1, instances.stream().distinct().count());
        }
    }

    @Nested
    @DisplayName("Tests for getting repositories")
    class GetRepositoriesTests {

        @Test
        @DisplayName("Should get audit repository")
        void shouldGetAuditRepository() {
            // Act
            AuditRepository auditRepository = RepositoryFactory.getInstance().getAuditRepository();

            // Assert
            assertNotNull(auditRepository);
            assertTrue(auditRepository instanceof AuditRepository);
        }

        @Test
        @DisplayName("Should get client repository")
        void shouldGetClientRepository() {
            // Act
            ClientRepository clientRepository = RepositoryFactory.getInstance().getClientRepository();

            // Assert
            assertNotNull(clientRepository);
            assertTrue(clientRepository instanceof ClientRepository);
        }

        @Test
        @DisplayName("Should get account repository")
        void shouldGetAccountRepository() {
            // Act
            AccountRepository accountRepository = RepositoryFactory.getInstance().getAccountRepository();

            // Assert
            assertNotNull(accountRepository);
            assertTrue(accountRepository instanceof AccountRepository);
        }

        @Test
        @DisplayName("Should get card repository")
        void shouldGetCardRepository() {
            // Act
            CardRepository cardRepository = RepositoryFactory.getInstance().getCardRepository();

            // Assert
            assertNotNull(cardRepository);
            assertTrue(cardRepository instanceof CardRepository);
        }

        @Test
        @DisplayName("Should get transaction repository")
        void shouldGetTransactionRepository() {
            // Act
            TransactionRepository transactionRepository = RepositoryFactory.getInstance().getTransactionRepository();

            // Assert
            assertNotNull(transactionRepository);
            assertTrue(transactionRepository instanceof TransactionRepository);
        }
    }
}
