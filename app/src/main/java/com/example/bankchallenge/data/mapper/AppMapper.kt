package com.example.bankchallenge.data.mapper

import com.example.bankchallenge.presentation.util.NewUserModel

fun NewUserModel.transformToFirebaseUser() = hashMapOf(
    "name" to this.name,
    "lastname" to this.lastname,
    "email" to this.lastname
)
