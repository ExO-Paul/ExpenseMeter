package com.exo.expensemeter.service

import android.content.Context
import com.exo.expensemeter.model.TransactionField
import com.exo.expensemeter.model.TransactionType
import com.exo.expensemeter.ui.NotifierProvider

class TransactionParseDefinitionService(val context: Context) {
    val notifierProvider = NotifierProvider(context)

    val smsDataDefinitions: MutableSet<TransactionParseDefinition> =
            mutableSetOf<TransactionParseDefinition>(
                    TransactionParseDefinition(
                            "Payment",
                            "Oplata",
                            ". ",
                            TransactionType.EXPENSE,
                            mapOf(
                                    TransactionField.CARD to 1,
                                    TransactionField.SUM to 3,
                                    TransactionField.CURRENCY to 3,
                                    TransactionField.PLACE to 4
                            )
                    )
            )

    fun findAppropriateParseDefinitions(smsText: String): List<TransactionParseDefinition> {
        return smsDataDefinitions
                .filter { smsText.contains(it.definingWord) }
    }

    fun requestAdditionalParseDefinition(smsText: String): List<TransactionParseDefinition> {

        //TODO: replace with a UI call
        smsDataDefinitions.add(TransactionParseDefinition(smsDataDefinitions.size.toString(), smsText))
        return findAppropriateParseDefinitions(smsText);
    }

    fun requestDefinitionsElaboration(smsText: String, definitions: List<TransactionParseDefinition>): List<TransactionParseDefinition> {
        //TODO: Make a UI call to

        return findAppropriateParseDefinitions(smsText);
    }
}