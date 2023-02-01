package com.mad.cube.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.mad.cube.R
import com.mad.cube.ViewModelFactory
import com.mad.cube.databinding.ActivityMainBinding
import com.mad.cube.preferences.SettingPreferences
import com.mad.cube.preferences.UserPreferences
import com.mad.cube.viewmodel.MainViewModel
import com.mad.cube.viewmodel.SettingViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0F

        saveSettingPreference()
        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                SettingPreferences.getInstance(dataStore),
                UserPreferences.getInstance(dataStore)
            )
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            supportActionBar?.title = getString(R.string.greeting, user.name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bar_settings -> {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettingPreference() {
        val settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                SettingPreferences.getInstance(dataStore), UserPreferences.getInstance(dataStore)
            )
        )[SettingViewModel::class.java]
        settingViewModel.getThemeSettings().observe(this) { isDarkModeEnable: Boolean ->
            if (isDarkModeEnable) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

// onBackPressed moved to TransactionFragment
    /*
        override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@MainActivity, R.style.AlertDialogTheme)
            .setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.are_you_sure_exit))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
        }
    */
}