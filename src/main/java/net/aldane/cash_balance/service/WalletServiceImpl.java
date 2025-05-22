package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.UserMapper;
import net.aldane.cash_balance.mapper.WalletMapper;
import net.aldane.cash_balance.repository.db.UserDbRepository;
import net.aldane.cash_balance.repository.db.WalletDbRepository;
import net.aldane.cash_balance_api_server_java.model.Wallet;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletDbRepository walletDbRepository;
    private final UserDbRepository userDbRepository;
    private final UserMapper userMapper;
    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletDbRepository walletDbRepository, WalletMapper walletMapper, UserDbRepository userDbRepository, UserMapper userMapper) {
        this.walletDbRepository = walletDbRepository;
        this.walletMapper = walletMapper;
        this.userDbRepository = userDbRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<Wallet> getWallets(List<String> statesIds) {
        try {
            var walletsList = walletDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return walletMapper.walletDbListToWalletList(walletsList);
        } catch (Exception e) {
            System.out.println("Error obtaining wallets");
            return new ArrayList<>();
        }
    }

    @Override
    public List<Wallet> getWalletsByUserId(Long idUser) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                var usuario =userDbRepository.findByUsername(authentication.getName());
                return walletMapper.walletDbListToWalletList(walletDbRepository.findByUserId(usuario.getId()));
            }
            var walletsList = walletDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return walletMapper.walletDbListToWalletList(walletsList);
        } catch (Exception e) {
            System.out.println("Error obtaining wallets");
            return new ArrayList<>();
        }
    }
    @Override
    public Wallet getWalletById(Long walletId) {
        try {
            var wallet = walletDbRepository.findById(walletId).orElse(null);
            return walletMapper.walletDbToWallet(wallet);
        } catch (Exception e) {
            System.out.println("Error obtaining wallet with id: " + walletId);
            return null;
        }
    }

    @Override
    public Wallet createWallet(Wallet wallet) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                var usuario = userDbRepository.findByUsername(authentication.getName());
                //wallet.setUser(userMapper.userDbToUser(usuario));
            }
            //wallet.setLastModification(OffsetDateTime.now());
            wallet.setCreationDate(OffsetDateTime.now());
            wallet.setBudget(0.0F);
            var walletSaved = walletDbRepository.save(walletMapper.walletToWalletDb(wallet));
            return walletMapper.walletDbToWallet(walletSaved);
        } catch (Exception e) {
            System.out.println("Error creating wallet");
            return null;
        }
    }

    @Override
    public boolean deleteWallet(Long id) {
        try {
            walletDbRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting wallet with id: " + id);
            return false;
        }
    }

    @Override
    public Wallet updateWallet(Wallet wallet) {
        try {
            //wallet.setLastModification(OffsetDateTime.now());
            var walletDb = walletDbRepository.findById(wallet.getId()).orElse(null);
            walletDb.setName(wallet.getName());
            walletDb.setComment(wallet.getComment());
            return walletMapper.walletDbToWallet(walletDbRepository.save(walletDb));
        } catch (Exception e) {
            System.out.println("Error updating wallet with id: " + wallet.getId());
            return null;
        }
    }
}
