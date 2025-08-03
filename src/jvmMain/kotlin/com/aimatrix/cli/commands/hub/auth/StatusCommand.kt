package com.aimatrix.cli.commands.hub.auth

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class StatusCommand : CliktCommand(
    name = "status",
    help = "Check AMX-Hub authentication status"
) {
    private val terminal = Terminal()
    
    override fun run() = runBlocking {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(yellow("Not logged in to AMX-Hub"))
            terminal.println(gray("Run 'amx hub auth login' to authenticate"))
            return@runBlocking
        }
        
        terminal.println(table {
            header {
                row("Property", "Value")
            }
            body {
                row("Status", green("Logged in"))
                row("Host", config.host)
                row("Username", config.user.username)
                row("Email", config.user.email)
                row("Full Name", config.user.fullName)
            }
        })
        
        // Try to validate the token
        try {
            val client = AmxHubClient(config.host, config.accessToken)
            // Make a simple API call to verify the token is still valid
            client.listWorkspaces()
            terminal.println(green("✓ Authentication token is valid"))
        } catch (e: Exception) {
            terminal.println(red("✗ Authentication token may be expired"))
            terminal.println(gray("Run 'amx hub auth refresh' to refresh your token"))
        }
    }
}