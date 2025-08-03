package com.aimatrix.cli.config

import com.aimatrix.cli.hub.models.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ConfigManager {
    private val configDir = File(System.getProperty("user.home"), ".aimatrix")
    private val amxHubConfigFile = File(configDir, "amx-hub.json")
    private val llmProvidersFile = File(configDir, "providers.json")
    private val settingsFile = File(configDir, "settings.json")
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    init {
        // Ensure config directory exists
        if (!configDir.exists()) {
            configDir.mkdirs()
        }
    }
    
    // AMX-Hub configuration
    fun saveAmxHubAuth(host: String, token: String, refreshToken: String, user: UserDto) {
        val config = AmxHubConfig(
            host = host,
            accessToken = token,
            refreshToken = refreshToken,
            user = AmxHubUser(
                id = user.id,
                username = user.username,
                email = user.email,
                fullName = user.fullName
            )
        )
        
        amxHubConfigFile.writeText(json.encodeToString(config))
    }
    
    fun getAmxHubConfig(): AmxHubConfig? {
        if (!amxHubConfigFile.exists()) return null
        
        return try {
            json.decodeFromString<AmxHubConfig>(amxHubConfigFile.readText())
        } catch (e: Exception) {
            null
        }
    }
    
    fun clearAmxHubAuth() {
        if (amxHubConfigFile.exists()) {
            amxHubConfigFile.delete()
        }
    }
    
    // LLM Provider configuration
    fun saveLLMProviders(providers: List<LLMProviderConfig>) {
        llmProvidersFile.writeText(json.encodeToString(providers))
    }
    
    fun getLLMProviders(): List<LLMProviderConfig> {
        if (!llmProvidersFile.exists()) return emptyList()
        
        return try {
            json.decodeFromString<List<LLMProviderConfig>>(llmProvidersFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addLLMProvider(provider: LLMProviderConfig) {
        val providers = getLLMProviders().toMutableList()
        providers.removeAll { it.id == provider.id }
        providers.add(provider)
        saveLLMProviders(providers)
    }
    
    // Settings configuration
    fun saveSettings(settings: AppSettings) {
        settingsFile.writeText(json.encodeToString(settings))
    }
    
    fun getSettings(): AppSettings {
        if (!settingsFile.exists()) {
            return AppSettings()
        }
        
        return try {
            json.decodeFromString<AppSettings>(settingsFile.readText())
        } catch (e: Exception) {
            AppSettings()
        }
    }
}

@Serializable
data class AmxHubConfig(
    val host: String,
    val accessToken: String,
    val refreshToken: String,
    val user: AmxHubUser
)

@Serializable
data class AmxHubUser(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String
)

@Serializable
data class LLMProviderConfig(
    val id: String,
    val provider: String,
    val name: String,
    val apiKey: String,
    val baseUrl: String? = null,
    val models: List<String> = emptyList(),
    val isDefault: Boolean = false,
    val isEnabled: Boolean = true
)

@Serializable
data class AppSettings(
    val theme: String = "dark",
    val defaultProvider: String? = null,
    val maxTokens: Int = 2000,
    val temperature: Double = 0.7,
    val streamResponses: Boolean = true,
    val saveHistory: Boolean = true,
    val historyPath: String = "~/.aimatrix/history"
)