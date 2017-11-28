package com.exo.expensemeter

import java.time.LocalDateTime
import java.util.*

public class Transaction(val card: String,
                         val dateTime: String,
                         val sum: Double,
                         val currency: String,
                         val place: String) {
    override fun toString(): String {
        return "Transaction(card='$card', dateTime=$dateTime, sum=$sum, currency='$currency', place='$place')"
    }
}