package com.exo.expensemeter

import java.time.LocalDateTime
import java.util.*

public class TransactionParseDefinition (
        val transactionGroup: String,
        val definingWord: String,
        val fieldDelimiter: String,
        val type: TransactionType,
        val projection: Map<TransactionField, Int>
)