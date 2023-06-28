package com.example.mensajes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPhoneNumbers: EditText
    private lateinit var editTextMessage: EditText

    companion object {
        private const val PERMISSION_REQUEST_SMS = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPhoneNumbers = findViewById(R.id.editTextPhoneNumbers)
        editTextMessage = findViewById(R.id.editTextMessage)

        val buttonSendMessage: Button = findViewById(R.id.buttonSendMessage)
        buttonSendMessage.setOnClickListener {
            val phoneNumbers = editTextPhoneNumbers.text.toString()
            val message = editTextMessage.text.toString()

            if (phoneNumbers.isNotEmpty() && message.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    sendSMS(phoneNumbers, message)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.SEND_SMS),
                        PERMISSION_REQUEST_SMS
                    )
                }
            } else {
                Toast.makeText(
                    this,
                    "Ingresa al menos un número de teléfono y un mensaje",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendSMS(phoneNumbers: String, message: String) {
        val smsManager: SmsManager = SmsManager.getDefault()
        val numbersArray = phoneNumbers.split(",").map { it.trim() }

        val sendResults = mutableListOf<String>()

        for (phoneNumber in numbersArray) {
            try {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                sendResults.add("$phoneNumber: Enviado")
            } catch (e: Exception) {
                sendResults.add("$phoneNumber: Error")
            }
        }

        val resultMessage = sendResults.joinToString("\n")
        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_SMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumbers = editTextPhoneNumbers.text.toString()
                val message = editTextMessage.text.toString()
                sendSMS(phoneNumbers, message)
            } else {
                Toast.makeText(
                    this,
                    "Permiso denegado para enviar mensajes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
