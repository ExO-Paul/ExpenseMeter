package com.exo.expensemeter.dao

import android.content.Context
import android.provider.Telephony

public class SmsDAO(val context: Context) {

    data class SmsInfo(val body: String, val date: String)

    public fun retrieveSMSInfo(address: String): List<SmsInfo> {
        //TODO: add permission request
//        val permissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_SMS)
//        ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.READ_SMS),
//                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        val contentResolver = context.contentResolver
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
}