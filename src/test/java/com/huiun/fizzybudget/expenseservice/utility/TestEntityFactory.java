package com.huiun.fizzybudget.expenseservice.utility;

import com.huiun.fizzybudget.common.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestEntityFactory {

    public static User createDefaultUser(List<Role> roles) {
        User user = new User();
        user.setUsername("testUser");
        user.setPasswordHash("testUser");
        user.setEmail("testUser@gmail.com");
        user.setActivated(true);

        roles.forEach(role -> user.getRoles().add(role));

        return user;
    }

    // first one is user role and the second one is manager role
    public static List<Role> createDefaultRoles() {
        Role userRole = new Role();
        userRole.setRoleName("ROLE_USER");

        Role managerRole = new Role();
        managerRole.setRoleName("ROLE_MANAGER");

        return Arrays.asList(
                userRole, managerRole
        );
    }

    public static List<Expense> createDefaultExpense(User user, List<Category> categories, List<Currency> currencies) {
        Expense expense1 = new Expense();
        expense1.setUser(user);
        expense1.setCategory(categories.get(0));
        expense1.setCurrency(currencies.get(0));
        expense1.setDate(LocalDate.now());
        expense1.setExpenseAmount(new BigDecimal("11.00"));
        expense1.setExpenseDescription("Ingredients");

        Expense expense2 = new Expense();
        expense2.setUser(user);
        expense2.setCategory(categories.get(0));
        expense2.setCurrency(currencies.get(0));
        expense2.setDate(LocalDate.now());
        expense2.setExpenseAmount(new BigDecimal("11.00"));
        expense2.setExpenseDescription("Ingredients");

        Expense expense3 = new Expense();
        expense3.setUser(user);
        expense3.setCategory(categories.get(0));
        expense3.setCurrency(currencies.get(0));
        expense3.setDate(LocalDate.now());
        expense3.setExpenseAmount(new BigDecimal("11.00"));
        expense3.setExpenseDescription("Ingredients");

        Expense expense4 = new Expense();
        expense4.setUser(user);
        expense4.setCategory(categories.get(1));
        expense4.setCurrency(currencies.get(0));
        expense4.setDate(LocalDate.now());
        expense4.setExpenseAmount(new BigDecimal("11.00"));
        expense4.setExpenseDescription("Ingredients");


        return Arrays.asList(
                expense1, expense2, expense3, expense4);
    }

    public static List<Category> createDefaultCategories() {
        Category food = new Category();
        food.setCategoryName("Food");

        Category transport = new Category();
        transport.setCategoryName("Transport");
        return Arrays.asList(
                food, transport
        );
    }

    public static List<Currency> createDefaultCurrencies() {
        Currency usDollars = new Currency();
        usDollars.setCurrencyCode("USD");
        usDollars.setCurrencyName("US Dollars");
        usDollars.setCurrencySymbol("$");

        Currency euros = new Currency();
        euros.setCurrencyCode("EUR");
        euros.setCurrencyName("EUROS");
        euros.setCurrencySymbol("€");

        return Arrays.asList(
                usDollars, euros
        );
    }
}
