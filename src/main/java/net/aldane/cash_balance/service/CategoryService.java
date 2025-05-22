package net.aldane.cash_balance.service;


import net.aldane.cash_balance_api_server_java.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories(List<String> categoriesIds);

    Category getCategoryById(Long categoryId);

    Category createCategory(Category category);

    boolean deleteCategory(Long id);

    Category updateCategory(Category category);
}
