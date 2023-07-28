package com.example.bankchallenge.domain.entity

import android.net.Uri

data class NewUserModel(
    var name: String,
    var lastname: String,
    var email: String,
    var password: String,
    var imageUri: Uri?
)
