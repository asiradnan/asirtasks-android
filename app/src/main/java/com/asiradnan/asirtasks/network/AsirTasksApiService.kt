package com.asiradnan.asirtasks.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AsirTasksApiService {
    @GET(".")
    suspend fun getTasks(): List<NetworkTask>

    @POST(".")
    suspend fun createTask(@Body task: NetworkTask): NetworkTask

    @PUT("{id}/")
    suspend fun updateTask(@Path("id") taskId: String, @Body task: NetworkTask): NetworkTask

    @DELETE("{id}/")
    suspend fun deleteTask(@Path("id") taskId: String)
}