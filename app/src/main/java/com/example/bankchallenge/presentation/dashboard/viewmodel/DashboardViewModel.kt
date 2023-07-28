package com.example.bankchallenge.presentation.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankchallenge.domain.entity.AccountMovement
import com.example.bankchallenge.domain.entity.DisplayProfile
import com.example.bankchallenge.domain.entity.UserBalance
import com.example.bankchallenge.domain.usecase.GetBalanceUseCase
import com.example.bankchallenge.domain.usecase.GetMovementsUseCase
import com.example.bankchallenge.domain.usecase.GetProfileUseCase
import com.example.bankchallenge.domain.util.CoroutineResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val getProfileUseCase: GetProfileUseCase,
    val getBalanceUseCase: GetBalanceUseCase,
    val getMovementsUseCase: GetMovementsUseCase
) : ViewModel() {

    private val _profileState: MutableLiveData<ProfileData> = MutableLiveData()
    val profileState: LiveData<ProfileData>
        get() = _profileState

    fun onGetProfile() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _profileState.postValue(ProfileData(state = ProfileState.SHOW_LOADING))
            when (val profileData = getProfileUseCase()) {
                is CoroutineResult.Success -> {
                    _profileState.postValue(
                        ProfileData(
                            state = ProfileState.SHOW_PROFILE_DATA,
                            displayProfile = profileData.data
                        )
                    )
                    if (profileData.data.email != null) {
                        onGetUserBalance(profileData.data.email)
                        onGetUserMovements(profileData.data.email)
                    }
                }

                is CoroutineResult.Failure -> {
                    _profileState.postValue(
                        ProfileData(
                            state = ProfileState.SHOW_ERROR_DIALOG,
                            errorMessage = profileData.error
                        )
                    )
                }
            }
        }
    }

    private fun onGetUserBalance(email: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            when (val balanceData = getBalanceUseCase(email)) {
                is CoroutineResult.Success -> {
                    _profileState.postValue(
                        ProfileData(
                            state = ProfileState.SHOW_BALANCE,
                            balance = balanceData.data
                        )
                    )
                }

                is CoroutineResult.Failure -> {
                    _profileState.postValue(
                        ProfileData(
                            state = ProfileState.SHOW_ERROR_DIALOG,
                            errorMessage = balanceData.error
                        )
                    )
                }
            }
        }
    }

    private fun onGetUserMovements(email: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            when (val movementsData = getMovementsUseCase(email)) {
                is CoroutineResult.Success -> {
                    _profileState.postValue(
                        ProfileData(
                            state = ProfileState.SHOW_MOVEMENTS,
                            movements = movementsData.data,
                            displayProfile = DisplayProfile(email = email)
                        )
                    )
                }

                is CoroutineResult.Failure -> {
                    _profileState.postValue(
                        ProfileData(
                            state = ProfileState.SHOW_ERROR_DIALOG,
                            errorMessage = movementsData.error
                        )
                    )
                }
            }
        }
    }

    data class ProfileData(
        val state: ProfileState? = null,
        val displayProfile: DisplayProfile = DisplayProfile(),
        val balance: UserBalance? = UserBalance(),
        val movements: List<AccountMovement> = emptyList(),
        val errorMessage: String? = ""
    )

    enum class ProfileState {
        SHOW_PROFILE_DATA,
        SHOW_BALANCE,
        SHOW_MOVEMENTS,
        SHOW_LOADING,
        SHOW_ERROR_DIALOG
    }
}
