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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@GraphQlTest(ExpenseController.class)
@Import(GraphQlConfig.class)
public class GraphQlTests {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
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
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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

    private ExpenseConnection getTestConnection() {

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

        when(expenseService.findAll(any(), any())).thenReturn(getTestConnection());

        // prepare the GraphQL query
        // language=GraphQL
        String graphqlQuery = """
            query { 
                getAllExpenses(first: 10) { 
                    edges { 
                        node { 
                            id 
                            expenseAmount 
                            expenseDescription
                            date 
                        } 
                        cursor 
                    } 
                    pageInfo { 
                        hasPreviousPage 
                        hasNextPage 
                    } 
                }
            }
        """;

        // perform the POST request to the GraphQL endpoint
        graphQlTester.document(graphqlQuery)
                .execute()
                .path("getAllExpenses")
                .entity(ExpenseConnection.class)
                .satisfies(connection -> {
                    assertEquals(connection.getEdges().size(), expenses.size());
                });
    }

}