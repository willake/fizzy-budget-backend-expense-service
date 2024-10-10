package com.huiun.fizzybudget.expenseservice.service;

import com.huiun.fizzybudget.common.entity.Currency;

import java.util.List;

public interface CurrencyService {

    List<Currency> findAll();
}
