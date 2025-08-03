package com.aimatrix.cli.commands.hub.workspace

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class ListCommand : CliktCommand(
    name = "list",
    help = "List AMX-Hub workspaces"
) {
    private val terminal = Terminal()
    private val limit by option("-n", "--limit", help = "Maximum number of workspaces to list").default("30")
    private val public by option("--public", help = "Show only public workspaces").flag()
    private val json by option("--json", help = "Output in JSON format").flag()
    
    override fun run() = runBlocking {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(red("Not logged in to AMX-Hub"))
            terminal.println(gray("Run 'amx hub auth login' to authenticate"))
            return@runBlocking
        }
        
        try {
            val client = AmxHubClient(config.host, config.accessToken)
            val workspaces = client.listWorkspaces()
            
            if (workspaces.isEmpty()) {
                terminal.println(yellow("No workspaces found"))
                terminal.println(gray("Create a workspace with 'amx hub workspace create <name>'"))
                return@runBlocking
            }
            
            if (json) {
                // TODO: Output JSON format
                terminal.println("JSON output not yet implemented")
            } else {
                terminal.println(table {
                    header {
                        row("ID", "Name", "Visibility", "Agents", "Members", "Activity")
                    }
                    body {
                        workspaces.take(limit.toInt()).forEach { workspace ->
                            row(
                                workspace.id.take(8),
                                workspace.name,
                                when(workspace.visibility) {
                                    com.aimatrix.cli.hub.models.WorkspaceVisibility.PUBLIC -> green("public")
                                    com.aimatrix.cli.hub.models.WorkspaceVisibility.PRIVATE -> yellow("private")
                                    com.aimatrix.cli.hub.models.WorkspaceVisibility.ORGANIZATION -> blue("org")
                                },
                                workspace.agentCount.toString(),
                                workspace.memberCount.toString(),
                                workspace.lastActivityAt?.take(10) ?: gray("never")
                            )
                        }
                    }
                    captionBottom(gray("Showing ${workspaces.take(limit.toInt()).size} of ${workspaces.size} workspaces"))
                })
            }
            
        } catch (e: Exception) {
            terminal.println(red("✗ Failed to list workspaces: ${e.message}"))
        }
    }
}