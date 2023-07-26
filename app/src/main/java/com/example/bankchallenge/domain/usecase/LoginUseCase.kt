package com.example.bankchallenge.domain.usecase

import com.example.bankchallenge.data.service.AuthenticationService
import com.example.bankchallenge.domain.util.LoginResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authService: AuthenticationService) {
    suspend operator fun invoke(email: String, password: String): LoginResult =
        authService.login(email, password)
}
