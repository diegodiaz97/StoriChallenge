package com.example.bankchallenge.presentation.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun View.hideSoftKeyboard() {
    if (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) is InputMethodManager) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
