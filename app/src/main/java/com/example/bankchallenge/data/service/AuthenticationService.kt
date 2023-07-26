package com.example.bankchallenge.data.service

import com.example.bankchallenge.domain.util.LoginResult
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationService @Inject constructor(private val firebase: FirebaseClient) {
    suspend fun login(email: String, password: String): LoginResult = runCatching {
        firebase.auth.signInWithEmailAndPassword(email, password).await()
    }.toLoginResult()

    private fun Result<AuthResult>.toLoginResult() =
        if (getOrNull() == null) {
            LoginResult.Failure(exceptionOrNull()?.message)
        } else {
            LoginResult.Success
        }
}
