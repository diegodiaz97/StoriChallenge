package com.example.bankchallenge.presentation.signup.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ActivitySignUpBinding
import com.example.bankchallenge.presentation.login.view.LoginActivity
import com.example.bankchallenge.presentation.signup.viewmodel.SignUpViewModel
import com.example.bankchallenge.domain.entity.NewUserModel
import com.example.bankchallenge.presentation.util.PermissionUtil
import com.example.bankchallenge.presentation.util.hideSoftKeyboard
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.util.Calendar

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        signUpViewModel.signUpState.observe(this, ::onSignUpStateChanged)
    }

    private fun setupViews() {
        binding.nameEditText.addTextChangedListener {
            onInputFieldChanged()
        }
        binding.lastnameEditText.addTextChangedListener {
            onInputFieldChanged()
        }
        binding.emailEditText.addTextChangedListener {
            onInputFieldChanged()
        }
        binding.passwordEditText.addTextChangedListener {
            onInputFieldChanged()
        }
        binding.takePhotoButton.setOnClickListener {
            it.hideSoftKeyboard()
            if (PermissionUtil.isCameraPermissionGranted(this)) {
                startForCameraResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        binding.backImageView.setOnClickListener {
            it.hideSoftKeyboard()
            onBackPressed()
        }
    }

    private fun onSignUpStateChanged(data: SignUpViewModel.SignUpData) {
        when (data.state) {
            SignUpViewModel.SignUpState.CHECKING_FIELDS -> updateUi(data)
            SignUpViewModel.SignUpState.SHOW_LOADING -> showLoader()
            SignUpViewModel.SignUpState.SUCCESS -> goToSuccessSignUp()
            SignUpViewModel.SignUpState.SHOW_ERROR_DIALOG -> showError(data.errorMessage)
            else -> {}
        }
    }

    private fun goToSuccessSignUp() {
        hideLoader()
        startActivity(SuccessSignUpActivity.newIntent(this))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun showError(errorMessage: String? = "") {
        hideLoader()
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun onInputFieldChanged() {
        signUpViewModel.onFieldChanged(
            name = binding.nameEditText.text.toString(),
            lastname = binding.lastnameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
            password = binding.passwordEditText.text.toString()
        )
    }

    private fun updateUi(data: SignUpViewModel.SignUpData) {
        with(binding) {
            if (binding.passwordEditText.text.toString().isEmpty()) {
                passwordTextInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                passwordTextInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
            emailTextInputLayout.error =
                if (data.isValidEmail) null else getString(R.string.email_error_text)
            passwordTextInputLayout.error =
                if (data.isValidPassword) null else getString(R.string.password_error_text)

            takePhotoButton.isEnabled = data.shouldEnablePhoto
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startForCameraResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            showError("No se dieron permisos de cámara")
        }
    }

    private val startForCameraResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            showLoader()
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            signUpViewModel.onProfilePhotoCaptured(
                NewUserModel(
                    name = binding.nameEditText.text.toString(),
                    lastname = binding.lastnameEditText.text.toString(),
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString(),
                    imageUri = getImageUriFromBitmap(this, imageBitmap)
                )
            )
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_IMAGE_QUALITY, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            IMAGE_PATH_TITLE + Calendar.getInstance().time,
            null
        )
        return Uri.parse(path.toString())
    }

    override fun onBackPressed() {
        startActivity(LoginActivity.newIntent(this))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    private fun showLoader() {
        binding.loginLoader.visibility = View.VISIBLE
        binding.loaderBackgroundView.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loginLoader.visibility = View.GONE
        binding.loaderBackgroundView.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
        const val PROFILE_IMAGE_QUALITY = 100
        const val IMAGE_PATH_TITLE = "IMG_"
    }
}
