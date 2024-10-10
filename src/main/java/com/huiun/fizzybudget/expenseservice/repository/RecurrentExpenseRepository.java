package com.huiun.fizzybudget.expenseservice.repository;

import com.huiun.fizzybudget.common.entity.RecurrentExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrentExpenseRepository extends JpaRepository<RecurrentExpense, Long> {
}
