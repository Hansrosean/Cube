package com.mad.cube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mad.cube.preferences.SettingPreferences
import com.mad.cube.preferences.UserPreferences
import com.mad.cube.viewmodel.MainViewModel
import com.mad.cube.viewmodel.SecondScreenViewModel
import com.mad.cube.viewmodel.SettingViewModel

class ViewModelFactory(private val settingPreferences: SettingPreferences, private val userPreferences: UserPreferences) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(settingPreferences) as T
            }
            modelClass.isAssignableFrom(SecondScreenViewModel::class.java) -> {
                SecondScreenViewModel(userPreferences) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}