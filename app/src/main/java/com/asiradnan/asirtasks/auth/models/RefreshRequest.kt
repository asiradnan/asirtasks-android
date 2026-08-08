package com.asiradnan.asirtasks.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val refresh: String
)
