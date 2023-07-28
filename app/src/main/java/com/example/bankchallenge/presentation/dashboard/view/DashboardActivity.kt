package com.example.bankchallenge.presentation.dashboard.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ActivityDashboardBinding
import com.example.bankchallenge.domain.entity.AccountMovement
import com.example.bankchallenge.presentation.dashboard.adapter.DashboardAdapter
import com.example.bankchallenge.presentation.dashboard.viewmodel.DashboardViewModel
import com.example.bankchallenge.presentation.dialog.ErrorDialog
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding
    private var dashboardAdapter = DashboardAdapter { movement ->
        showMovementDetail(movement)
    }

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
        val errorDialog = ErrorDialog.newInstance(messageText = errorMessage.toString())
        supportFragmentManager.beginTransaction().add(
            errorDialog,
            ErrorDialog.TAG
        ).commitAllowingStateLoss()
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
        dashboardAdapter.submitListAndUser(data.movements, data.displayProfile.email.toString())
        binding.movementsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dashboardAdapter
            visibility = View.VISIBLE
        }
    }

    private fun showMovementDetail(movement: AccountMovement) {
        val extras = Bundle()

        movement.amount?.let { extras.putLong(AMOUNT, it) }
        movement.author?.let { extras.putString(AUTHOR, it) }
        movement.receiver?.let { extras.putString(RECEIVER, it) }
        movement.authorEmail.let { extras.putString(AUTHOR_EMAIL, it) }
        movement.receiverEmail?.let { extras.putString(RECEIVER_EMAIL, it) }
        movement.description?.let { extras.putString(DESCRIPTION, it) }
        movement.code?.let { extras.putLong(CODE, it) }
        movement.date?.let { extras.putString(DATE, it) }

        startActivity(MovementDetailActivity.newIntent(this).putExtras(extras))
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
        const val AMOUNT = "AMOUNT"
        const val AUTHOR = "AUTHOR"
        const val RECEIVER = "RECEIVER"
        const val AUTHOR_EMAIL = "AUTHOR_EMAIL"
        const val RECEIVER_EMAIL = "RECEIVER_EMAIL"
        const val DESCRIPTION = "DESCRIPTION"
        const val CODE = "CODE"
        const val DATE = "DATE"
    }
}
