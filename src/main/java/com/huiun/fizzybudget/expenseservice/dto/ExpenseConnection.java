package com.huiun.fizzybudget.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseConnection {
    List<ExpenseEdge> edges;
    PageInfo pageInfo;
}