package com.aimatrix.cli.commands.hub.agent

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.aimatrix.cli.hub.models.AgentStatus
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class ListAgentsCommand : CliktCommand(
    name = "list",
    help = "List agents in a workspace or all your agents"
) {
    private val terminal = Terminal()
    private val workspace by argument(help = "Workspace ID (optional)").optional()
    private val all by option("-a", "--all", help = "List all your agents across workspaces").flag()
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
            val agents = if (workspace != null && !all) {
                client.listAgents(workspace)
            } else {
                client.listAgents(null)
            }
            
            if (agents.isEmpty()) {
                terminal.println(yellow("No agents found"))
                terminal.println(gray("Create an agent with 'amx hub agent create <name>'"))
                return@runBlocking
            }
            
            if (json) {
                // TODO: Output JSON format
                terminal.println("JSON output not yet implemented")
            } else {
                terminal.println(table {
                    header {
                        row("ID", "Name", "Status", "Type", "Sessions", "Last Active")
                    }
                    body {
                        agents.forEach { agent ->
                            row(
                                agent.id.take(8),
                                agent.displayName ?: agent.name,
                                when(agent.status) {
                                    AgentStatus.RUNNING -> green("running")
                                    AgentStatus.STOPPED -> gray("stopped")
                                    AgentStatus.FAILED -> red("failed")
                                    AgentStatus.STARTING -> yellow("starting")
                                    AgentStatus.STOPPING -> yellow("stopping")
                                    else -> gray(agent.status.name.lowercase())
                                },
                                agent.agentTypeId.take(8),
                                agent.statistics.activeSessions.toString(),
                                agent.lastActivityAt.take(10)
                            )
                        }
                    }
                    captionBottom(gray("Total: ${agents.size} agents"))
                })
            }
            
        } catch (e: Exception) {
            terminal.println(red("✗ Failed to list agents: ${e.message}"))
        }
    }
}