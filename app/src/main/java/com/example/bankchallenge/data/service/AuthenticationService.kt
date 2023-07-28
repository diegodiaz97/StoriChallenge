package com.example.bankchallenge.data.service

import com.example.bankchallenge.domain.entity.NewUserModel
import com.example.bankchallenge.domain.util.LoginResult
import com.example.bankchallenge.domain.util.SignUpResult
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
        val filename = newUserModel.email
        val storageRef = firebase.storage.reference
        val imagesRef = storageRef.child("usersProfileImage/")
        val imgRef = imagesRef.child(filename)

        newUserModel.imageUri?.let {
            imgRef.putFile(it).addOnSuccessListener {
                imgRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = userProfileChangeRequest {
                        displayName = newUserModel.name
                        photoUri = uri
                    }
                    firebase.auth.currentUser?.updateProfile(profileUpdates)
                    newUserModel.imageUri = uri
                }
            }
        }
    }
}
