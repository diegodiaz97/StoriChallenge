package com.example.bankchallenge.domain.usecase

import com.example.bankchallenge.data.service.BalanceService
import com.example.bankchallenge.domain.entity.UserBalance
import com.example.bankchallenge.domain.util.CoroutineResult
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(private val balanceService: BalanceService) {
    suspend operator fun invoke(email: String): CoroutineResult<UserBalance> =
        balanceService.getUserBalance(email)
}
