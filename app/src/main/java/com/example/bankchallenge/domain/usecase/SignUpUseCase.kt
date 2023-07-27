package com.example.bankchallenge.domain.usecase

import com.example.bankchallenge.data.service.AuthenticationService
import com.example.bankchallenge.data.service.SignUpService
import com.example.bankchallenge.domain.util.SignUpResult
import com.example.bankchallenge.presentation.util.NewUserModel
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authService: AuthenticationService,
    private val userService: SignUpService
) {
    suspend operator fun invoke(newUser: NewUserModel): SignUpResult {
        val newAccount = authService.signUp(newUser)
        if (newAccount is SignUpResult.Success) {
            userService.addUser(newUser)
        }
        return newAccount
    }
}
