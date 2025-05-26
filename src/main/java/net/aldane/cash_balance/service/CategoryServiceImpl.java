package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.CategoryMapper;
import net.aldane.cash_balance.repository.db.CategoryDbRepository;
import net.aldane.cash_balance.repository.db.entity.CategoryDb;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance.utils.StatusUtils;
import net.aldane.cash_balance_api_server_java.model.Category;
import net.aldane.cash_balance_api_server_java.model.Category;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private StatusUtils statusUtils;
    private final CategoryDbRepository categoryDbRepository;
    private final CategoryMapper categoryMapper;
    private final Logger log = LogManager.getLogger(this.getClass());

    public CategoryServiceImpl(CategoryDbRepository categoryDbRepository, CategoryMapper categoryMapper) {
        this.categoryDbRepository = categoryDbRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getCategories() {
        try {
            List<CategoryDb> categoryDbList;
            if (authUtils.isUserAdmin()) {
                categoryDbList = categoryDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            } else {
                categoryDbList = categoryDbRepository.findAllByStatusAndUserOrderByNameAsc(statusUtils.getActiveStatus(), authUtils.getUser());
            }
            return categoryMapper.categoryDbListToCategoryList(categoryDbList);
        } catch (Exception e) {
            log.error("Error obtaining categories");
            return new ArrayList<>();
        }
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        try {
            var category = categoryDbRepository.findById(categoryId).orElse(null);
            if (category != null) {
                if (authUtils.isUserAdmin()) {
                    return categoryMapper.categoryDbToCategory(category);
                } else {
                    if (category.getStatus().equals(statusUtils.getActiveStatus()) && category.getUser().getId().equals(authUtils.getUser().getId())) {
                        return categoryMapper.categoryDbToCategory(category);
                    } else {
                        log.warn("Category with id {} is not active or does not belong to the user", categoryId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error obtaining category with id: {}", categoryId);
        }
        return null;
    }

    @Override
    public Category createCategory(Category category) {
        try {
            if (category.getName() == null || category.getName().trim().isBlank()) {
                log.info("Category name can't be empty");
                return null;
            }
            CategoryDb categoryDb = new CategoryDb();
            categoryDb.setName(category.getName());

            if (category.getComment() != null && !category.getComment().trim().isBlank()) {
                categoryDb.setComment(category.getComment());
            } else {
                categoryDb.setComment("");
            }
            categoryDb.setStatus(statusUtils.getActiveStatus());
            categoryDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
            categoryDb.setUser(authUtils.getUser());
            var brandSaved = categoryDbRepository.save(categoryDb);
            return categoryMapper.categoryDbToCategory(brandSaved);
        } catch (Exception e) {
            log.error("Error creating category");
            return null;
        }
    }

    @Override
    public boolean deleteCategory(Long id) {
        try {
            var categoryDb = categoryDbRepository.findById(id).orElse(null);
            if (categoryDb != null && (authUtils.isUserAdmin() || categoryDb.getUser().getId().equals(authUtils.getUser().getId()))) {
                categoryDb.setStatus(statusUtils.getDeletedStatus());
                categoryDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                categoryDbRepository.save(categoryDb);
                return true;
            }
        } catch (Exception e) {
            log.error("Error deleting category with id: {}", id);
        }
        return false;
    }

    @Override
    public Category updateCategory(Category category) {
        try {
            var categoryDb = categoryDbRepository.findById(category.getId()).orElse(null);
            if (categoryDb != null) {
                if (!authUtils.isUserAdmin() && !categoryDb.getUser().getId().equals(authUtils.getUser().getId())) {
                    log.warn("User does not have permission to update this brand");
                    return null;
                }
                categoryDb.setLastModification(OffsetDateTime.now().toLocalDateTime());
                if (category.getName() != null && !category.getName().trim().isBlank()) {
                    categoryDb.setName(category.getName());
                }
                if (category.getComment() != null && !category.getComment().trim().isBlank()) {
                    categoryDb.setComment(category.getComment());
                }
                return categoryMapper.categoryDbToCategory(categoryDbRepository.save(categoryDb));
            }
        } catch (Exception e) {
            log.error("Error updating category with id: {}", category.getId());
        }
        return null;
    }

    @Override
    public List<Category> getCategoriesByUserId(Long userId) {
        try {
            if (authUtils.isUserAdmin()) {
                return categoryMapper.categoryDbListToCategoryList(categoryDbRepository.findByUserIdOrderByNameAsc(userId));
            } else if (authUtils.getUser().getId().equals(userId)) {
                return categoryMapper.categoryDbListToCategoryList(categoryDbRepository.findAllByStatusAndUserOrderByNameAsc(statusUtils.getActiveStatus(), authUtils.getUser()));
            }
        } catch (Exception e) {
            log.error("Error obtaining categories for user with id: {}", userId);
        }
        return new ArrayList<>();
    }
}
