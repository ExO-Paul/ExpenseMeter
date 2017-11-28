package com.exo.expensemeter

public enum class TransactionType(val sign: Double) {
    EXPENSE(-1.0), INCOME(1.0)
}