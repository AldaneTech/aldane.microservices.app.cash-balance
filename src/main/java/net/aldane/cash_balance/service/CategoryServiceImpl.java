package net.aldane.cash_balance.service;

import net.aldane.cash_balance.mapper.CategoryMapper;
import net.aldane.cash_balance.repository.db.CategoryDbRepository;
import net.aldane.cash_balance_api_server_java.model.Category;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDbRepository categoryDbRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryDbRepository categoryDbRepository, CategoryMapper categoryMapper) {
        this.categoryDbRepository = categoryDbRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> getCategories(List<String> categoriesIds) {
        try {
            var categorysList = categoryDbRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
            return categoryMapper.categoryDbListToCategoryList(categorysList);
        } catch (Exception e) {
            System.out.println("Error obtaining categories");
            return new ArrayList<>();
        }
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        try {
            var category = categoryDbRepository.findById(categoryId).orElse(null);
            return categoryMapper.categoryDbToCategory(category);
        } catch (Exception e) {
            System.out.println("Error obtaining category with id: " + categoryId);
            return null;
        }
    }

    @Override
    public Category createCategory(Category category) {
        try {
            //category.setLastModification(OffsetDateTime.now());
            var categorySaved = categoryDbRepository.save(categoryMapper.categoryToCategoryDb(category));
            return categoryMapper.categoryDbToCategory(categorySaved);
        } catch (Exception e) {
            System.out.println("Error creating category");
            return null;
        }
    }

    @Override
    public boolean deleteCategory(Long id) {
        try {
            categoryDbRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting account entry with id: " + id);
            return false;
        }
    }

    @Override
    public Category updateCategory(Category category) {
        try {
            //category.setLastModification(OffsetDateTime.now());
            var categoryDb = categoryDbRepository.findById(category.getId()).orElse(null);
            categoryDb.setName(category.getName());
            categoryDb.setComment(category.getComment());
            return categoryMapper.categoryDbToCategory(categoryDbRepository.save(categoryDb));
        } catch (Exception e) {
            System.out.println("Error updating category with id: " + category.getId());
            return null;
        }
    }
}
