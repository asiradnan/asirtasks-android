package com.asiradnan.asirtasks.auth.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Modular Tip: This extension is private so it doesn't leak outside this file
private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

interface TokenManager {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    suspend fun saveTokens(accessToken: String, refreshToken: String?)
    suspend fun clearTokens()
}

class DataStoreTokenManager(private val context: Context) : TokenManager {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    override val accessToken: Flow<String?> = context.dataStore.data.map { it[accessTokenKey] }
    override val refreshToken: Flow<String?> = context.dataStore.data.map { it[refreshTokenKey] }

    override suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        context.dataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            if (refreshToken != null) {
                prefs[refreshTokenKey] = refreshToken
            }
        }
    }

    override suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(accessTokenKey)
            prefs.remove(refreshTokenKey)
        }
    }
}