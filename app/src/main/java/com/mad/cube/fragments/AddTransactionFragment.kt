package com.mad.cube.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.mad.cube.R
import com.mad.cube.databinding.FragmentAddTransactionBinding
import com.mad.cube.transaction.Transaction
import com.mad.cube.transaction.TransactionDatabase
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTransaction()
        backToTransactionFragment()
    }

    private fun addTransaction() {
        binding.btnAddTransaction.setOnClickListener {
            val label = binding.labelEditText.text.toString().trim()
            val amount = binding.amountEditText.text.toString().trim().toDoubleOrNull()
            val description = binding.descriptionEditText.text.toString().trim()

            // if edit text not null
            binding.labelEditText.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    binding.labelTextInputLayout.error = null
                }
            }
            binding.amountEditText.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    binding.amountTextInputLayout.error = null
                }
            }
            binding.descriptionEditText.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    binding.descriptionInputLayout.error = null
                }
            }

            when {
                label.isEmpty() -> {
                    binding.labelTextInputLayout.error = "Add a label"
                }
                amount == null -> {
                    binding.amountTextInputLayout.error = "Add some amount"
                }
                description.isEmpty() -> {
                    binding.descriptionInputLayout.error = "Add some description"
                }
                else -> {
                    val transaction = Transaction(0, label, amount, description)
                    insertTransaction(transaction)
                    val toast = Toasty.success(
                        requireContext(),
                        getString(R.string.transaction_added),
                        Toast.LENGTH_SHORT,
                        true
                    )
                    toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 50)
                    toast.show()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.setCustomAnimations(R.anim.slide_out, R.anim.fade_out)
                        ?.replace(R.id.fragmentContainerView, TransactionFragment())
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }
        }
    }

    private fun insertTransaction(transaction: Transaction) {
        val transactionDatabase = Room.databaseBuilder(
            requireContext(),
            TransactionDatabase::class.java, "transactions"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            transactionDatabase.transactionDao().insertTransaction(transaction)
        }
    }

    private fun backToTransactionFragment() {
        binding.btnBackToTransactionFragment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_out, R.anim.fade_out)
                .addToBackStack(null)
                .replace(R.id.fragmentContainerView, TransactionFragment()).commit()
        }
    }
}