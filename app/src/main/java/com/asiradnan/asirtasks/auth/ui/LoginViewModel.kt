package com.asiradnan.asirtasks.auth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asiradnan.asirtasks.auth.data.AuthRepository
import com.asiradnan.asirtasks.auth.models.LoginRequest
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = authRepository.login(LoginRequest(username, password))
            isLoading = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                val exception = result.exceptionOrNull()
                errorMessage = when (exception) {
                    is IOException -> "No internet connection. Please check your network."
                    else -> "Login failed. Please check your credentials."
                }
            }
        }
    }
}