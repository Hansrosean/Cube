package com.mad.cube.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mad.cube.preferences.UserPreferences
import com.mad.cube.user.User

class MainViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    fun getUser(): LiveData<User> {
        return userPreferences.getUser().asLiveData()
    }
}