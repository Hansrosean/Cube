package com.mad.cube.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mad.cube.preferences.UserPreferences
import com.mad.cube.user.User
import kotlinx.coroutines.launch

class SecondScreenViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    fun saveUser(user: User) {
        viewModelScope.launch {
            userPreferences.saveUser(user)
        }
    }

    fun getUser(): LiveData<User> {
        return userPreferences.getUser().asLiveData()
    }
}