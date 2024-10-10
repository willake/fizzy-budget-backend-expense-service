package com.huiun.fizzybudget.expenseservice.repository;

import com.huiun.fizzybudget.common.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e " +
            "WHERE e.id > :afterId")
    Page<Expense> findAllByIdGreaterThan(@Param("afterId")Long afterId, Pageable pageable);

    @Query("SELECT e FROM Expense e " +
            "WHERE (:userId IS NULL OR e.user.id = :userId) " +
            "AND (:categoryId IS NULL OR e.category.id = :categoryId) " +
            "AND (:currencyId IS NULL OR e.currency.id = :currencyId) " +
            "AND (:afterId IS NULL OR e.id > :afterId)")
    Page<Expense> findAllByFilters(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("currencyId") Long currencyId,
            @Param("afterId") Long afterId,
            Pageable pageable);
}
