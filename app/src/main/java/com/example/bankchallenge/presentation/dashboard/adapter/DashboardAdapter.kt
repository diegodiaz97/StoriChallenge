package com.example.bankchallenge.presentation.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bankchallenge.R
import com.example.bankchallenge.databinding.ItemIncomingMovementBinding
import com.example.bankchallenge.databinding.ItemOutgoingMovementBinding
import com.example.bankchallenge.domain.entity.AccountMovement

class DashboardAdapter(private val onItemClick: (AccountMovement) -> Unit) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var movementsList: List<AccountMovement> = ArrayList()
    private var userMail: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            R.layout.item_incoming_movement ->
                (IncomingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_incoming_movement, parent, false), onItemClick
                ))

            else ->
                (OutgoingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_outgoing_movement, parent, false), onItemClick
                ))
        }

    override fun getItemViewType(position: Int): Int =
        when (movementsList[position].authorEmail) {
            userMail -> R.layout.item_outgoing_movement
            else -> R.layout.item_incoming_movement
        }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(movementsList[position])
    }

    override fun getItemCount(): Int = movementsList.size

    fun submitListAndUser(movements: List<AccountMovement>, mail: String) {
        this.movementsList = movements
        this.userMail = mail
    }
}

class IncomingViewHolder(itemView: View, private val onItemClick: (AccountMovement) -> Unit) :
    BaseViewHolder(itemView) {

    private val binding = ItemIncomingMovementBinding.bind(itemView)

    override fun bind(movementItem: AccountMovement) {
        with(binding) {
            authorTextView.text = movementItem.author
            receiverTextView.text = movementItem.receiver
            dateTextView.text= movementItem.date
            amountTextView.text = "$ ${movementItem.amount}"
            balanceCardView.setOnClickListener {
                onItemClick(movementItem)
            }
        }
    }
}

class OutgoingViewHolder(itemView: View, private val onItemClick: (AccountMovement) -> Unit) :
    BaseViewHolder(itemView) {

    private val binding = ItemOutgoingMovementBinding.bind(itemView)

    override fun bind(movementItem: AccountMovement) {
        with(binding) {
            authorTextView.text = movementItem.author
            receiverTextView.text = movementItem.receiver
            dateTextView.text= movementItem.date
            amountTextView.text = "$ ${movementItem.amount}"
            balanceCardView.setOnClickListener {
                onItemClick(movementItem)
            }
        }
    }
}