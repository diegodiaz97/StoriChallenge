package com.example.bankchallenge.data.service

import com.example.bankchallenge.data.mapper.transformToMovement
import com.example.bankchallenge.data.util.Constants
import com.example.bankchallenge.data.util.Constants.MOVEMENTS_COLLECTION
import com.example.bankchallenge.data.util.Constants.POST_DELAYED_ERROR_MESSAGE
import com.example.bankchallenge.data.util.PostDelayedResponseEnum
import com.example.bankchallenge.domain.entity.AccountMovement
import com.example.bankchallenge.domain.util.CoroutineResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovementsService @Inject constructor(private val firebase: FirebaseClient) {

    suspend fun getMovements(email: String): CoroutineResult<List<AccountMovement>> {
        val movements = mutableListOf<AccountMovement>()
        var status = PostDelayedResponseEnum.POST_DELAYED_ERROR
        var error = POST_DELAYED_ERROR_MESSAGE
        val done = CountDownLatch(Constants.ONE_INT)

        firebase.db
            .collection(MOVEMENTS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (movementBelongsToUser(document.transformToMovement(), email)) {
                        movements.add(document.transformToMovement())
                    }
                }
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
            PostDelayedResponseEnum.POST_DELAYED_SUCCESS -> CoroutineResult.Success(sortByDate(movements))
            PostDelayedResponseEnum.POST_DELAYED_ERROR -> CoroutineResult.Failure(error)
        }
    }

    private fun sortByDate(movements: MutableList<AccountMovement>): List<AccountMovement> {
        return movements.sortedWith(compareBy<AccountMovement> { it.date?.get(6) }
            .thenBy { it.date?.get(7) }.thenBy { it.date?.get(8) }.thenBy { it.date?.get(9) }
            .thenBy { it.date?.get(3) }.thenBy { it.date?.get(4) }.thenBy { it.date?.get(0) }
            .thenBy { it.date?.get(1) }).reversed()
    }

    private fun movementBelongsToUser(movement: AccountMovement, email: String): Boolean {
        return movement.authorEmail == email || movement.receiverEmail == email
    }
}
