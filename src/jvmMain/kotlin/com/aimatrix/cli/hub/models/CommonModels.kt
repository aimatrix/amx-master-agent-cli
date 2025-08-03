package com.aimatrix.cli.hub.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val success: Boolean = true,
    val errors: List<String>? = null
)