package com.mad.cube.onboarding

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mad.cube.R
import com.mad.cube.ViewModelFactory
import com.mad.cube.databinding.FragmentSecondScreenBinding
import com.mad.cube.fragments.TransactionFragment
import com.mad.cube.preferences.SettingPreferences
import com.mad.cube.preferences.UserPreferences
import com.mad.cube.user.User
import com.mad.cube.viewmodel.SecondScreenViewModel
import es.dmoral.toasty.Toasty


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SecondScreenFragment : Fragment() {

    private var _binding: FragmentSecondScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var secondScreenViewModel: SecondScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUsername()
        setupViewModel()
    }

    private fun setupViewModel() {
        val userDataStore = requireContext().dataStore
        secondScreenViewModel =
            ViewModelProvider(
                requireActivity(),
                ViewModelFactory(
                    SettingPreferences.getInstance(userDataStore),
                    UserPreferences.getInstance(userDataStore)
                )
            )[SecondScreenViewModel::class.java]
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    private fun getUsername() {
        binding.tvFinishOnboarding.setOnClickListener {
            val edtUsername = binding.edtUsername.text.toString().trim()

            if (edtUsername.isEmpty()) {
                binding.edtUsername.error = getString(R.string.required)
            } else {
                secondScreenViewModel.saveUser(User(edtUsername))
                findNavController().navigate(R.id.transactionFragment)
                onBoardingFinished()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    ?.replace(R.id.fragmentContainerView, TransactionFragment())
                    ?.commit()

                val toast =
                    Toasty.normal(requireContext(), getString(R.string.welcome), Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 50)
                toast.show()
            }
        }
    }
}