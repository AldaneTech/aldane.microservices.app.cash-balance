package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.StoreService;
import net.aldane.cash_balance_api_server_java.api.StoreApi;
import net.aldane.cash_balance_api_server_java.model.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StoreController implements StoreApi {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public ResponseEntity<Store> createStore(@Valid Store store) {
        var newStore = storeService.createStore(store);
        return newStore != null ? ResponseEntity.ok(newStore) : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteStore(Long storeId) {
        var store = storeService.deleteStore(storeId);
        return store ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Store> getStoreById(Long storeId) {
        var store = storeService.getStoreById(storeId);
        return store != null ? ResponseEntity.ok(store) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<Store>> getStores() {
        var states = storeService.getStores(new ArrayList<>());
        return ResponseEntity.ok(states);
    }

    @Override
    public ResponseEntity<List<Store>> getStoresByUserId(Long aLong) {
        return null;
    }

    @Override
    public ResponseEntity<Store> updateStore(@Valid Store store) {
        return storeService.updateStore(store) != null ? ResponseEntity.ok(store) : ResponseEntity.badRequest().build();
    }
}
