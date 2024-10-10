package com.huiun.fizzybudget.expenseservice.service;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseFilter {
    private Long userId;
    private String categoryName;
    private String currencyCode;
}
