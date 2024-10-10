package com.huiun.fizzybudget.expenseservice.dto;

import com.huiun.fizzybudget.common.entity.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseEdge {
    private Expense node;
    private String cursor;
}
