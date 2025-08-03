package com.aimatrix.cli.commands.hub

import com.aimatrix.cli.commands.hub.agent.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class AgentCommand : CliktCommand(
    name = "agent",
    help = "Manage AMX-Hub agents"
) {
    init {
        subcommands(
            ListAgentsCommand(),
            CreateAgentCommand(),
            ViewAgentCommand(),
            StartAgentCommand(),
            StopAgentCommand(),
            DeleteAgentCommand(),
            ChatAgentCommand()
        )
    }

    override fun run() {
        // Parent command
    }
}