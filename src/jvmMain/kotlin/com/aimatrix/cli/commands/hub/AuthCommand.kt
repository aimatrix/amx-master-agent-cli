package com.aimatrix.cli.commands.hub

import com.aimatrix.cli.commands.hub.auth.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class AuthCommand : CliktCommand(
    name = "auth",
    help = "Manage AMX-Hub authentication"
) {
    init {
        subcommands(
            LoginCommand(),
            LogoutCommand(),
            StatusCommand(),
            TokenCommand(),
            RefreshCommand()
        )
    }

    override fun run() {
        // Parent command
    }
}