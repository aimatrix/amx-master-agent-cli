package com.aimatrix.cli.hub

import com.aimatrix.cli.hub.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AmxHubClient(
    private val baseUrl: String,
    private var authToken: String? = null
) {
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
    
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
        
        install(Logging) {
            level = LogLevel.INFO
        }
        
        defaultRequest {
            contentType(ContentType.Application.Json)
            authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
        
        expectSuccess = false
    }
    
    fun setAuthToken(token: String) {
        authToken = token
    }
    
    // Auth endpoints
    suspend fun login(request: LoginRequest): LoginResponse {
        val response = client.post("$baseUrl/api/auth/login") {
            setBody(request)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Login failed: ${response.status.description}")
        }
        
        return response.body()
    }
    
    suspend fun logout(): Boolean {
        val response = client.post("$baseUrl/api/auth/logout")
        return response.status.isSuccess()
    }
    
    suspend fun refreshToken(refreshToken: String): TokenResponse {
        val response = client.post("$baseUrl/api/auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken))
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Token refresh failed: ${response.status.description}")
        }
        
        return response.body()
    }
    
    // Workspace endpoints
    suspend fun listWorkspaces(): List<WorkspaceDto> {
        val response = client.get("$baseUrl/api/v1/workspaces")
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to list workspaces: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<List<WorkspaceDto>> = response.body()
        return apiResponse.data ?: emptyList()
    }
    
    suspend fun createWorkspace(request: CreateWorkspaceRequest): WorkspaceDto {
        val response = client.post("$baseUrl/api/v1/workspaces") {
            setBody(request)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create workspace: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<WorkspaceDto> = response.body()
        return apiResponse.data ?: throw Exception("No workspace data returned")
    }
    
    suspend fun getWorkspace(workspaceId: String): WorkspaceDto {
        val response = client.get("$baseUrl/api/v1/workspaces/$workspaceId")
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get workspace: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<WorkspaceDto> = response.body()
        return apiResponse.data ?: throw Exception("No workspace data returned")
    }
    
    suspend fun deleteWorkspace(workspaceId: String): Boolean {
        val response = client.delete("$baseUrl/api/v1/workspaces/$workspaceId")
        return response.status.isSuccess()
    }
    
    // Agent endpoints
    suspend fun listAgents(workspaceId: String? = null): List<AgentDto> {
        val url = if (workspaceId != null) {
            "$baseUrl/api/v1/agents/workspace/$workspaceId"
        } else {
            "$baseUrl/api/v1/agents/my-agents"
        }
        
        val response = client.get(url)
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to list agents: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<List<AgentDto>> = response.body()
        return apiResponse.data ?: emptyList()
    }
    
    suspend fun createAgent(request: CreateAgentRequest): AgentDto {
        val response = client.post("$baseUrl/api/v1/agents") {
            setBody(request)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create agent: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<AgentDto> = response.body()
        return apiResponse.data ?: throw Exception("No agent data returned")
    }
    
    suspend fun getAgent(agentId: String): AgentDto {
        val response = client.get("$baseUrl/api/v1/agents/$agentId")
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to get agent: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<AgentDto> = response.body()
        return apiResponse.data ?: throw Exception("No agent data returned")
    }
    
    suspend fun startAgent(agentId: String): AgentDto {
        val response = client.post("$baseUrl/api/v1/agents/$agentId/start")
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to start agent: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<AgentDto> = response.body()
        return apiResponse.data ?: throw Exception("No agent data returned")
    }
    
    suspend fun stopAgent(agentId: String): AgentDto {
        val response = client.post("$baseUrl/api/v1/agents/$agentId/stop")
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to stop agent: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<AgentDto> = response.body()
        return apiResponse.data ?: throw Exception("No agent data returned")
    }
    
    suspend fun deleteAgent(agentId: String): Boolean {
        val response = client.delete("$baseUrl/api/v1/agents/$agentId")
        return response.status.isSuccess()
    }
    
    // Session endpoints
    suspend fun createSession(agentId: String, request: CreateAgentSessionRequest): AgentSessionDto {
        val response = client.post("$baseUrl/api/v1/agents/$agentId/sessions") {
            setBody(request)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to create session: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<AgentSessionDto> = response.body()
        return apiResponse.data ?: throw Exception("No session data returned")
    }
    
    suspend fun sendMessage(sessionId: String, request: CreateMessageRequest): SessionMessageDto {
        val response = client.post("$baseUrl/api/v1/sessions/$sessionId/messages") {
            setBody(request)
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to send message: ${response.status.description}")
        }
        
        val apiResponse: ApiResponse<SessionMessageDto> = response.body()
        return apiResponse.data ?: throw Exception("No message data returned")
    }
    
    fun close() {
        client.close()
    }
}