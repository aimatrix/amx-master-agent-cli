package com.aimatrix.cli.commands.hub.agent

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class StartAgentCommand : CliktCommand(
    name = "start",
    help = "Start an agent"
) {
    private val agent by argument(help = "Agent ID")
    
    override fun run() {
        // TODO: Implement agent start
    }
}