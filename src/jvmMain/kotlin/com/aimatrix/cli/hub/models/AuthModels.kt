package com.aimatrix.cli.hub.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false,
    val mfaCode: String? = null
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserDto,
    val requiresMfa: Boolean = false,
    val mfaToken: String? = null
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val organizationId: String? = null,
    val roles: Set<String>,
    val permissions: Set<String>,
    val status: UserStatus,
    val emailVerified: Boolean,
    val mfaEnabled: Boolean,
    val createdAt: String,
    val lastLoginAt: String? = null
)

@Serializable
enum class UserStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("SUSPENDED") SUSPENDED,
    @SerialName("LOCKED") LOCKED,
    @SerialName("DELETED") DELETED
}