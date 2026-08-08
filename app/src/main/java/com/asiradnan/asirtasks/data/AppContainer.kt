package com.asiradnan.asirtasks.data

import DataStoreUserPreferencesManager
import UserPreferencesManager
import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import com.asiradnan.asirtasks.auth.data.AuthRepository
import com.asiradnan.asirtasks.auth.data.DataStoreTokenManager
import com.asiradnan.asirtasks.auth.data.DefaultAuthRepository
import com.asiradnan.asirtasks.auth.data.TokenManager
import com.asiradnan.asirtasks.auth.models.RefreshRequest
import com.asiradnan.asirtasks.auth.network.AuthApiService
import com.asiradnan.asirtasks.auth.network.AuthInterceptor
import com.asiradnan.asirtasks.auth.network.TokenAuthenticator
import com.asiradnan.asirtasks.network.AsirTasksApiService
import com.asiradnan.asirtasks.util.NetworkConnectivityObserver
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.jvm.java


interface AppContainer {
    val tasksRepository: TasksRepository
    val taskDao: TaskDAO
    val taskApiService: AsirTasksApiService
    val tokenManager: TokenManager
    val authApiService: AuthApiService
    val authRepository: AuthRepository
    val userPreferencesManager: UserPreferencesManager
    val workManager: WorkManager
val connectivityObserver: NetworkConnectivityObserver
}

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

/**
 * [AppContainer] implementation that provides instance of [DefaultTasksRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://tasksbackend.asiradnan.com/api/tasks/"
    private val authBaseUrl = "https://accounts.asiradnan.com/api/"
    override val tokenManager: TokenManager by lazy {
        DataStoreTokenManager(context)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .addNetworkInterceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.code == 403) {
                response.newBuilder()
                    .code(401)
                    .message("Converted 403 to 401 to trigger Authenticator")
                    .build()
            } else {
                response
            }
        }
        .addInterceptor(AuthInterceptor(tokenManager))
        .authenticator(TokenAuthenticator(tokenManager) { refreshToken ->
            try {
                authApiService.refreshToken(RefreshRequest(refreshToken))
            } catch (e: Exception) {
                Log.e("Authenticator", "Refresh failed", e)
                null
            }
        })
        .build()

    private val authRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(authBaseUrl)
        .client(okHttpClient)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    override val authApiService: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    override val taskApiService: AsirTasksApiService by lazy {
        retrofit.create(AsirTasksApiService::class.java)
    }

    override val taskDao: TaskDAO by lazy {
        AsirTasksDatabase.getDatabase(context).taskDao()
    }

    override val tasksRepository: TasksRepository by lazy {
        DefaultTasksRepository(taskDao, context = context)
    }

    override val authRepository: AuthRepository by lazy {
        DefaultAuthRepository(authApiService, tokenManager)
    }

    override val userPreferencesManager: UserPreferencesManager by lazy {
        DataStoreUserPreferencesManager(context)
    }

    override val workManager: WorkManager by lazy {
        WorkManager.getInstance(context) // 2. Initialize it
    }

    override val connectivityObserver by lazy { NetworkConnectivityObserver(context) }
}