package com.asiradnan.asirtasks.auth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    @SerialName("access")
    val accessToken: String,
    @SerialName("refresh")
    val refreshToken: String? = null
)

