package com.example.bankchallenge.presentation.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtil {

    fun isCameraPermissionGranted(context: Activity) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkRequirePermission(context: Activity) {

    }
}