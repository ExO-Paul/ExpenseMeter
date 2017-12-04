package com.exo.expensemeter.service

import com.exo.expensemeter.dao.SmsDAO
import com.exo.expensemeter.model.Transaction
import com.exo.expensemeter.model.TransactionField
import com.exo.expensemeter.model.TransactionType


public class SmsDataParser(val parseDefinitionService: TransactionParseDefinitionService) {

    fun parse(smsInfo: List<SmsDAO.SmsInfo>): List<Transaction> {
        val transactions = smsInfo
                .map { it to parseDefinitionService.findAppropriateParseDefinitions(it.body) }
                .map { (sms, definitions) -> parse(sms, definitions) }
                .filterNotNull()
        return transactions;
    }


    private fun parse(sms: SmsDAO.SmsInfo, definitions: List<TransactionParseDefinition>): Transaction? {
        if (definitions.size == 1) {
            return parse(sms, definitions.single())
        } else {
            if (definitions.isEmpty()) {
                val updatedDefinitions = parseDefinitionService.requestAdditionalParseDefinition(sms.body);
                return parse(sms, updatedDefinitions);
            } else {
                val updatedDefinitions = parseDefinitionService.requestDefinitionsElaboration(sms.body, definitions);
                return parse(sms, updatedDefinitions);
            }
        }
    }

    private fun parse(sms: SmsDAO.SmsInfo, parseDefinition: TransactionParseDefinition): Transaction? {
        if (parseDefinition.type != TransactionType.SKIP) {
            val blocks = sms.body.split(parseDefinition.fieldDelimiter)

            val projectionMap = parseDefinition.projection
                    .map { (transactionField, index) -> transactionField to blocks[index] }
                    .toMap()

            return Transaction(
                    card = projectionMap[TransactionField.CARD] ?: "",
                    dateTime = sms.date,
                    sum = projectionMap[TransactionField.SUM],
                    currency = projectionMap[TransactionField.CURRENCY],
                    place = projectionMap[TransactionField.PLACE] ?: ""
            )
        } else {
            return null
        }
    }
}
