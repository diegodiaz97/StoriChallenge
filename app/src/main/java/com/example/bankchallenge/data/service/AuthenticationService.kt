package com.example.bankchallenge.data.service

import android.net.Uri
import com.example.bankchallenge.domain.util.LoginResult
import com.example.bankchallenge.domain.util.SignUpResult
import com.example.bankchallenge.presentation.util.NewUserModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationService @Inject constructor(private val firebase: FirebaseClient) {

    suspend fun signUp(newUserModel: NewUserModel): SignUpResult = runCatching {
        firebase.auth.createUserWithEmailAndPassword(
            newUserModel.email,
            newUserModel.password
        ).await()
    }.toSignUpResult(newUserModel)

    suspend fun login(email: String, password: String): LoginResult = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResult()

    private fun Result<AuthResult>.toLoginResult() =
        if (getOrNull() == null) {
            LoginResult.Failure(exceptionOrNull()?.message)
        } else {
            LoginResult.Success
        }

    private fun Result<AuthResult>.toSignUpResult(newUserModel: NewUserModel) =
        if (getOrNull() == null) {
            SignUpResult.Failure(exceptionOrNull()?.message)
        } else {
            updatePhotoAndDisplayName(newUserModel)
            SignUpResult.Success
        }

    private fun updatePhotoAndDisplayName(newUserModel: NewUserModel) {
        newUserModel.imageUri.let { profileImage ->
            val profileUpdates = userProfileChangeRequest {
                displayName = newUserModel.name
                photoUri = Uri.parse(profileImage.toString())
            }
            firebase.auth.currentUser?.updateProfile(profileUpdates)
        }
    }
}
