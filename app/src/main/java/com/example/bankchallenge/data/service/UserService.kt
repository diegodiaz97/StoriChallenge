package com.example.bankchallenge.data.service

import com.example.bankchallenge.data.mapper.transformToDisplayUser
import com.example.bankchallenge.data.mapper.transformToFirebaseUser
import com.example.bankchallenge.data.util.Constants.ONE_INT
import com.example.bankchallenge.data.util.Constants.POST_DELAYED_ERROR_MESSAGE
import com.example.bankchallenge.data.util.Constants.USER_COLLECTION
import com.example.bankchallenge.data.util.PostDelayedResponseEnum
import com.example.bankchallenge.domain.entity.DisplayProfile
import com.example.bankchallenge.domain.entity.NewUserModel
import com.example.bankchallenge.domain.util.CoroutineResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    suspend fun addUser(newUser: NewUserModel) = runCatching {
        firebase.db
            .collection(USER_COLLECTION)
            .document(newUser.email)
            .set(newUser.transformToFirebaseUser()).await()
    }

    suspend fun getProfile(): CoroutineResult<DisplayProfile> {
        var user = DisplayProfile()
        var status = PostDelayedResponseEnum.POST_DELAYED_ERROR
        var error = POST_DELAYED_ERROR_MESSAGE
        val done = CountDownLatch(ONE_INT)

        firebase.db
            .collection(USER_COLLECTION)
            .document(firebase.auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { result ->
                user = result.transformToDisplayUser()
                status = PostDelayedResponseEnum.POST_DELAYED_SUCCESS
                done.countDown()
            }
            .addOnFailureListener { exception ->
                error = exception.message.toString()
                status = PostDelayedResponseEnum.POST_DELAYED_ERROR
                done.countDown()
            }
        withContext(Dispatchers.IO) { done.await() }

        return when (status) {
            PostDelayedResponseEnum.POST_DELAYED_SUCCESS -> CoroutineResult.Success(user)
            PostDelayedResponseEnum.POST_DELAYED_ERROR -> CoroutineResult.Failure(error)
        }
    }
}
