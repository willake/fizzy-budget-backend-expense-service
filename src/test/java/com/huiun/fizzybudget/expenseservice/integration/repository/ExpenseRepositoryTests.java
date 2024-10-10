package com.huiun.fizzybudget.expenseservice.integration.repository;

import com.huiun.fizzybudget.common.entity.*;
import com.huiun.fizzybudget.common.repository.RoleRepository;
import com.huiun.fizzybudget.common.repository.UserRepository;
import com.huiun.fizzybudget.expenseservice.repository.CategoryRepository;
import com.huiun.fizzybudget.expenseservice.repository.CurrencyRepository;
import com.huiun.fizzybudget.expenseservice.repository.ExpenseRepository;
import com.huiun.fizzybudget.expenseservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaRepositories(basePackages = {
        "com.huiun.fizzybudget.expenseservice.repository",
        "com.huiun.fizzybudget.common.repository"
})
@EntityScan(basePackages = "com.huiun.fizzybudget.common.entity")
public class ExpenseRepositoryTests {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseRepositoryTests.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    private User testUser;

    private List<Expense> expenses;

    private List<Category> categories;

    private List<Currency> currencies;

    @BeforeEach
    public void setUp() {
        List<Role> roles = TestEntityFactory.createDefaultRoles();
        roles.forEach(role -> roleRepository.save(role));

        testUser = TestEntityFactory.createDefaultUser(roles);
        userRepository.save(testUser);

        categories = TestEntityFactory.createDefaultCategories();
        categories.forEach(category -> categoryRepository.save(category));

        currencies = TestEntityFactory.createDefaultCurrencies();
        currencies.forEach(currency -> currencyRepository.save(currency));

        expenses = TestEntityFactory.createDefaultExpense(testUser, categories, currencies);
        expenses.forEach(expense -> expenseRepository.save(expense));
    }

    @Test
    public void testFindAll() {
        Pageable pageable = PageRequest.of(0, expenses.size(), Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAll(pageable);

        assertEquals(expenses.size(), retrivedPage.getSize());

        for (int i = 0; i < expenses.size(); i++) {
            assertEquals(expenses.get(i).getExpenseAmount(), retrivedPage.getContent().get(i).getExpenseAmount());
        }
    }

    @Test
    public void testFindAllGreaterThan() {
        int after = 2;
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByIdGreaterThan((long) after, pageable);

        assertEquals(expenses.size() - after, retrivedPage.getSize());

        for (int i = 0; i < retrivedPage.getSize(); i++) {
            assertEquals(expenses.get(after + i).getExpenseAmount(), retrivedPage.getContent().get(i).getExpenseAmount());
        }
    }

    @Test
    public void testFindAllByUserId() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByFilters(
                testUser.getId(), null, null,
                null, pageable);

        retrivedPage.getContent().forEach(
            expense -> assertEquals(testUser.getId(), expense.getUser().getId())
        );
    }

    @Test
    public void testFindAllByCategoryId() {
        Category food = categories.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByFilters(
                null, food.getId(), null,
                null, pageable);

        retrivedPage.getContent().forEach(
                expense -> assertEquals(food.getId(), expense.getCategory().getId())
        );
    }

    @Test
    public void testFindAllByCurrencyId() {
        Currency usd = currencies.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByFilters(
                null, usd.getId(), null,
                null, pageable);

        retrivedPage.getContent().forEach(
                expense -> assertEquals(usd.getId(), expense.getCurrency().getId())
        );
    }

    @Test
    public void testFindAllByUserIdAndCategoryId() {
        User user = testUser;
        Category food = categories.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByFilters(
                user.getId(), food.getId(), null,
                null, pageable);

        retrivedPage.getContent().forEach(
                expense -> {
                    assertEquals(user.getId(), expense.getUser().getId());
                    assertEquals(food.getId(), expense.getCategory().getId());
                }
        );
    }

    @Test
    public void testFindAllByUserIdAndCurrencyId() {
        User user = testUser;
        Currency usd = currencies.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByFilters(
                user.getId(), null, usd.getId(),
                null, pageable);

        retrivedPage.getContent().forEach(
                expense -> {
                    assertEquals(user.getId(), expense.getUser().getId());
                    assertEquals(usd.getId(), expense.getCurrency().getId());
                }
        );
    }

    @Test
    public void testFindAllByFilters() {
        User user = testUser;
        Currency usd = currencies.getFirst();
        Category food = categories.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Expense> retrivedPage = expenseRepository.findAllByFilters(
                user.getId(), food.getId(), usd.getId(),
                null, pageable);

        retrivedPage.getContent().forEach(
                expense -> {
                    assertEquals(user.getId(), expense.getUser().getId());
                    assertEquals(food.getId(), expense.getCategory().getId());
                    assertEquals(usd.getId(), expense.getCurrency().getId());
                }
        );
    }
}
