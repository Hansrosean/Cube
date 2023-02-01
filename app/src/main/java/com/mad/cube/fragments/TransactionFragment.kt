package com.mad.cube.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mad.cube.R
import com.mad.cube.adapter.TransactionAdapter
import com.mad.cube.databinding.FragmentTransactionBinding
import com.mad.cube.transaction.Transaction
import com.mad.cube.transaction.TransactionClickHandler
import com.mad.cube.transaction.TransactionDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(), TransactionClickHandler {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactions: List<Transaction>
    private lateinit var oldTransactions: List<Transaction>
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var transactionDatabase: TransactionDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactions = arrayListOf()
        transactionAdapter = TransactionAdapter(transactions, this)
        linearLayoutManager = LinearLayoutManager(requireContext().applicationContext)

        transactionDatabase =
            Room.databaseBuilder(
                requireContext(),
                TransactionDatabase::class.java, "transactions"
            ).build()

        _binding!!.rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        updateDashboard()
        fabAddTransaction()
        fetchAllTransactions()
        deleteTransaction()

        // onBackPressed action
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                        .setTitle(getString(R.string.exit))
                        .setMessage(getString(R.string.are_you_sure_exit))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            requireActivity().finish()
                        }
                        .setNegativeButton(getString(R.string.no), null)
                        .show()
                }
            }
        )
    }

    private fun updateDashboard() {
        val totalAmount: Double = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
        val expenseAmount = totalAmount - budgetAmount

        Handler(Looper.getMainLooper()).post {
            _binding?.tvBalance?.text =
                requireContext().getString(R.string.amount_digit).format(totalAmount)
            _binding?.tvBudget?.text =
                requireContext().getString(R.string.amount_digit).format(budgetAmount)
            _binding?.tvExpense?.text =
                requireContext().getString(R.string.amount_digit).format(expenseAmount)
        }
    }

    private fun fetchAllTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            transactions = transactionDatabase.transactionDao().getAllTransactions()

            updateDashboard()
            transactionAdapter.setTransactionData(transactions)
        }
    }

    private fun deleteTransaction() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteItemTransaction(transactions[viewHolder.adapterPosition])
            }

        }
        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(binding.rvTransactions)
    }

    private fun deleteItemTransaction(transaction: Transaction) {
        deletedTransaction = transaction
        oldTransactions = transactions

        CoroutineScope(Dispatchers.IO).launch {
            transactionDatabase.transactionDao().deleteTransaction(transaction)

            transactions = transactions.filter {
                it.id != transaction.id
            }
            activity?.runOnUiThread {
                updateDashboard()
                transactionAdapter.setTransactionData(transactions)

                val snackBar =
                    Snackbar.make(requireView(), getString(R.string.deleted), Snackbar.LENGTH_SHORT)
                snackBar.setAction(getString(R.string.undo)) {
                    undoDeleteTransaction()
                }
                snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                snackBar.show()
                val snackBarCustom =
                    snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                snackBarCustom.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
            }
        }
    }

    private fun undoDeleteTransaction() {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDatabase.transactionDao().insertTransaction(deletedTransaction)
            transactions = oldTransactions

            CoroutineScope(Dispatchers.Main).launch {
                transactionAdapter.setTransactionData(transactions)
                updateDashboard()
            }
        }
    }

    override fun clickedItemTransaction(transaction: Transaction) {
        val detailTransactionFragment = DetailTransactionFragment().apply {
            arguments = Bundle().apply {
                putParcelable("transaction", transaction)
            }
        }
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
            .replace(R.id.fragmentContainerView, detailTransactionFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fabAddTransaction() {
        binding.fabAddTransaction.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                .replace(R.id.fragmentContainerView, AddTransactionFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        fetchAllTransactions()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
}