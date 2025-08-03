package com.aimatrix.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption

class AmxCommand : CliktCommand(
    name = "amx",
    help = "AIMatrix CLI - Multi-LLM Command Line Interface with AMX-Hub integration"
) {
    init {
        versionOption("1.0.0")
        subcommands(
            HubCommand(),
            ChatCommand(),
            ConfigCommand(),
            StatusCommand()
        )
    }

    override fun run() {
        // This is called when 'amx' is run without subcommands
        // We can show a brief help or status here
    }
}