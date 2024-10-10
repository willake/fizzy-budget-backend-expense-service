package com.huiun.fizzybudget.expenseservice.exception;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException() {
        super("Expense not found");
    }

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}