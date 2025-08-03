package com.aimatrix.cli.commands.hub.auth

import com.aimatrix.cli.config.ConfigManager
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal

class TokenCommand : CliktCommand(
    name = "token",
    help = "Display or manage AMX-Hub access tokens"
) {
    private val terminal = Terminal()
    private val showRefresh by option("-r", "--refresh", help = "Show refresh token").flag()
    private val raw by option("--raw", help = "Show raw token without formatting").flag()
    
    override fun run() {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(red("Not logged in to AMX-Hub"))
            return
        }
        
        if (raw) {
            if (showRefresh) {
                terminal.println(config.refreshToken)
            } else {
                terminal.println(config.accessToken)
            }
        } else {
            if (showRefresh) {
                terminal.println(blue("Refresh Token:"))
                terminal.println(gray("${config.refreshToken.take(20)}...${config.refreshToken.takeLast(20)}"))
            } else {
                terminal.println(blue("Access Token:"))
                terminal.println(gray("${config.accessToken.take(20)}...${config.accessToken.takeLast(20)}"))
            }
            terminal.println()
            terminal.println(yellow("⚠ Keep your tokens secure and never share them"))
        }
    }
}