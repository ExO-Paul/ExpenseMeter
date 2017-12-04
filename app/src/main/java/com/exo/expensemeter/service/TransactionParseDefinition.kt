package com.exo.expensemeter.service

import com.exo.expensemeter.model.TransactionField
import com.exo.expensemeter.model.TransactionType

public class TransactionParseDefinition (
        val transactionGroup: String,
        val definingWord: String,
        val fieldDelimiter: String,
        val type: TransactionType,
        val projection: Map<TransactionField, Int>
) {
    constructor(transactionGroup: String, definingWord: String) : this(
            transactionGroup,
            definingWord,
            "",
            TransactionType.SKIP,
            mapOf()
    )
}