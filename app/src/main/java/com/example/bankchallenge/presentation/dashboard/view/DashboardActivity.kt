package com.example.bankchallenge.presentation.dashboard.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ActivityDashboardBinding
import com.example.bankchallenge.presentation.dashboard.viewmodel.DashboardViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        dashboardViewModel.profileState.observe(this, ::onProfileStateChanged)
    }

    private fun setupViews() {
        dashboardViewModel.onGetProfile()
    }

    private fun onProfileStateChanged(data: DashboardViewModel.ProfileData) {
        when (data.state) {
            DashboardViewModel.ProfileState.SHOW_PROFILE_DATA -> updateProfileUi(data)
            DashboardViewModel.ProfileState.SHOW_BALANCE -> updateBalanceUi(data)
            DashboardViewModel.ProfileState.SHOW_MOVEMENTS -> updateMovementsUi(data)
            DashboardViewModel.ProfileState.SHOW_LOADING -> showLoader()
            DashboardViewModel.ProfileState.SHOW_ERROR_DIALOG -> showError(data.errorMessage)
            else -> {}
        }
    }

    private fun showError(errorMessage: String? = "") {
        hideLoader()
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun updateProfileUi(data: DashboardViewModel.ProfileData) {
        hideLoader()
        Picasso.get()
            .load(data.displayProfile.profileImage?.toUri())
            .placeholder(R.drawable.ic_user)
            .into(binding.profileImageView)
        binding.nameTextView.text = getString(
            R.string.user_name_text,
            data.displayProfile.name,
            data.displayProfile.lastName
        )
    }

    private fun updateBalanceUi(data: DashboardViewModel.ProfileData) {
        hideLoader()
        binding.balanceView.balanceTextView.text =
            getString(R.string.balance_value_text, data.balance?.amount, data.balance?.amountLimit)
        binding.balanceView.balanceLastDigitsTextView.text =
            getString(R.string.last_digits_text, data.balance?.lastDigits)
    }

    private fun updateMovementsUi(data: DashboardViewModel.ProfileData) {
        hideLoader()
        binding.movementsTextView.text = data.movements.toString()
    }

    private fun showLoader() {
        binding.loginLoader.visibility = View.VISIBLE
        binding.loaderBackgroundView.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.loginLoader.visibility = View.GONE
        binding.loaderBackgroundView.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, DashboardActivity::class.java)
    }
}
