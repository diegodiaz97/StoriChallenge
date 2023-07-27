package com.example.bankchallenge.presentation.signup.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ActivitySuccessSignUpBinding
import com.example.bankchallenge.presentation.login.view.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.closeImageView.setOnClickListener {
            startActivity(LoginActivity.newIntent(this))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SuccessSignUpActivity::class.java)
    }
}
