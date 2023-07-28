package com.example.bankchallenge.data.mapper

import com.example.bankchallenge.domain.entity.AccountMovement
import com.example.bankchallenge.domain.entity.DisplayProfile
import com.example.bankchallenge.domain.entity.NewUserModel
import com.example.bankchallenge.domain.entity.UserBalance
import com.google.firebase.firestore.DocumentSnapshot

fun NewUserModel.transformToFirebaseUser() = hashMapOf(
    "name" to this.name,
    "lastname" to this.lastname,
    "email" to this.email,
    "profile_image" to this.imageUri.toString()
)

fun DocumentSnapshot.transformToDisplayUser() = DisplayProfile(
    this.get("name") as String,
    this.get("lastname") as String,
    this.get("email") as String,
    this.get("profile_image") as String,
)

fun DocumentSnapshot.transformToMovement() = AccountMovement(
    this.get("amount") as Long,
    this.get("author") as String,
    this.get("receiver") as String,
    this.get("author_email") as String,
    this.get("receiver_email") as String,
    this.get("description") as String,
    this.get("code") as Long,
    this.get("date") as String
)

fun DocumentSnapshot.transformToBalance() = UserBalance(
    this.get("amount") as Long,
    this.get("amount_limit") as Long,
    this.get("last_digits") as Long,
)
