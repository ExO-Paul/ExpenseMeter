package com.exo.expensemeter

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.Manifest.permission
import android.Manifest.permission.WRITE_CALENDAR
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(retrieveSMSInfo("Priorbank"))
    }

    private fun retrieveSMSInfo(address: String): List<String> {

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
        } else {
            throw RuntimeException("You have no SMS in Inbox")
        }
        cursor.close()
        return smsBodies
    }
}
