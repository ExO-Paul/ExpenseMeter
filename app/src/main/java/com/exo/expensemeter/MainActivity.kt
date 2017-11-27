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
            val messages = retrieveSMSInfo(address?.toString() ?: "")
                    .filter { !it.contains("3D-Secure") }
                    .filter { !it.contains("zachisle", true) }
                    .filter { !it.contains("bankomat", true) }
                    .filter { !it.contains("vozvrat", true) }
                    .filter { !it.contains("schet platezha", true) }
                    .filter { !it.contains("overdraft", true) }
                    .filter { !it.contains("Поздравляем", true) }
                    .map { parse(it) }
            messages.forEach { println(it) }


            result.text = messages
                    .filter { it.place.contains(wordsToSeek.text.toString(), true)}
                    .map { if(it.currency.equals("USD")) it.sum * usdRatio.text.toString().toDouble() else it.sum  }
                    .sumByDouble { it }
                    .toString()
        }

    }

    private fun retrieveSMSInfo(address: String): List<String> {
        //TODO: add permission request
//        val permissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_SMS)
//        ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.READ_SMS),
//                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        val contentResolver = this.contentResolver
        val cursor = contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.ADDRESS),
                Telephony.Sms.Inbox.ADDRESS + " = ?",
                arrayOf(address),
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
        )

        val smsBodies = mutableListOf<String>()

        val count = cursor.count
        if (cursor.moveToFirst()) {
            for (i in 0 until count) {
                smsBodies.add(cursor.getString(0))
                cursor.moveToNext()
            }
        }

        cursor.close()
        return smsBodies
    }

    private val dateFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss")

    private fun parse(sms: String): Transaction {
        println(sms.split(". "))

        val blocks = sms.split(". ")

        val sumParts = blocks[3].split(" ")
        val sign = if (sumParts[0].equals("Oplata", true)) (-1.0) else 1.0

        return Transaction(
                card = blocks[1],
                dateTime = dateFormat.parse(blocks[2]),
                sum = sign * sumParts[1].toDouble(),
                currency = sumParts[2],
                place = blocks[4]
        )
    }
}
