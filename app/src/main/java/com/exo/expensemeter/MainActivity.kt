package com.exo.expensemeter

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.Manifest.permission
import android.Manifest.permission.WRITE_CALENDAR
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        receiveButton.setOnClickListener {
            val address = addressText.text

            val parseDefinition = TransactionParseDefinition(
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

            val messages = retrieveSMSInfo(address?.toString() ?: "")
                    .filter { !it.body.contains("3D-Secure") }
                    .filter { !it.body.contains("zachisle", true) }
                    .filter { !it.body.contains("bankomat", true) }
                    .filter { !it.body.contains("vozvrat", true) }
                    .filter { !it.body.contains("schet platezha", true) }
                    .filter { !it.body.contains("overdraft", true) }
                    .filter { !it.body.contains("Поздравляем", true) }
                    .map { parse(it, parseDefinition) }
            messages.forEach { println(it) }


            result.text = messages
                    .filter { it.place.contains(wordsToSeek.text.toString(), true) }
                    .map {
                        when (it.currency) {
                            Currency.USD -> it.sum * usdRatio.text.toString().toDouble()
                            Currency.RUB -> it.sum * rubRatio.text.toString().toDouble()
                            else -> it.sum
                        }
                    }
                    .sumByDouble { it }
                    .toString()
        }

    }

    private fun retrieveSMSInfo(address: String): List<SmsInfo> {
        //TODO: add permission request
//        val permissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_SMS)
//        ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.READ_SMS),
//                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        val contentResolver = this.contentResolver
        val cursor = contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE),
                Telephony.Sms.Inbox.ADDRESS + " = ?",
                arrayOf(address),
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )

        val smsInfo = mutableListOf<SmsInfo>()

        val count = cursor.count
        if (cursor.moveToFirst()) {
            for (i in 0 until count) {
                smsInfo.add(SmsInfo(cursor.getString(0), cursor.getString(1)))
                cursor.moveToNext()
            }
        }

        cursor.close()
        return smsInfo
    }

    data class SmsInfo(val body: String, val date: String)

    private fun parse(sms: SmsInfo, parseDefinition: TransactionParseDefinition): Transaction {
        val blocks = sms.body.split(parseDefinition.fieldDelimiter)

        val projectionMap = parseDefinition.projection
                .map { (transactionField, index) -> transactionField to blocks[index] }
                .toMap()


//        val sumParts = blocks[3].split(" ")

        return Transaction(
                card = projectionMap[TransactionField.CARD] ?: "",
                dateTime = sms.date,
                sum = parseDouble(projectionMap[TransactionField.SUM]),
                currency = parseCurrency(projectionMap[TransactionField.CURRENCY]),
                place = projectionMap[TransactionField.PLACE] ?: ""
        )
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
}
