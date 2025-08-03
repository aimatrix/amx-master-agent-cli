package com.aimatrix.cli.commands.hub

import com.aimatrix.cli.commands.hub.workspace.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class WorkspaceCommand : CliktCommand(
    name = "workspace",
    help = "Manage AMX-Hub workspaces"
) {
    init {
        subcommands(
            ListCommand(),
            CreateCommand(),
            ViewCommand(),
            DeleteCommand(),
            MembersCommand(),
            ActivityCommand(),
            CloneCommand()
        )
    }

    override fun run() {
        // Parent command
    }
}