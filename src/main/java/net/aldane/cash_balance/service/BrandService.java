package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> getBrands();

    Brand getBrandById(Long brandId);

    Brand createBrand(Brand brand);

    boolean deleteBrand(Long id);

    Brand updateBrand(Brand brand);

    List<Brand> getBrandsByUserId(Long userId);
}
