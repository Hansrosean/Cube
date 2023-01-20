package com.mad.cube.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mad.cube.R
import com.mad.cube.databinding.TransactionRowItemBinding
import com.mad.cube.transaction.Transaction
import com.mad.cube.transaction.TransactionClickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class TransactionAdapter(
    private var transaction: List<Transaction>,
    private val clickHandler: TransactionClickHandler
) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    inner class TransactionHolder(binding: TransactionRowItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val label: TextView = binding.tvLabel
        val amount: TextView = binding.tvAmount

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val currentTransaction = transaction[adapterPosition]
            clickHandler.clickedItemTransaction(currentTransaction)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        return TransactionHolder(
            TransactionRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val bindTransaction: Transaction = transaction[position]
        val context: Context = holder.amount.context

        if (bindTransaction.amount >= 0) {
            holder.amount.text =
                context.getString(R.string.amount_digit_plus).format(bindTransaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.orange))
        } else {
            holder.amount.text =
                context.getString(R.string.amount_digit_minus).format(abs(bindTransaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = bindTransaction.label
    }

    override fun getItemCount(): Int {
        return transaction.size
    }

    fun setTransactionData(transaction: List<Transaction>) {
        this.transaction = transaction

        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }
}

