package com.example.bankchallenge.domain.util

sealed class LoginResult {
    object Success : LoginResult()
    data class Failure(val error: String?) : LoginResult()
}
