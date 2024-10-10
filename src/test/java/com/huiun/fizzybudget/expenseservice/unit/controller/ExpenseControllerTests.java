package com.huiun.fizzybudget.expenseservice.unit.controller;

import com.huiun.fizzybudget.common.entity.*;
import com.huiun.fizzybudget.expenseservice.config.GraphQlConfig;
import com.huiun.fizzybudget.expenseservice.controller.ExpenseController;
import com.huiun.fizzybudget.expenseservice.dto.ExpenseConnection;
import com.huiun.fizzybudget.expenseservice.dto.ExpenseEdge;
import com.huiun.fizzybudget.expenseservice.dto.PageInfo;
import com.huiun.fizzybudget.expenseservice.service.CategoryService;
import com.huiun.fizzybudget.expenseservice.service.CurrencyService;
import com.huiun.fizzybudget.expenseservice.service.ExpenseService;
import com.huiun.fizzybudget.expenseservice.utility.PaginationUtil;
import com.huiun.fizzybudget.expenseservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseControllerTests {

    private ExpenseController expenseController;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CurrencyService currencyService;

    private User testUser;
    private Role userRole;
    private Role managerRole;
    private List<Expense> expenses;
    private List<Category> categories;
    private List<Currency> currencies;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        expenseController = new ExpenseController(expenseService, categoryService, currencyService);

        // create default user role
        List<Role> roles = TestEntityFactory.createDefaultRoles();
        userRole = roles.get(0);
        userRole.setId(0L);
        managerRole = roles.get(1);
        managerRole.setId(0L);

        testUser = TestEntityFactory.createDefaultUser(roles);
        testUser.setId(0L);

        // set ids since unit test the entities will not have ids
        categories = TestEntityFactory.createDefaultCategories();
        for(int i = 0; i < categories.size(); i++) { categories.get(i).setId((long) i); }

        currencies = TestEntityFactory.createDefaultCurrencies();
        for(int i = 0; i < currencies.size(); i++) { currencies.get(i).setId((long) i); }

        expenses = TestEntityFactory.createDefaultExpense(testUser, categories, currencies);
        for(int i = 0; i < expenses.size(); i++) { expenses.get(i).setId((long) i); }
    }

    private ExpenseConnection getMockConnection() {

        List<ExpenseEdge> edges = expenses.stream()
                .map(expense -> new ExpenseEdge(expense, PaginationUtil.encodeCursor(expense.getId())))
                .toList();

        // Build the PageInfo object to check if there are more pages
        PageInfo pageInfo = new PageInfo();
        pageInfo.setHasPreviousPage(false);
        pageInfo.setHasNextPage(false);

        return new ExpenseConnection(edges, pageInfo);
    }

    @Test
    public void testGetAllExpenses_ReturnExpenses() throws Exception {

        when(expenseService.findAll(any(), any())).thenReturn(getMockConnection());

        ExpenseConnection response = expenseController.getAllExpenses(10, null);

        assertEquals(expenses.size(), response.getEdges().size());
        assertEquals(expenses.get(0).getId(), response.getEdges().get(0).getNode().getId());
    }

    @Test
    public void testGetAllExpensesByFilters_ReturnExpenses() throws Exception {

        when(expenseService.findAllByFilters(any(), any(), any())).thenReturn(getMockConnection());

        ExpenseConnection response = expenseController.getAllExpensesByFilters(
                testUser.getId(), null, null,
                10,null);

        assertEquals(expenses.size(), response.getEdges().size());
        assertEquals(expenses.get(0).getId(), response.getEdges().get(0).getNode().getId());
    }

    @Test
    public void testGetAllCategories_ReturnCategories() throws Exception {

        when(categoryService.findAll()).thenReturn(categories);

        List<Category> response = expenseController.getAllCategories();

        assertEquals(categories.size(), response.size());
        for(int i = 0; i < categories.size(); i++) {
            assertEquals(categories.get(i).getCategoryName(), response.get(i).getCategoryName());
        }
    }

    @Test
    public void testGetAllCurrencies_ReturnCurrencies() throws Exception {

        when(currencyService.findAll()).thenReturn(currencies);

        List<Currency> response = expenseController.getAllCurrencies();

        assertEquals(currencies.size(), response.size());
        for(int i = 0; i < currencies.size(); i++) {
            assertEquals(currencies.get(i).getCurrencyCode(), response.get(i).getCurrencyCode());
        }
    }

}
