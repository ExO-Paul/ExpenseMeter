package com.exo.expensemeter.service

import android.content.Context
import android.util.Log
import com.exo.expensemeter.dao.SmsDAO
import com.exo.expensemeter.model.Transaction

class SmsDataService(val context: Context) {

    // TODO: looks like it's THE TIME to look for DI
    val smsDao = SmsDAO(context)
    val parseDefiitionService = TransactionParseDefinitionService(context)

    //TODO: that's exactly not the place where this should happen
    val smsDataParser = SmsDataParser(parseDefiitionService)

    fun parseSmsData(address: String): List<Transaction> {
        val smsInfo = smsDao.retrieveSMSInfo(address)
        val transactions = smsDataParser.parse(smsInfo)

        //TODO: remove
        transactions.forEach { Log.d("SmsDataService", it.toString()) }

        return transactions
    }

}