package com.example.bankchallenge.domain.usecase

import com.example.bankchallenge.data.service.MovementsService
import com.example.bankchallenge.domain.entity.AccountMovement
import com.example.bankchallenge.domain.util.CoroutineResult
import javax.inject.Inject

class GetMovementsUseCase @Inject constructor(private val movementsService: MovementsService) {
    suspend operator fun invoke(email: String): CoroutineResult<List<AccountMovement>> =
        movementsService.getMovements(email)
}
