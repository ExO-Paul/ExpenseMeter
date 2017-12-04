package com.exo.expensemeter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.exo.expensemeter.model.Currency
import com.exo.expensemeter.service.SmsDataService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val smsDataService = SmsDataService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        receiveButton.setOnClickListener {
            val address = addressText.text?.toString() ?: ""
            val transactions = smsDataService.parseSmsData(address)

            result.text = transactions
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
}
