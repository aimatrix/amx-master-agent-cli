package com.aimatrix.cli.commands

import com.aimatrix.cli.config.ConfigManager
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal

class ConfigCommand : CliktCommand(
    name = "config",
    help = "Manage AIMatrix CLI configuration"
) {
    private val terminal = Terminal()
    private val key by argument(help = "Configuration key").optional()
    private val value by argument(help = "Configuration value").optional()
    private val list by option("-l", "--list", help = "List all configuration").flag()
    private val get by option("-g", "--get", help = "Get a configuration value").flag()
    
    override fun run() {
        when {
            list -> listConfig()
            get && key != null -> getConfig(key!!)
            key != null && value != null -> setConfig(key!!, value!!)
            else -> terminal.println(yellow("Use --list to see all configuration or provide key and value to set"))
        }
    }
    
    private fun listConfig() {
        terminal.println(blue("AIMatrix CLI Configuration:"))
        terminal.println()
        
        val settings = ConfigManager.getSettings()
        terminal.println("Theme: ${settings.theme}")
        terminal.println("Default Provider: ${settings.defaultProvider ?: "none"}")
        terminal.println("Max Tokens: ${settings.maxTokens}")
        terminal.println("Temperature: ${settings.temperature}")
        terminal.println("Stream Responses: ${settings.streamResponses}")
        terminal.println("Save History: ${settings.saveHistory}")
        
        val amxHub = ConfigManager.getAmxHubConfig()
        if (amxHub != null) {
            terminal.println()
            terminal.println(green("AMX-Hub:"))
            terminal.println("  Host: ${amxHub.host}")
            terminal.println("  User: ${amxHub.user.username}")
        }
        
        val providers = ConfigManager.getLLMProviders()
        if (providers.isNotEmpty()) {
            terminal.println()
            terminal.println(green("LLM Providers:"))
            providers.forEach { provider ->
                terminal.println("  ${provider.name} (${provider.provider})")
            }
        }
    }
    
    private fun getConfig(key: String) {
        // TODO: Implement getting specific config values
        terminal.println("Get config not yet implemented")
    }
    
    private fun setConfig(key: String, value: String) {
        // TODO: Implement setting specific config values
        terminal.println("Set config not yet implemented")
    }
}