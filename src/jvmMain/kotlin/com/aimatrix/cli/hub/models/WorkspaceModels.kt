package com.aimatrix.cli.hub.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkspaceRequest(
    val name: String,
    val description: String? = null,
    val visibility: WorkspaceVisibility = WorkspaceVisibility.PRIVATE,
    val organizationId: String? = null,
    val tags: Set<String> = emptySet(),
    val configuration: WorkspaceConfiguration = WorkspaceConfiguration()
)

@Serializable
data class UpdateWorkspaceRequest(
    val name: String? = null,
    val description: String? = null,
    val visibility: WorkspaceVisibility? = null,
    val tags: Set<String>? = null,
    val configuration: WorkspaceConfiguration? = null
)

@Serializable
data class WorkspaceDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val ownerId: String,
    val organizationId: String? = null,
    val status: WorkspaceStatus,
    val visibility: WorkspaceVisibility,
    val tags: Set<String>,
    val agentCount: Int,
    val activeSessionCount: Int,
    val memberCount: Int,
    val statistics: WorkspaceStatistics,
    val configuration: WorkspaceConfiguration,
    val createdAt: String,
    val updatedAt: String,
    val lastActivityAt: String? = null
)

@Serializable
enum class WorkspaceVisibility {
    @SerialName("public") PUBLIC,
    @SerialName("private") PRIVATE,
    @SerialName("organization") ORGANIZATION
}

@Serializable
enum class WorkspaceStatus {
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("SUSPENDED") SUSPENDED,
    @SerialName("ARCHIVED") ARCHIVED,
    @SerialName("DELETED") DELETED
}

@Serializable
data class WorkspaceConfiguration(
    val maxAgents: Int = 10,
    val maxConcurrentSessions: Int = 5,
    val storageQuotaGb: Int = 100,
    val computeQuotaHours: Int = 1000,
    val allowedAgentTypes: Set<String> = setOf("general", "code", "data", "creative"),
    val enabledFeatures: Set<String> = setOf("collaboration", "versioning", "analytics"),
    val resourceLimits: ResourceLimits = ResourceLimits()
)

@Serializable
data class ResourceLimits(
    val cpuCoresPerAgent: Int = 2,
    val memoryGbPerAgent: Int = 4,
    val gpuEnabled: Boolean = false,
    val networkBandwidthMbps: Int = 100
)

@Serializable
data class WorkspaceStatistics(
    val totalTasksCompleted: Long,
    val avgTaskCompletionSeconds: Double,
    val successRate: Double,
    val totalComputeHours: Double,
    val storageUsedGb: Double,
    val activeUsers24h: Int,
    val totalCollaborations: Long
)

@Serializable
data class WorkspaceMemberDto(
    val userId: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val role: WorkspaceMemberRole,
    val permissions: Set<String>,
    val joinedAt: String,
    val lastActiveAt: String? = null
)

@Serializable
enum class WorkspaceMemberRole {
    @SerialName("owner") OWNER,
    @SerialName("admin") ADMIN,
    @SerialName("editor") EDITOR,
    @SerialName("viewer") VIEWER
}