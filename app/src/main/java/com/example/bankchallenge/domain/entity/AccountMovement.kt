package com.example.bankchallenge.domain.entity

data class AccountMovement(
    val amount: Long? = 0,
    val author: String? = "",
    val receiver: String? = "",
    val description: String? = "",
    val code: Long? = 0,
    val date: String? = "",
)
