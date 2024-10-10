package com.huiun.fizzybudget.expenseservice.service;

import com.huiun.fizzybudget.common.entity.Expense;
import com.huiun.fizzybudget.expenseservice.dto.ExpenseConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ExpenseService {

    ExpenseConnection findAll(Long afterId, Pageable pageable);

    ExpenseConnection findAllByFilters(ExpenseFilter filter, Long afterId, Pageable pageable);

    Expense addExpense(BigDecimal amount, String description, LocalDate date,
                       Long userId, Long categoryId, Long currencyId);

    Expense updateExpense(Long expenseId, BigDecimal amount, String description, LocalDate date,
                          Long userId, Long categoryId, Long currencyId);

    Boolean deleteExpense(Long expenseId);
}
