package com.aimatrix.cli.commands.hub.workspace

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.aimatrix.cli.hub.models.CreateWorkspaceRequest
import com.aimatrix.cli.hub.models.WorkspaceVisibility
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class CreateCommand : CliktCommand(
    name = "create",
    help = "Create a new AMX-Hub workspace"
) {
    private val terminal = Terminal()
    private val name by argument(help = "Workspace name")
    private val description by option("-d", "--description", help = "Workspace description")
    private val public by option("--public", help = "Make workspace public").flag()
    private val tags = mutableListOf<String>()
    private val interactive by option("-i", "--interactive", help = "Interactive mode").flag()
    
    override fun run() = runBlocking {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(red("Not logged in to AMX-Hub"))
            terminal.println(gray("Run 'amx hub auth login' to authenticate"))
            return@runBlocking
        }
        
        try {
            val visibility = if (public) WorkspaceVisibility.PUBLIC else WorkspaceVisibility.PRIVATE
            
            val desc = if (interactive && description == null) {
                terminal.prompt("Description (optional)") ?: ""
            } else {
                description ?: ""
            }
            
            terminal.println(blue("Creating workspace '$name'..."))
            
            val client = AmxHubClient(config.host, config.accessToken)
            val workspace = client.createWorkspace(
                CreateWorkspaceRequest(
                    name = name,
                    description = desc,
                    visibility = visibility,
                    tags = tags.toSet()
                )
            )
            
            terminal.println(green("✓ Created workspace successfully"))
            terminal.println()
            terminal.println("Workspace ID: ${workspace.id}")
            terminal.println("URL: ${config.host}/workspaces/${workspace.id}")
            terminal.println()
            terminal.println(gray("To clone this workspace locally:"))
            terminal.println(gray("  amx hub workspace clone ${workspace.id}"))
            
        } catch (e: Exception) {
            terminal.println(red("✗ Failed to create workspace: ${e.message}"))
        }
    }
}