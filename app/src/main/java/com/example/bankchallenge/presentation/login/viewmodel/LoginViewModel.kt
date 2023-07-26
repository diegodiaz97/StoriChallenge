package com.example.bankchallenge.presentation.login.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankchallenge.domain.usecase.LoginUseCase
import com.example.bankchallenge.domain.util.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val loginUseCase: LoginUseCase) : ViewModel() {

    private val _loginState: MutableLiveData<LoginData> = MutableLiveData()
    val loginState: LiveData<LoginData>
        get() = _loginState

    fun onFieldChanged(email: String, password: String) {
        _loginState.value = LoginData(
            state = LoginState.CHECKING_FIELDS,
            isValidEmail = isValidEmail(email),
            isValidPassword = isValidPassword(password),
            shouldEnableLogin = shouldEnableLogin(email, password)
        )
    }

    fun onLoginButtonPressed(email: String, password: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _loginState.postValue(LoginData(state = LoginState.SHOW_LOADING))
            when (val result = loginUseCase(email, password)) {
                is LoginResult.Success -> {
                    _loginState.postValue(LoginData(state = LoginState.DO_LOGIN))
                }
                is LoginResult.Failure -> {
                    _loginState.postValue(
                        LoginData(
                            state = LoginState.SHOW_ERROR_DIALOG,
                            errorMessage = result.error
                        )
                    )
                }
            }
        }
    }

    fun onSignUpButtonPressed() {
        _loginState.value = LoginData(state = LoginState.GO_TO_SIGN_UP)
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidPassword(password: String) =
        password.length >= PASSWORD_MIN_LENGTH || password.isEmpty()

    private fun shouldEnableLogin(email: String, password: String) =
        isValidEmail(email) && isValidPassword(password) && email.isNotEmpty() && password.isNotEmpty()

    data class LoginData(
        val state: LoginState? = null,
        val isValidEmail: Boolean = true,
        val isValidPassword: Boolean = true,
        val shouldEnableLogin: Boolean = false,
        val errorMessage: String? = ""
    )

    enum class LoginState {
        CHECKING_FIELDS,
        SHOW_LOADING,
        GO_TO_SIGN_UP,
        DO_LOGIN,
        SHOW_ERROR_DIALOG
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}
