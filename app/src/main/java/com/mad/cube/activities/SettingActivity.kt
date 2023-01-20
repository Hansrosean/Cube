package com.mad.cube.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.mad.cube.R
import com.mad.cube.ViewModelFactory
import com.mad.cube.databinding.ActivitySettingBinding
import com.mad.cube.preferences.SettingPreferences
import com.mad.cube.preferences.UserPreferences
import com.mad.cube.viewmodel.SettingViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.elevation = 0F

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        openLanguageSetting()
    }


    private fun openLanguageSetting() {
        binding.cvLanguageSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupViewModel() {
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                SettingPreferences.getInstance(dataStore), UserPreferences.getInstance(dataStore)
            )
        )[SettingViewModel::class.java]

        val switchTheme = binding.switchTheme
        settingViewModel.getThemeSettings().observe(this) {
                isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}