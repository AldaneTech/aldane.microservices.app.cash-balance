package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.BrandMapper;
import net.aldane.cash_balance.repository.db.BrandDbRepository;
import net.aldane.cash_balance.repository.db.entity.BrandDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.Brand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private StatusUtils statusUtils;
    private final BrandDbRepository brandDbRepository;
    private final BrandMapper brandMapper;
    private final Logger log = LogManager.getLogger(this.getClass());

    public BrandServiceImpl(BrandDbRepository brandDbRepository, BrandMapper brandMapper) {
        this.brandDbRepository = brandDbRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<Brand> getBrands() {
        try {
            List<BrandDb> brandDbList;
            if (authUtils.isUserAdmin()) {
                brandDbList = brandDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            } else {
                brandDbList = brandDbRepository.findAllByStatusAndUserOrderByIdAsc(statusUtils.getActiveStatus(), authUtils.getUser());
            }
            return brandMapper.brandDbListToBrandList(brandDbList);
        } catch (Exception e) {
            log.error("Error obtaining brands");
            return new ArrayList<>();
        }
    }

    @Override
    public Brand getBrandById(Long brandId) {
        try {
            var brand = brandDbRepository.findById(brandId).orElse(null);
            if (brand != null) {
                if (authUtils.isUserAdmin()) {
                    return brandMapper.brandDbToBrand(brand);
                } else {
                    if (brand.getStatus().equals(statusUtils.getActiveStatus()) && brand.getUser().getId().equals(authUtils.getUser().getId())) {
                        return brandMapper.brandDbToBrand(brand);
                    } else {
                        log.warn("Brand with id {} is not active or does not belong to the user", brandId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining brand with id: {}", brandId);
        }
        return null;
    }

    @Override
    public Brand createBrand(Brand brand) {
        try {
            if (brand.getName() == null || brand.getName().trim().isBlank()) {
                log.info("Brand name can't be empty");
                return null;
            }
            BrandDb brandDb = new BrandDb();
            brandDb.setName(brand.getName());
            brandDb.setComment(brand.getComment());
            brandDb.setStatus(statusUtils.getActiveStatus());
            brandDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
            brandDb.setUser(authUtils.getUser());
            var brandSaved = brandDbRepository.save(brandDb);
            return brandMapper.brandDbToBrand(brandSaved);
        } catch (Exception e) {
            log.error("Error creating brand");
            return null;
        }
    }

    @Override
    public boolean deleteBrand(Long id) {
        try {
            var brandDb = brandDbRepository.findById(id).orElse(null);
            if (brandDb != null && (authUtils.isUserAdmin() || brandDb.getUser().getId().equals(authUtils.getUser().getId()))) {
                brandDb.setStatus(statusUtils.getDeletedStatus());
                brandDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                brandDbRepository.save(brandDb);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting brand with id: {}", id);
        }
        return false;
    }

    @Override
    public Brand updateBrand(Brand brand) {
        try {
            //brand.setLastModification(OffsetDateTime.now());
            var brandDb = brandDbRepository.findById(brand.getId()).orElse(null);
            brandDb.setName(brand.getName());
            brandDb.setComment(brand.getComment());
            return brandMapper.brandDbToBrand(brandDbRepository.save(brandDb));
        } catch (Exception e) {
            System.out.println("Error updating brand with id: " + brand.getId());
            return null;
        }
    }
}
