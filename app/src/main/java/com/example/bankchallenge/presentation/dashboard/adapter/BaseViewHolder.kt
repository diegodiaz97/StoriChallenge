package com.example.bankchallenge.presentation.dashboard.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bankchallenge.domain.entity.AccountMovement

abstract class BaseViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(movement: AccountMovement)
}
