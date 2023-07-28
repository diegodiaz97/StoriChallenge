package com.example.bankchallenge.data.service

import com.example.bankchallenge.data.mapper.transformToBalance
import com.example.bankchallenge.data.util.Constants.BALANCE_COLLECTION
import com.example.bankchallenge.data.util.Constants.ONE_INT
import com.example.bankchallenge.data.util.Constants.POST_DELAYED_ERROR_MESSAGE
import com.example.bankchallenge.data.util.PostDelayedResponseEnum
import com.example.bankchallenge.domain.entity.UserBalance
import com.example.bankchallenge.domain.util.CoroutineResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceService @Inject constructor(private val firebase: FirebaseClient) {

    suspend fun getUserBalance(email: String): CoroutineResult<UserBalance> {
        var balance = UserBalance()
        var status = PostDelayedResponseEnum.POST_DELAYED_ERROR
        var error = POST_DELAYED_ERROR_MESSAGE
        val done = CountDownLatch(ONE_INT)

        firebase.db
            .collection(BALANCE_COLLECTION)
            .document(email)
            .get()
            .addOnSuccessListener { result ->
                balance = result.transformToBalance()
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
            PostDelayedResponseEnum.POST_DELAYED_SUCCESS -> CoroutineResult.Success(balance)
            PostDelayedResponseEnum.POST_DELAYED_ERROR -> CoroutineResult.Failure(error)
        }
    }
}