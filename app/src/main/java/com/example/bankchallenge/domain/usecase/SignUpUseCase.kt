package com.example.bankchallenge.domain.usecase

import com.example.bankchallenge.data.service.AuthenticationService
import com.example.bankchallenge.data.service.UserService
import com.example.bankchallenge.domain.entity.NewUserModel
import com.example.bankchallenge.domain.util.SignUpResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authService: AuthenticationService,
    private val userService: UserService
) {
    suspend operator fun invoke(newUser: NewUserModel): SignUpResult {
        val newAccount = authService.signUp(newUser)
        if (newAccount is SignUpResult.Success) {
            userService.addUser(newUser)
        }
        return newAccount
    }
}
