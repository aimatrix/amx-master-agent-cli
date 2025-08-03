package com.aimatrix.cli.commands.hub.agent

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class CreateAgentCommand : CliktCommand(
    name = "create",
    help = "Create a new agent"
) {
    private val name by argument(help = "Agent name")
    
    override fun run() {
        // TODO: Implement agent creation
    }
}