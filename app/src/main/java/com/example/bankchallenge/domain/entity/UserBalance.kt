package com.example.bankchallenge.domain.entity

data class UserBalance(
    val amount: Long? = 0,
    val amountLimit: Long? = 0,
    val lastDigits: Long? = 0
)
