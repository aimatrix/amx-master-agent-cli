package com.aimatrix.cli.commands.hub.workspace

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class MembersCommand : CliktCommand(
    name = "members",
    help = "Manage workspace members"
) {
    private val workspace by argument(help = "Workspace ID or name")
    
    override fun run() {
        // TODO: Implement workspace members management
    }
}