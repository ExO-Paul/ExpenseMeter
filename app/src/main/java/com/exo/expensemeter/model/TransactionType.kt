package com.exo.expensemeter.model

public enum class TransactionType(val sign: Double) {
    EXPENSE(-1.0), INCOME(1.0), SKIP(0.0)
}