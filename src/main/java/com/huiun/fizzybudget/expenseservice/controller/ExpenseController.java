package com.huiun.fizzybudget.expenseservice.controller;

import com.huiun.fizzybudget.common.entity.Category;
import com.huiun.fizzybudget.common.entity.Currency;
import com.huiun.fizzybudget.common.entity.Expense;
import com.huiun.fizzybudget.expenseservice.dto.ExpenseConnection;
import com.huiun.fizzybudget.expenseservice.service.CategoryService;
import com.huiun.fizzybudget.expenseservice.service.CurrencyService;
import com.huiun.fizzybudget.expenseservice.service.ExpenseFilter;
import com.huiun.fizzybudget.expenseservice.service.ExpenseService;
import com.huiun.fizzybudget.expenseservice.utility.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrencyService currencyService;

    public ExpenseController(ExpenseService expenseService, CategoryService categoryService, CurrencyService currencyService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.currencyService = currencyService;
    }

    @QueryMapping
    public ExpenseConnection getAllExpenses(@Argument Integer first, @Argument String after) {
        Pageable pageable;

        // Handle forward pagination (first and after)

        if (first == null) throw new IllegalArgumentException("Invalid pagination arguments: 'first' is require");

        if (after != null) {
            Long decodedCursor = PaginationUtil.decodeCursor(after);
            pageable = PageRequest.of(0, first, Sort.by("id").ascending());
            return expenseService.findAll(decodedCursor, pageable);
        }
        else {
            pageable = PageRequest.of(0, first, Sort.by("id").ascending());
            return expenseService.findAll(null, pageable);
        }
    }

    @QueryMapping
    public ExpenseConnection getAllExpensesByFilters(
            @Argument Long userId, @Argument String categoryName, @Argument String currencyCode,
            @Argument Integer first, @Argument String after) {

        Pageable pageable;

        // Handle forward pagination (first and after)

        if (first == null) throw new IllegalArgumentException("Invalid pagination arguments: 'first' is require");

        if (after != null) {
            Long decodedCursor = PaginationUtil.decodeCursor(after);
            pageable = PageRequest.of(0, first, Sort.by("id").ascending());
            return expenseService.findAllByFilters(new ExpenseFilter(userId, categoryName, currencyCode), decodedCursor, pageable);
        }
        else {
            pageable = PageRequest.of(0, first, Sort.by("id").ascending());
            return expenseService.findAllByFilters(new ExpenseFilter(userId, categoryName, currencyCode), null, pageable);
        }
    }

    @QueryMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public List<Currency> getAllCurrencies() {
        return currencyService.findAll();
    }

    @QueryMapping
    public Expense addExpense(
            @Argument BigDecimal amount, @Argument String description, @Argument LocalDate date,
            @Argument Long userId, @Argument Long categoryId, @Argument Long currencyId) {
        return expenseService.addExpense(amount, description, date, userId, categoryId, currencyId);
    }

    @QueryMapping
    public Expense updateExpense(
            @Argument Long expenseId,
            @Argument BigDecimal amount, @Argument String description, @Argument LocalDate date,
            @Argument Long userId, @Argument Long categoryId, @Argument Long currencyId) {
        return expenseService.updateExpense(expenseId, amount, description, date, userId, categoryId, currencyId);
    }

    @QueryMapping
    public Boolean deleteExpense(@Argument Long expenseId) {
        return expenseService.deleteExpense(expenseId);
    }
}
