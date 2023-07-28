package com.example.bankchallenge.presentation.dashboard.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ActivityMovementDetailBinding
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.AMOUNT
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.AUTHOR
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.AUTHOR_EMAIL
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.CODE
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.DATE
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.DESCRIPTION
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.RECEIVER
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity.Companion.RECEIVER_EMAIL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovementDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovementDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovementDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        val params = this.intent.extras
        if (params != null) {
            with(binding) {
                amountTextView.text =
                    getString(R.string.amount_text, params.getLong(AMOUNT))
                authorTextView.text =
                    getString(R.string.detail_name_text, params.getString(AUTHOR))
                receiverTextView.text =
                    getString(R.string.detail_name_text, params.getString(RECEIVER))
                authorEmailTextView.text =
                    getString(R.string.detail_email_text, params.getString(AUTHOR_EMAIL))
                receiverEmailTextView.text =
                    getString(R.string.detail_email_text, params.getString(RECEIVER_EMAIL))
                descriptionTextView.text =
                    getString(R.string.detail_description_text, params.getString(DESCRIPTION))
                codeTextView.text =
                    getString(R.string.detail_code_text, params.getLong(CODE))
                dateTextView.text =
                    getString(R.string.detail_date_text, params.getString(DATE))
            }
        }
        binding.closeImageView.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        startActivity(DashboardActivity.newIntent(this))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MovementDetailActivity::class.java)
    }
}
