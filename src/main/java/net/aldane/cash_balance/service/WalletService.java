package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Wallet;

import java.util.List;

public interface WalletService {
    List<Wallet> getWallets();

    Wallet getWalletById(Long walletId);

    Wallet createWallet(Wallet wallet);

    boolean deleteWallet(Long id);

    Wallet updateWallet(Wallet wallet);

    List<Wallet> getWalletsByUserId(Long idUser);
}
