package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.StoreMapper;
import net.aldane.cash_balance.repository.db.StoreDbRepository;
import net.aldane.cash_balance.repository.db.entity.StoreDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.Store;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private StatusUtils statusUtils;
    private final StoreDbRepository storeDbRepository;
    private final StoreMapper storeMapper;
    private final Logger log = LogManager.getLogger(this.getClass());

    public StoreServiceImpl(StoreDbRepository storeDbRepository, StoreMapper storeMapper) {
        this.storeDbRepository = storeDbRepository;
        this.storeMapper = storeMapper;
    }

    @Override
    public List<Store> getStores() {
        try {
            List<StoreDb> storeDbList;
            if (authUtils.isUserAdmin()) {
                storeDbList = storeDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            } else {
                storeDbList = storeDbRepository.findAllByStatusAndUserOrderByNameAsc(statusUtils.getActiveStatus(), authUtils.getUser());
            }
            return storeMapper.storeDbListToStoreList(storeDbList);
        } catch (Exception e) {
            log.error("Error obtaining stores");
            return new ArrayList<>();
        }
    }

    @Override
    public Store getStoreById(Long storeId) {
        try {
            var store = storeDbRepository.findById(storeId).orElse(null);
            if (store != null) {
                if (authUtils.isUserAdmin()) {
                    return storeMapper.storeDbToStore(store);
                } else {
                    if (store.getStatus().equals(statusUtils.getActiveStatus()) && store.getUser().getId().equals(authUtils.getUser().getId())) {
                        return storeMapper.storeDbToStore(store);
                    } else {
                        log.warn("Store with id {} is not active or does not belong to the user", storeId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining store with id: {}", storeId);
        }
        return null;
    }

    @Override
    public Store createStore(Store store) {
        try {
            if (store.getName() == null || store.getName().trim().isBlank()) {
                log.info("Store name can't be empty");
                return null;
            }
            StoreDb storeDb = new StoreDb();
            storeDb.setName(store.getName());

            if (store.getComment() != null && !store.getComment().trim().isBlank()) {
                storeDb.setComment(store.getComment());
            } else {
                storeDb.setComment("");
            }
            storeDb.setStatus(statusUtils.getActiveStatus());
            storeDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
            storeDb.setUser(authUtils.getUser());
            var brandSaved = storeDbRepository.save(storeDb);
            return storeMapper.storeDbToStore(brandSaved);
        } catch (Exception e) {
            log.error("Error creating store");
            return null;
        }
    }

    @Override
    public boolean deleteStore(Long id) {
        try {
            var storeDb = storeDbRepository.findById(id).orElse(null);
            if (storeDb != null && (authUtils.isUserAdmin() || storeDb.getUser().getId().equals(authUtils.getUser().getId()))) {
                storeDb.setStatus(statusUtils.getDeletedStatus());
                storeDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                storeDbRepository.save(storeDb);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting store with id: {}", id);
        }
        return false;
    }

    @Override
    public Store updateStore(Store store) {
        try {
            var storeDb = storeDbRepository.findById(store.getId()).orElse(null);
            if (storeDb != null) {
                if (!authUtils.isUserAdmin() && !storeDb.getUser().getId().equals(authUtils.getUser().getId())) {
                    log.warn("User does not have permission to update this brand");
                    return null;
                }
                storeDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                if (store.getName() != null && !store.getName().trim().isBlank()) {
                    storeDb.setName(store.getName());
                }
                if (store.getComment() != null && !store.getComment().trim().isBlank()) {
                    storeDb.setComment(store.getComment());
                }
                return storeMapper.storeDbToStore(storeDbRepository.save(storeDb));
            }
        } catch (Exception e) {
            log.error("Error updating store with id: {}", store.getId());
        }
        return null;
    }

    @Override
    public List<Store> getStoresByUserId(Long userId) {
        try {
            if (authUtils.isUserAdmin()) {
                return storeMapper.storeDbListToStoreList(storeDbRepository.findByUserIdOrderByNameAsc(userId));
            } else if (authUtils.getUser().getId().equals(userId)) {
                return storeMapper.storeDbListToStoreList(storeDbRepository.findAllByStatusAndUserOrderByNameAsc(statusUtils.getActiveStatus(), authUtils.getUser()));
            }
        } catch (Exception e) {
            log.error("Error obtaining stores for user with id: {}", userId);
        }
        return new ArrayList<>();
    }
}
