package com.example.quizmaster.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.quizmaster.data.model.User
import com.example.quizmaster.data.model.UserRole
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore for user session management
 */
class UserSessionManager(private val context: Context) {
    
    private val Context.dataStore by preferencesDataStore(name = "user_session")
    private val gson = Gson()
    
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }
    
    /**
     * Save user session
     */
    suspend fun saveUserSession(token: String, user: User) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
            preferences[USER_DATA_KEY] = gson.toJson(user)
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }
    
    /**
     * Get auth token
     */
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }
    
    /**
     * Get current user
     */
    val currentUser: Flow<User?> = context.dataStore.data.map { preferences ->
        preferences[USER_DATA_KEY]?.let { json ->
            try {
                gson.fromJson(json, User::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Check if user is logged in
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }
    
    /**
     * Clear user session (logout)
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Update user data
     */
    suspend fun updateUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_DATA_KEY] = gson.toJson(user)
        }
    }
}
