package net.aldane.cash_balance.controller;

import jakarta.validation.Valid;
import net.aldane.cash_balance.service.BrandService;
import net.aldane.cash_balance_api_server_java.api.BrandApi;
import net.aldane.cash_balance_api_server_java.model.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BrandController implements BrandApi {

    private final BrandService brandService;
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Override
    public ResponseEntity<List<Brand>> getBrands() {
        var brands = brandService.getBrands();
        return ResponseEntity.ok(brands);
    }

    @Override
    public ResponseEntity<Brand> getBrandById(Long brandId) {
        var brand = brandService.getBrandById(brandId);
        return brand != null ? ResponseEntity.ok(brand) : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<Brand>> getBrandsByUserId(Long userId) {
        var brands = brandService.getBrandsByUserId(userId);
        return ResponseEntity.ok(brands);
    }

    @Override
    public ResponseEntity<Brand> createBrand(@Valid Brand brand) {
        var newBrand = brandService.createBrand(brand);
        return newBrand != null ? ResponseEntity.ok(newBrand) : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteBrand(Long brandId) {
        var brand = brandService.deleteBrand(brandId);
        return brand ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }


    @Override
    public ResponseEntity<Brand> updateBrand(@Valid Brand brand) {
        var result = brandService.updateBrand(brand);
        return  result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }
}
