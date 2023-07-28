package com.example.bankchallenge.presentation.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ActivityLoginBinding
import com.example.bankchallenge.presentation.dashboard.view.DashboardActivity
import com.example.bankchallenge.presentation.login.viewmodel.LoginViewModel
import com.example.bankchallenge.presentation.signup.view.SignUpActivity
import com.example.bankchallenge.presentation.util.hideSoftKeyboard
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        loginViewModel.loginState.observe(this, ::onLoginStateChanged)
    }

    private fun setupViews() {
        binding.emailEditText.addTextChangedListener {
            onInputFieldChanged()
        }
        binding.passwordEditText.addTextChangedListener {
            onInputFieldChanged()
        }
        binding.loginButton.setOnClickListener {
            it.hideSoftKeyboard()
            loginViewModel.onLoginButtonPressed(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString()
            )
        }
        binding.signUpTextView.setOnClickListener {
            it.hideSoftKeyboard()
            loginViewModel.onSignUpButtonPressed()
        }
    }

    private fun onLoginStateChanged(data: LoginViewModel.LoginData) {
        when (data.state) {
            LoginViewModel.LoginState.CHECKING_FIELDS -> updateUi(data)
            LoginViewModel.LoginState.SHOW_LOADING -> showLoader()
            LoginViewModel.LoginState.GO_TO_SIGN_UP -> goToSignUp()
            LoginViewModel.LoginState.DO_LOGIN -> goToDashboard()
            LoginViewModel.LoginState.SHOW_ERROR_DIALOG -> showError(data.errorMessage)
            else -> {}
        }
    }

    private fun goToSignUp() {
        startActivity(SignUpActivity.newIntent(this))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun goToDashboard() {
        hideLoader()
        startActivity(DashboardActivity.newIntent(this))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun showError(errorMessage: String? = "") {
        hideLoader()
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun onInputFieldChanged() {
        loginViewModel.onFieldChanged(
            email = binding.emailEditText.text.toString(),
            password = binding.passwordEditText.text.toString()
        )
    }

    private fun updateUi(data: LoginViewModel.LoginData) {
        with(binding) {
            if (binding.passwordEditText.text.toString().isEmpty()) {
                passwordTextInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                passwordTextInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
            emailTextInputLayout.error =
                if (data.isValidEmail) null else getString(R.string.email_error_text)
            passwordTextInputLayout.error =
                if (data.isValidPassword) null else getString(R.string.password_error_text)

            loginButton.isEnabled = data.shouldEnableLogin
        }
    }

    private fun showLoader() {
        binding.loginLoader.visibility = View.VISIBLE
        binding.loaderBackgroundView.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loginLoader.visibility = View.GONE
        binding.loaderBackgroundView.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
