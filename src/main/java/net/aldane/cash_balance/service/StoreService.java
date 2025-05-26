package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Brand;
import net.aldane.cash_balance_api_server_java.model.Store;

import java.util.List;

public interface StoreService {
    List<Store> getStores();

    Store getStoreById(Long storeId);

    Store createStore(Store store);

    boolean deleteStore(Long id);

    Store updateStore(Store store);
    List<Store> getStoresByUserId(Long userId);


}
