package com.mad.cube.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mad.cube.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[NAME_KEY] ?:"",
            )
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}