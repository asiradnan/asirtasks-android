package com.asiradnan.asirtasks.auth.network

import com.asiradnan.asirtasks.auth.models.LoginRequest
import com.asiradnan.asirtasks.auth.models.RefreshRequest
import com.asiradnan.asirtasks.auth.models.Tokens
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("token/")
    suspend fun login(@Body request: LoginRequest): Tokens

    @POST("token/refresh/")
    suspend fun refreshToken(@Body request: RefreshRequest): Tokens
}