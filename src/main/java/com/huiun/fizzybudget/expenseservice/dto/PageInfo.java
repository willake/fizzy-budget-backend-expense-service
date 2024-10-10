package com.huiun.fizzybudget.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo {
    Boolean hasPreviousPage;
    Boolean hasNextPage;
    String startCursor;
    String endCursor;
}
