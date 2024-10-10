package com.huiun.fizzybudget.expenseservice.integration.service;

import com.huiun.fizzybudget.common.entity.*;
import com.huiun.fizzybudget.common.repository.RoleRepository;
import com.huiun.fizzybudget.common.repository.UserRepository;
import com.huiun.fizzybudget.common.security.JWTTokenProvider;
import com.huiun.fizzybudget.expenseservice.dto.ExpenseConnection;
import com.huiun.fizzybudget.expenseservice.dto.ExpenseEdge;
import com.huiun.fizzybudget.expenseservice.repository.CategoryRepository;
import com.huiun.fizzybudget.expenseservice.repository.CurrencyRepository;
import com.huiun.fizzybudget.expenseservice.repository.ExpenseRepository;
import com.huiun.fizzybudget.expenseservice.service.ExpenseFilter;
import com.huiun.fizzybudget.expenseservice.service.ExpenseService;
import com.huiun.fizzybudget.expenseservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExpenseServiceImplTests {

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

    @MockBean
    private JWTTokenProvider tokenProvider; // Mocking the JWT provider to disable it

    @Autowired
    private ExpenseService expenseService;

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
        ExpenseConnection connection = expenseService.findAll(null, pageable);

        assertEquals(expenses.size(), connection.getEdges().size(), 0.01);

        for (int i = 0; i < expenses.size(); i++) {
            assertEquals(expenses.get(i).getExpenseAmount(), connection.getEdges().get(i).getNode().getExpenseAmount());
        }
    }

    @Test
    public void testFindAllGreaterThan() {
        int after = 2;
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        ExpenseConnection connection = expenseService.findAll((long) after, pageable);

        assertEquals(expenses.size() - after, connection.getEdges().size());

        for (int i = 0; i < connection.getEdges().size(); i++) {
            assertEquals(expenses.get(after + i).getExpenseAmount(), connection.getEdges().get(i).getNode().getExpenseAmount());
        }
    }

    @Test
    public void testFindAllByUserId() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        ExpenseConnection connection = expenseService.findAllByFilters(
                new ExpenseFilter(testUser.getId(), null, null),
                null, pageable);

        connection.getEdges().forEach(
                edge -> assertEquals(testUser.getId(), edge.getNode().getUser().getId())
        );
    }

    @Test
    public void testFindAllByCategoryName() {
        Category food = categories.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        ExpenseConnection connection = expenseService.findAllByFilters(
                new ExpenseFilter(null, food.getCategoryName(), null),
                null, pageable);

        connection.getEdges().forEach(
                edge -> assertEquals(food.getCategoryName(), edge.getNode().getCategory().getCategoryName())
        );
    }

    @Test
    public void testFindAllByCurrencyCode() {
        Currency usd = currencies.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        ExpenseConnection connection = expenseService.findAllByFilters(
                new ExpenseFilter(null, null, usd.getCurrencyCode()),
                null, pageable);

        connection.getEdges().forEach(
                edge -> assertEquals(usd.getCurrencyCode(), edge.getNode().getCurrency().getCurrencyCode())
        );
    }

    @Test
    public void testFindAllByUserIdAndCategoryId() {
        User user = testUser;
        Category food = categories.getFirst();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        ExpenseConnection connection = expenseService.findAllByFilters(
                new ExpenseFilter(user.getId(), food.getCategoryName(), null),
                null, pageable);

        connection.getEdges().stream().map(ExpenseEdge::getNode).forEach(
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
        ExpenseConnection connection = expenseService.findAllByFilters(
                new ExpenseFilter(user.getId(), null, usd.getCurrencyCode()),
                null, pageable);

        connection.getEdges().stream().map(ExpenseEdge::getNode).forEach(
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
        ExpenseConnection connection = expenseService.findAllByFilters(
                new ExpenseFilter(user.getId(), food.getCategoryName(), usd.getCurrencyCode()),
                null, pageable);

        connection.getEdges().stream().map(ExpenseEdge::getNode).forEach(
                expense -> {
                    assertEquals(user.getId(), expense.getUser().getId());
                    assertEquals(food.getId(), expense.getCategory().getId());
                    assertEquals(usd.getId(), expense.getCurrency().getId());
                }
        );
    }
}
