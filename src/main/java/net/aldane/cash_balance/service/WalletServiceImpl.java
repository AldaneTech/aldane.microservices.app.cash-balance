package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.WalletMapper;
import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance.repository.db.entity.StoreDb;
import net.aldane.cash_balance.repository.db.entity.WalletDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.Wallet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private StatusUtils statusUtils;
    private final WalletDbRepository walletDbRepository;
    private final WalletMapper walletMapper;
    private final Logger log = LogManager.getLogger(this.getClass());


    public WalletServiceImpl(WalletDbRepository walletDbRepository, WalletMapper walletMapper) {
        this.walletDbRepository = walletDbRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public List<Wallet> getWallets() {
        try {
            List<WalletDb> walletDbList;
            if (authUtils.isUserAdmin()) {
                walletDbList = walletDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            } else {
                walletDbList = walletDbRepository.findAllByStatusAndUserOrderByNameAsc(statusUtils.getActiveStatus(), authUtils.getUser());
            }
            return walletMapper.walletDbListToWalletList(walletDbList);
        } catch (Exception e) {
            log.error("Error obtaining wallets");
            return new ArrayList<>();
        }
    }

    @Override
    public Wallet getWalletById(Long walletId) {
        try {
            var wallet = walletDbRepository.findById(walletId).orElse(null);
            if (wallet != null) {
                if (authUtils.isUserAdmin()) {
                    return walletMapper.walletDbToWallet(wallet);
                } else {
                    if (wallet.getStatus().equals(statusUtils.getActiveStatus()) && wallet.getUser().getId().equals(authUtils.getUser().getId())) {
                        return walletMapper.walletDbToWallet(wallet);
                    } else {
                        log.warn("Wallet with id {} is not active or does not belong to the user", walletId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining wallet with id: {}", walletId);
        }
        return null;
    }

    @Override
    public List<Wallet> getWalletsByUserId(Long userId) {
        try {
            if (authUtils.isUserAdmin()) {
                return walletMapper.walletDbListToWalletList(walletDbRepository.findByUserIdOrderByNameAsc(userId));
            } else if (authUtils.getUser().getId().equals(userId)) {
                return walletMapper.walletDbListToWalletList(walletDbRepository.findAllByStatusAndUserOrderByNameAsc(statusUtils.getActiveStatus(), authUtils.getUser()));
            }
        } catch (Exception e) {
            log.error("Error obtaining wallets for user with id: {}", userId);
        }
        return new ArrayList<>();
    }

    @Override
    public Wallet createWallet(Wallet wallet) {
        try {
            if (wallet.getName() == null || wallet.getName().trim().isBlank()) {
                log.info("Wallet name can't be empty");
                return null;
            }
            WalletDb walletDb = new WalletDb();
            walletDb.setName(wallet.getName());
            walletDb.setBudget(0F);
            if (wallet.getComment() != null && !wallet.getComment().trim().isBlank()) {
                walletDb.setComment(wallet.getComment());
            } else {
                walletDb.setComment("");
            }
            walletDb.setCreation_date(OffsetDateTime.now().toLocalDateTime());
            walletDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
            walletDb.setStatus(statusUtils.getActiveStatus());
            walletDb.setUser(authUtils.getUser());
            var walletSaved = walletDbRepository.save(walletDb);
            return walletMapper.walletDbToWallet(walletSaved);
        } catch (Exception e) {
            log.error("Error creating wallet");
            return null;
        }
    }

    @Override
    public boolean deleteWallet(Long id) {
        try {
            var walletDb = walletDbRepository.findById(id).orElse(null);
            if (walletDb != null && (authUtils.isUserAdmin() || walletDb.getUser().getId().equals(authUtils.getUser().getId()))) {
                walletDb.setStatus(statusUtils.getDeletedStatus());
                walletDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                walletDbRepository.save(walletDb);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting wallet with id: {}", id);
        }
        return false;
    }

    @Override
    public Wallet updateWallet(Wallet wallet) {
        try {
            var walletDb = walletDbRepository.findById(wallet.getId()).orElse(null);
            if (walletDb != null) {
                if (!authUtils.isUserAdmin() && !walletDb.getUser().getId().equals(authUtils.getUser().getId())) {
                    log.warn("User does not have permission to update this brand");
                    return null;
                }
                walletDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                if (wallet.getName() != null && !wallet.getName().trim().isBlank()) {
                    walletDb.setName(wallet.getName());
                }
                if (wallet.getComment() != null && !wallet.getComment().trim().isBlank()) {
                    walletDb.setComment(wallet.getComment());
                }
                return walletMapper.walletDbToWallet(walletDbRepository.save(walletDb));
            }
        } catch (Exception e) {
            log.error("Error updating wallet with id: {}", wallet.getId());
        }
        return null;
    }
}
