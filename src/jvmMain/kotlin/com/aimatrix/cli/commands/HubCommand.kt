package com.aimatrix.cli.commands

import com.aimatrix.cli.commands.hub.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class HubCommand : CliktCommand(
    name = "hub",
    help = "Interact with AMX-Hub platform"
) {
    init {
        subcommands(
            AuthCommand(),
            WorkspaceCommand(),
            AgentCommand(),
            SessionCommand(),
            ApiCommand()
        )
    }

    override fun run() {
        // This is called when 'amx hub' is run without subcommands
    }
}