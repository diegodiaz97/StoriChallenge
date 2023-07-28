package com.example.bankchallenge.domain.usecase

import com.example.bankchallenge.data.service.UserService
import com.example.bankchallenge.domain.entity.DisplayProfile
import com.example.bankchallenge.domain.util.CoroutineResult
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val userService: UserService) {
    suspend operator fun invoke(): CoroutineResult<DisplayProfile> = userService.getProfile()
}
