package net.aldane.cash_balance;

import jakarta.persistence.EntityManager;
import net.aldane.cash_balance.mapper.WalletMapper;
import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance.repository.db.entity.UserDb;
import net.aldane.cash_balance.repository.db.entity.WalletDb;
import net.aldane.cash_balance.service.WalletServiceImpl;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletDescriptionTest {
    @Test void clearsAnExplicitlyEmptyDescriptionButPreservesAnOmittedOne() {
        var repository = mock(WalletDbRepository.class);
        var mapper = mock(WalletMapper.class);
        var auth = mock(AuthUtils.class);
        var user = new UserDb(); user.setId(1L);
        var wallet = new WalletDb(); wallet.setId(7L); wallet.setUser(user); wallet.setComment("Original");
        when(auth.getUser()).thenReturn(user);
        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(wallet));
        when(repository.save(wallet)).thenReturn(wallet);
        when(mapper.walletDbToWallet(wallet)).thenReturn(new Wallet().id(7L));
        var service = new WalletServiceImpl(repository, mapper);
        ReflectionTestUtils.setField(service, "authUtils", auth);
        ReflectionTestUtils.setField(service, "entityManager", mock(EntityManager.class));
        assertNotNull(service.updateWallet(new Wallet().id(7L)));
        assertEquals("Original", wallet.getComment());
        assertNotNull(service.updateWallet(new Wallet().id(7L).comment("  ")));
        assertEquals("", wallet.getComment());
        verify(repository, times(2)).save(wallet);
    }
}
