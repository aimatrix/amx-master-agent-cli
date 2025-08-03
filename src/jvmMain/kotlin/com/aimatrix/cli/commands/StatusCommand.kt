package com.aimatrix.cli.commands

import com.aimatrix.cli.config.ConfigManager
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal

class StatusCommand : CliktCommand(
    name = "status",
    help = "Show AIMatrix CLI status and configuration"
) {
    private val terminal = Terminal()
    
    override fun run() {
        terminal.println(magenta("╔═══════════════════════════════════════════╗"))
        terminal.println(magenta("║        AIMatrix CLI Status v1.0.0         ║"))
        terminal.println(magenta("╚═══════════════════════════════════════════╝"))
        terminal.println()
        
        // AMX-Hub Status
        val amxHub = ConfigManager.getAmxHubConfig()
        terminal.println(table {
            header {
                row(cyan("AMX-Hub"), "Status")
            }
            body {
                if (amxHub != null) {
                    row("Connection", green("Connected"))
                    row("Host", amxHub.host)
                    row("User", amxHub.user.username)
                } else {
                    row("Connection", red("Not connected"))
                    row("Host", gray("--"))
                    row("User", gray("--"))
                }
            }
        })
        
        terminal.println()
        
        // LLM Providers Status
        val providers = ConfigManager.getLLMProviders()
        terminal.println(table {
            header {
                row(cyan("LLM Providers"), "Status", "Default")
            }
            body {
                if (providers.isEmpty()) {
                    row(gray("No providers configured"), gray("--"), gray("--"))
                } else {
                    providers.forEach { provider ->
                        row(
                            provider.name,
                            if (provider.isEnabled) green("Enabled") else gray("Disabled"),
                            if (provider.isDefault) green("✓") else gray("--")
                        )
                    }
                }
            }
        })
        
        terminal.println()
        terminal.println(gray("Run 'amx config --list' to see all configuration"))
        terminal.println(gray("Run 'amx hub auth login' to connect to AMX-Hub"))
    }
}