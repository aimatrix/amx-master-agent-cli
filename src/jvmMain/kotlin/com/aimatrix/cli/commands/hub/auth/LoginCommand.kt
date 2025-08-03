package com.aimatrix.cli.commands.hub.auth

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.aimatrix.cli.hub.models.LoginRequest
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class LoginCommand : CliktCommand(
    name = "login",
    help = "Login to AMX-Hub"
) {
    private val terminal = Terminal()
    private val username by option("-u", "--username", help = "Username or email")
    private val password by option("-p", "--password", help = "Password (will prompt if not provided)")
    private val host by option("-h", "--host", help = "AMX-Hub host URL").prompt(
        default = "https://hub.aimatrix.com",
        text = "AMX-Hub host",
        hideInput = false
    )
    private val interactive by option("-i", "--interactive", help = "Use browser-based login").flag()
    
    override fun run() = runBlocking {
        terminal.println(blue("Logging in to AMX-Hub at $host"))
        
        if (interactive) {
            performBrowserLogin()
        } else {
            performCredentialLogin()
        }
    }
    
    private suspend fun performCredentialLogin() {
        val user = username ?: terminal.prompt("Username or email") ?: return
        val pass = password ?: terminal.prompt("Password", hideInput = true) ?: return
        
        try {
            val client = AmxHubClient(host)
            val response = client.login(LoginRequest(user, pass))
            
            // Save credentials and tokens
            ConfigManager.saveAmxHubAuth(
                host = host,
                token = response.accessToken,
                refreshToken = response.refreshToken,
                user = response.user
            )
            
            terminal.println(green("✓ Successfully logged in as ${response.user.username}"))
            terminal.println(gray("Authentication saved to ~/.aimatrix/amx-hub.json"))
            
        } catch (e: Exception) {
            terminal.println(red("✗ Login failed: ${e.message}"))
        }
    }
    
    private suspend fun performBrowserLogin() {
        terminal.println(yellow("Opening browser for authentication..."))
        // TODO: Implement OAuth2 flow
        terminal.println(red("Browser-based login not yet implemented"))
    }
}