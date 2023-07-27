package com.example.bankchallenge.presentation.signup.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankchallenge.domain.usecase.SignUpUseCase
import com.example.bankchallenge.domain.util.SignUpResult
import com.example.bankchallenge.presentation.util.NewUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val signUpUseCase: SignUpUseCase) : ViewModel() {

    private val _signUpState: MutableLiveData<SignUpData> = MutableLiveData()
    val signUpState: LiveData<SignUpData>
        get() = _signUpState

    fun onFieldChanged(name: String, lastname: String, email: String, password: String) {
        _signUpState.value = SignUpData(
            state = SignUpState.CHECKING_FIELDS,
            isValidEmail = isValidEmail(email),
            isValidPassword = isValidPassword(password),
            shouldEnablePhoto = shouldEnablePhoto(email, password) && isValidName(name, lastname)
        )
    }

    fun onProfilePhotoCaptured(newUserModel: NewUserModel) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _signUpState.postValue(SignUpData(state = SignUpState.SHOW_LOADING))
            when (val result = signUpUseCase(newUserModel)) {
                is SignUpResult.Success -> {
                    _signUpState.postValue(SignUpData(state = SignUpState.SUCCESS))
                }
                is SignUpResult.Failure -> {
                    _signUpState.postValue(
                        SignUpData(
                            state = SignUpState.SHOW_ERROR_DIALOG,
                            errorMessage = result.error
                        )
                    )
                }
            }
        }
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidPassword(password: String) =
        password.length >= PASSWORD_MIN_LENGTH || password.isEmpty()

    private fun isValidName(name: String, lastname: String) =
        name.isNotEmpty() && lastname.isNotEmpty()

    private fun shouldEnablePhoto(email: String, password: String) =
        isValidEmail(email) && isValidPassword(password) && email.isNotEmpty() && password.isNotEmpty()

    data class SignUpData(
        val state: SignUpState? = null,
        val isValidEmail: Boolean = true,
        val isValidPassword: Boolean = true,
        val shouldEnablePhoto: Boolean = false,
        val errorMessage: String? = ""
    )

    enum class SignUpState {
        CHECKING_FIELDS,
        SHOW_LOADING,
        SUCCESS,
        SHOW_ERROR_DIALOG
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}
