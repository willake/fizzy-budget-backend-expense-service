package com.huiun.fizzybudget.expenseservice.integration.controller;

import com.huiun.fizzybudget.common.entity.*;
import com.huiun.fizzybudget.common.repository.RoleRepository;
import com.huiun.fizzybudget.common.repository.UserRepository;
import com.huiun.fizzybudget.expenseservice.repository.CategoryRepository;
import com.huiun.fizzybudget.expenseservice.repository.CurrencyRepository;
import com.huiun.fizzybudget.expenseservice.repository.ExpenseRepository;
import com.huiun.fizzybudget.expenseservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
//@AutoConfigureMockMvc(addFilters = false)
//@GraphQlTest(ExpenseController.class)
//@Import(GraphQlConfig.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ExpenseControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    private User testUser;
    private Role userRole;
    private Role managerRole;
    private List<Expense> expenses;
    private List<Category> categories;
    private List<Currency> currencies;

    @BeforeEach
    public void setUp() {
        // create default user role
        List<Role> roles = TestEntityFactory.createDefaultRoles();
        userRole = roleRepository.save(roles.get(0));
        managerRole = roleRepository.save(roles.get(1));

        testUser = TestEntityFactory.createDefaultUser(roles);
        testUser = userRepository.save(testUser);

        // set ids since unit test the entities will not have ids
        categories = TestEntityFactory.createDefaultCategories();
        categories.forEach(category -> categoryRepository.save(category));

        currencies = TestEntityFactory.createDefaultCurrencies();
        currencies.forEach(currency -> currencyRepository.save(currency));

        expenses = TestEntityFactory.createDefaultExpense(testUser, categories, currencies);
        expenses.forEach(expense -> expenseRepository.save(expense));
    }

    @Test
    public void testGetAllExpenses_ReturnExpenses() throws Exception {
        // prepare the GraphQL query
        // prepare the GraphQL query
        String graphqlQuery = """
        {
            "query": "query { getAllExpenses(first: 10) { edges { node { id expenseAmount expenseDescription date } cursor } pageInfo { hasPreviousPage hasNextPage } } }"
        }
    """;

        // perform the POST request to the GraphQL endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                        .content(graphqlQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Check that the status is 200 OK
                .andExpect(jsonPath("$.data.getAllExpenses.edges").isArray())  // Verify the response structure
                .andExpect(jsonPath("$.data.getAllExpenses.edges[0].node.id").value(expenses.get(0).getId()))  // Assert values
                .andExpect(jsonPath("$.data.getAllExpenses.pageInfo.hasNextPage").value(false));  // Example: assert page info
    }

}
