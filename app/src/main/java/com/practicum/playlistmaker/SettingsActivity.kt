package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val buttonArrowBack = findViewById<MaterialToolbar>(R.id.toolbar)

        buttonArrowBack.setNavigationOnClickListener {
            finish()
        }

        val buttonShare = findViewById<MaterialTextView>(R.id.share)
        buttonShare.setOnClickListener {
            val message = getString(R.string.android_development_course)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            startActivity(shareIntent)
        }

        val buttonSupport = findViewById<MaterialTextView>(R.id.support)
        buttonSupport.setOnClickListener {
            val message = getString(R.string.text_support)
            val messageAddress = getString(R.string.message_Address)
            val messageSubject  = getString(R.string.message_Subject)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(messageAddress))
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, messageSubject)
            startActivity(supportIntent)
        }

        val buttonAgreement= findViewById<MaterialTextView>(R.id.agreement)
        buttonAgreement.setOnClickListener {
            val linkAgreement = getString(R.string.link_to_agreement)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkAgreement))
            startActivity(agreementIntent)
        }
    }
}