package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Store;

import java.util.List;

public interface StoreService {
    List<Store> getStores(List<String> storesIds);

    Store getStoreById(Long storeId);

    Store createStore(Store store);

    boolean deleteStore(Long id);

    Store updateStore(Store store);
}
