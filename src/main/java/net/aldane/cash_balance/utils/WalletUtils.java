package net.aldane.cash_balance.utils;

import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance.repository.db.entity.StatusDb;
import net.aldane.cash_balance.repository.db.entity.WalletDb;
import org.springframework.stereotype.Component;

@Component
public class WalletUtils {

    private final WalletDbRepository walletDbRepository;

    public WalletUtils(WalletDbRepository walletDbRepository) {
        this.walletDbRepository = walletDbRepository;
    }

    public WalletDb findWalletDb(Long walletId) {
        return walletDbRepository.findById(walletId).orElse(null);
    }

}
