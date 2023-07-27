package com.example.bankchallenge.domain.util

sealed class SignUpResult {
    object Success : SignUpResult()
    data class Failure(val error: String?) : SignUpResult()
}
