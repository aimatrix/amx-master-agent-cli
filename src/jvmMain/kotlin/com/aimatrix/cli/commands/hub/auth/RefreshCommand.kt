package com.aimatrix.cli.commands.hub.auth

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class RefreshCommand : CliktCommand(
    name = "refresh",
    help = "Refresh AMX-Hub authentication token"
) {
    private val terminal = Terminal()
    
    override fun run() = runBlocking {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(red("Not logged in to AMX-Hub"))
            terminal.println(gray("Run 'amx hub auth login' to authenticate"))
            return@runBlocking
        }
        
        try {
            terminal.println(blue("Refreshing authentication token..."))
            
            val client = AmxHubClient(config.host)
            val response = client.refreshToken(config.refreshToken)
            
            // Update stored tokens
            ConfigManager.saveAmxHubAuth(
                host = config.host,
                token = response.accessToken,
                refreshToken = response.refreshToken,
                user = config.user.let {
                    com.aimatrix.cli.hub.models.UserDto(
                        id = it.id,
                        username = it.username,
                        email = it.email,
                        fullName = it.fullName,
                        avatarUrl = null,
                        bio = null,
                        organizationId = null,
                        roles = emptySet(),
                        permissions = emptySet(),
                        status = com.aimatrix.cli.hub.models.UserStatus.ACTIVE,
                        emailVerified = true,
                        mfaEnabled = false,
                        createdAt = "",
                        lastLoginAt = null
                    )
                }
            )
            
            terminal.println(green("✓ Token refreshed successfully"))
            
        } catch (e: Exception) {
            terminal.println(red("✗ Failed to refresh token: ${e.message}"))
            terminal.println(gray("You may need to login again: 'amx hub auth login'"))
        }
    }
}