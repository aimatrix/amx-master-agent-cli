package com.aimatrix.cli.hub.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentDto(
    val id: String,
    val workspaceId: String,
    val agentTypeId: String,
    val templateId: String?,
    val name: String,
    val displayName: String?,
    val description: String?,
    val status: AgentStatus,
    val version: String,
    val ownerId: String,
    val configuration: AgentConfigurationDto,
    val statistics: AgentStatisticsDto,
    val capabilities: List<AgentCapabilityDto>,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val lastActivityAt: String
)

@Serializable
enum class AgentStatus {
    @SerialName("CREATED") CREATED,
    @SerialName("STARTING") STARTING,
    @SerialName("RUNNING") RUNNING,
    @SerialName("STOPPING") STOPPING,
    @SerialName("STOPPED") STOPPED,
    @SerialName("FAILED") FAILED,
    @SerialName("SUSPENDED") SUSPENDED
}

@Serializable
data class CreateAgentRequest(
    val workspaceId: String,
    val agentTypeId: String,
    val templateId: String?,
    val name: String,
    val displayName: String?,
    val description: String?,
    val configuration: AgentConfiguration,
    val capabilities: List<String> = emptyList()
)

@Serializable
data class UpdateAgentRequest(
    val displayName: String?,
    val description: String?,
    val configuration: AgentConfiguration?,
    val capabilities: List<String>?
)

@Serializable
data class AgentConfigurationDto(
    val id: String,
    val agentId: String,
    val configuration: Map<String, String>,
    val systemPrompt: String?,
    val modelSettings: ModelSettings,
    val toolsConfig: Map<String, String>,
    val resourceLimits: AgentResourceLimits,
    val environmentVars: Map<String, String>,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class AgentConfiguration(
    val configuration: Map<String, String> = emptyMap(),
    val systemPrompt: String?,
    val modelSettings: ModelSettings = ModelSettings(),
    val toolsConfig: Map<String, String> = emptyMap(),
    val resourceLimits: AgentResourceLimits = AgentResourceLimits(),
    val environmentVars: Map<String, String> = emptyMap()
)

@Serializable
data class ModelSettings(
    val model: String = "gpt-4",
    val temperature: Double = 0.7,
    val maxTokens: Int = 2000,
    val topP: Double = 1.0,
    val frequencyPenalty: Double = 0.0,
    val presencePenalty: Double = 0.0
)

@Serializable
data class AgentResourceLimits(
    val maxCpuCores: Double = 2.0,
    val maxMemoryMb: Int = 1024,
    val maxExecutionTimeSeconds: Int = 300,
    val maxConcurrentSessions: Int = 5,
    val maxTokensPerHour: Int = 10000
)

@Serializable
data class AgentStatisticsDto(
    val totalSessions: Int,
    val activeSessions: Int,
    val totalMessages: Int,
    val totalExecutions: Int,
    val successfulExecutions: Int,
    val failedExecutions: Int,
    val avgResponseTimeMs: Int,
    val totalTokensUsed: Long,
    val totalCostCents: Long,
    val uptimeSeconds: Long,
    val lastUsedAt: String?,
    val successRate: Double
)

@Serializable
data class AgentCapabilityDto(
    val id: String,
    val agentId: String,
    val capabilityName: String,
    val capabilityType: CapabilityType,
    val configuration: Map<String, String>,
    val isEnabled: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
enum class CapabilityType {
    @SerialName("TOOL") TOOL,
    @SerialName("API") API,
    @SerialName("INTEGRATION") INTEGRATION,
    @SerialName("PERMISSION") PERMISSION,
    @SerialName("FILE_ACCESS") FILE_ACCESS,
    @SerialName("CODE_EXECUTION") CODE_EXECUTION,
    @SerialName("WEB_BROWSING") WEB_BROWSING
}

// Session models
@Serializable
data class AgentSessionDto(
    val id: String,
    val agentId: String,
    val workspaceId: String,
    val userId: String,
    val title: String?,
    val description: String?,
    val status: AgentSessionStatus,
    val sessionType: AgentSessionType,
    val context: Map<String, String>,
    val metadata: Map<String, String>,
    val messageCount: Int,
    val tokenCount: Int,
    val costCents: Int,
    val startedAt: String,
    val endedAt: String?,
    val lastActivityAt: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateAgentSessionRequest(
    val agentId: String,
    val title: String?,
    val description: String?,
    val sessionType: AgentSessionType = AgentSessionType.CHAT,
    val context: Map<String, String> = emptyMap()
)

@Serializable
enum class AgentSessionStatus {
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("PAUSED") PAUSED,
    @SerialName("COMPLETED") COMPLETED,
    @SerialName("FAILED") FAILED,
    @SerialName("CANCELLED") CANCELLED
}

@Serializable
enum class AgentSessionType {
    @SerialName("CHAT") CHAT,
    @SerialName("TASK") TASK,
    @SerialName("WORKFLOW") WORKFLOW,
    @SerialName("COLLABORATION") COLLABORATION
}

// Message models
@Serializable
data class SessionMessageDto(
    val id: String,
    val sessionId: String,
    val agentId: String,
    val userId: String?,
    val messageType: MessageType,
    val role: MessageRole,
    val content: String,
    val metadata: Map<String, String>,
    val tokenCount: Int?,
    val costCents: Int?,
    val parentMessageId: String?,
    val createdAt: String
)

@Serializable
data class CreateMessageRequest(
    val content: String,
    val messageType: MessageType = MessageType.USER,
    val role: MessageRole = MessageRole.USER,
    val metadata: Map<String, String> = emptyMap(),
    val parentMessageId: String?
)

@Serializable
enum class MessageType {
    @SerialName("USER") USER,
    @SerialName("AGENT") AGENT,
    @SerialName("SYSTEM") SYSTEM,
    @SerialName("TOOL_CALL") TOOL_CALL,
    @SerialName("TOOL_RESULT") TOOL_RESULT
}

@Serializable
enum class MessageRole {
    @SerialName("USER") USER,
    @SerialName("ASSISTANT") ASSISTANT,
    @SerialName("SYSTEM") SYSTEM,
    @SerialName("TOOL") TOOL
}