package Diaz.Dev.BFlow.wallet.entities;

import bflow.wallet.entities.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletEntityTest {

    @Test
    void testWalletCreation() {
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setName("My Wallet");
        wallet.setCurrency("USD");
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setInitialValue(new BigDecimal("100.00"));

        assertNotNull(wallet.getId());
        assertEquals("My Wallet", wallet.getName());
        assertEquals("USD", wallet.getCurrency());
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        assertEquals(new BigDecimal("100.00"), wallet.getInitialValue());
    }

    @Test
    void testWalletBuilder() {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet(
                walletId,
                "Savings",
                "EUR",
                new BigDecimal("500.00"),
                new BigDecimal("500.00"),
                null
        );

        assertEquals(walletId, wallet.getId());
        assertEquals("Savings", wallet.getName());
        assertEquals("EUR", wallet.getCurrency());
    }
}
