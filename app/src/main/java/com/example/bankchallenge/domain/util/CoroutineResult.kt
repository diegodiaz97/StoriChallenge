package com.example.bankchallenge.domain.util

sealed class CoroutineResult<out T : Any> {
    class Success<out T : Any>(val data: T) : CoroutineResult<T>()
    class Failure(val error: String?) : CoroutineResult<Nothing>()
}
