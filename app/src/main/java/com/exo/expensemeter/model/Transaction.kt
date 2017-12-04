package com.exo.expensemeter.model


public class Transaction(val card: String,
                         val dateTime: String,
                         val sum: Double,
                         val currency: Currency,
                         val place: String) {

    constructor(card: String, dateTime: String, sum: String?, currency: String?, place: String) :
            this(card, dateTime, parseDouble(sum), parseCurrency(currency), place)

    override fun toString(): String {
        return "Transaction(card='$card', dateTime=$dateTime, sum=$sum, currency='$currency', place='$place')"
    }
}

private fun parseDouble(stringDouble: String?): Double {
    return stringDouble
            ?.replace("[^\\d\\s\\.]+".toRegex(), "")
            ?.replace(" ".toRegex(), "")
            ?.toDouble() ?: 0.0
}

private fun parseCurrency(stringCurrency: String?): Currency {
    return Currency.values()
            .map { it to stringCurrency?.lastIndexOf(it.name, ignoreCase = true) }
            .maxBy { it.second ?: -1 }
            ?.first
            ?: Currency.BYN
}