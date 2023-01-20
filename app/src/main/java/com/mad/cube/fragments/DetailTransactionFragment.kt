package com.mad.cube.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mad.cube.R
import com.mad.cube.databinding.FragmentDetailTransactionBinding
import com.mad.cube.transaction.Transaction
import com.mad.cube.transaction.TransactionDatabase
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailTransactionFragment : Fragment() {

    private var _binding: FragmentDetailTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var deleteTransaction: Transaction
    private lateinit var transactionDatabase: TransactionDatabase

    // get data from TransactionFragment
    private val transactionData: Transaction? by lazy {
        arguments?.getParcelable("transaction") as Transaction?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionDatabase = Room.databaseBuilder(
            requireContext(),
            TransactionDatabase::class.java, "transactions"
        ).build()

        binding.labelEditText.setText(transactionData?.label)
        binding.amountEditText.setText(transactionData?.amount.toString())
        binding.descriptionEditText.setText(transactionData?.description)
        // focus view
        binding.transactionDetailConstraintRoot.setOnClickListener {
            activity?.window?.decorView?.clearFocus()

            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        addTransaction()
        backToTransactionFragment()

        binding.btnDeleteTransaction.setOnClickListener {
            deleteItemTransaction(transactionData!!)
        }
    }

    private fun addTransaction() {
        binding.labelEditText.addTextChangedListener {
            binding.btnUpdateTransaction.visibility = View.VISIBLE
            if (it!!.isNotEmpty()) {
                binding.labelTextInputLayout.error = null
            }
        }
        binding.amountEditText.addTextChangedListener {
            binding.btnUpdateTransaction.visibility = View.VISIBLE
            if (it!!.isNotEmpty()) {
                binding.amountTextInputLayout.error = null
            }
        }
        binding.descriptionEditText.addTextChangedListener {
            binding.btnUpdateTransaction.visibility = View.VISIBLE
            if (it!!.isNotEmpty()) {
                binding.descriptionInputLayout.error = null
            }
        }

        binding.btnUpdateTransaction.setOnClickListener {
            val label = binding.labelEditText.text.toString().trim()
            val amount = binding.amountEditText.text.toString().trim().toDoubleOrNull()
            val description = binding.descriptionEditText.text.toString().trim()

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
                    val transaction = Transaction(transactionData!!.id, label, amount, description)
                    updateTransaction(transaction)
                    val toast = Toasty.success(
                        requireContext(),
                        getString(R.string.transaction_changed),
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

    private fun updateTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDatabase.transactionDao().updateTransaction(transaction)
        }
    }

    private fun deleteItemTransaction(transaction: Transaction) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.delete_transaction_dialog))
            .setMessage(getString(R.string.delete_transaction_message_dialog))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteTransaction = transaction

                CoroutineScope(Dispatchers.IO).launch {
                    transactionDatabase.transactionDao().deleteTransaction(transaction)
                }

                val snackBar =
                    Snackbar.make(requireView(), getString(R.string.deleted), Snackbar.LENGTH_SHORT)
                snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                snackBar.show()
                val snackBarCustom =
                    snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                snackBarCustom.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_out, R.anim.fade_out)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainerView, TransactionFragment()).commit()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
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