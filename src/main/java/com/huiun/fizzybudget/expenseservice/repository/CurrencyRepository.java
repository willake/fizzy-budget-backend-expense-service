package com.huiun.fizzybudget.expenseservice.repository;

import com.huiun.fizzybudget.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCurrencyCode(String currencyCode);

    Optional<Currency> findByCurrencyName(String currencyName);
}
