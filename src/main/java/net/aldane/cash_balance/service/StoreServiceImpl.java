package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.StoreMapper;
import net.aldane.cash_balance.repository.db.StoreDbRepository;
import net.aldane.cash_balance_api_server_java.model.Store;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    private final StoreDbRepository storeDbRepository;
    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreDbRepository storeDbRepository, StoreMapper storeMapper) {
        this.storeDbRepository = storeDbRepository;
        this.storeMapper = storeMapper;
    }

    @Override
    public List<Store> getStores(List<String> statesIds) {
        try {
            var storesList = storeDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return storeMapper.storeDbListToStoreList(storesList);
        } catch (Exception e) {
            System.out.println("Error obtaining stores");
            return new ArrayList<>();
        }
    }

    @Override
    public Store getStoreById(Long storeId) {
        try {
            var store = storeDbRepository.findById(storeId).orElse(null);
            return storeMapper.storeDbToStore(store);
        } catch (Exception e) {
            System.out.println("Error obtaining account entry with id: " + storeId);
            return null;
        }
    }

    @Override
    public Store createStore(Store store) {
        try {
            //store.setLastModification(OffsetDateTime.now());
            var storeSaved = storeDbRepository.save(storeMapper.storeToStoreDb(store));
            return storeMapper.storeDbToStore(storeSaved);
        } catch (Exception e) {
            System.out.println("Error creating store");
            return null;
        }
    }

    @Override
    public boolean deleteStore(Long id) {
        try {
            storeDbRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting store with id: " + id);
            return false;
        }
    }

    @Override
    public Store updateStore(Store store) {
        try {
            //store.setLastModification(OffsetDateTime.now());
            var storeDb = storeDbRepository.findById(store.getId()).orElse(null);
            storeDb.setName(store.getName());
            storeDb.setComment(store.getComment());
            return storeMapper.storeDbToStore(storeDbRepository.save(storeDb));
        } catch (Exception e) {
            System.out.println("Error updating store with id: " + store.getId());
            return null;
        }
    }
}
