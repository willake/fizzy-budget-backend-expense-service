package com.huiun.fizzybudget.expenseservice.service;

import com.huiun.fizzybudget.common.entity.Category;
import com.huiun.fizzybudget.expenseservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
