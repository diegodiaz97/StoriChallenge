package com.example.bankchallenge.data.service

import com.example.bankchallenge.data.mapper.transformToFirebaseUser
import com.example.bankchallenge.presentation.util.NewUserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignUpService @Inject constructor(private val firebase: FirebaseClient) {

    suspend fun addUser(newUser: NewUserModel) = runCatching {
        firebase.db
            .collection(USER_COLLECTION)
            .document(newUser.email)
            .set(newUser.transformToFirebaseUser()).await()
    }.isSuccess

    companion object {
        const val USER_COLLECTION = "users"
    }
}
