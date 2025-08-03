package com.aimatrix.cli.commands.hub.auth

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class LogoutCommand : CliktCommand(
    name = "logout",
    help = "Logout from AMX-Hub"
) {
    private val terminal = Terminal()
    
    override fun run() = runBlocking {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(yellow("Not logged in to AMX-Hub"))
            return@runBlocking
        }
        
        try {
            val client = AmxHubClient(config.host, config.accessToken)
            client.logout()
            
            ConfigManager.clearAmxHubAuth()
            terminal.println(green("✓ Successfully logged out from AMX-Hub"))
            
        } catch (e: Exception) {
            // Even if the server logout fails, clear local credentials
            ConfigManager.clearAmxHubAuth()
            terminal.println(yellow("⚠ Logged out locally (server logout failed: ${e.message})"))
        }
    }
}