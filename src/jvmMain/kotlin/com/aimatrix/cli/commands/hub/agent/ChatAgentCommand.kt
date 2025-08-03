package com.aimatrix.cli.commands.hub.agent

import com.aimatrix.cli.config.ConfigManager
import com.aimatrix.cli.hub.AmxHubClient
import com.aimatrix.cli.hub.models.CreateAgentSessionRequest
import com.aimatrix.cli.hub.models.CreateMessageRequest
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

class ChatAgentCommand : CliktCommand(
    name = "chat",
    help = "Start an interactive chat session with an agent"
) {
    private val terminal = Terminal()
    private val agentId by argument(help = "Agent ID")
    private val message by option("-m", "--message", help = "Send a single message (non-interactive)")
    private val stream by option("--stream", help = "Stream responses").flag(default = true)
    
    override fun run() = runBlocking {
        val config = ConfigManager.getAmxHubConfig()
        
        if (config == null) {
            terminal.println(red("Not logged in to AMX-Hub"))
            terminal.println(gray("Run 'amx hub auth login' to authenticate"))
            return@runBlocking
        }
        
        try {
            val client = AmxHubClient(config.host, config.accessToken)
            
            // Get agent info
            terminal.println(blue("Connecting to agent..."))
            val agent = client.getAgent(agentId)
            
            // Create a new session
            val session = client.createSession(
                agentId,
                CreateAgentSessionRequest(
                    agentId = agentId,
                    title = "CLI Chat Session",
                    description = "Interactive chat from AMX CLI"
                )
            )
            
            terminal.println(green("✓ Connected to ${agent.displayName ?: agent.name}"))
            terminal.println(gray("Session ID: ${session.id}"))
            terminal.println()
            
            if (message != null) {
                // Single message mode
                sendMessage(client, session.id, message!!)
            } else {
                // Interactive mode
                terminal.println(yellow("Type 'exit' or press Ctrl+C to end the chat"))
                terminal.println()
                
                while (true) {
                    val input = terminal.prompt(brightBlue("> ")) ?: break
                    
                    if (input.lowercase() in listOf("exit", "quit", "bye")) {
                        terminal.println(gray("Ending chat session..."))
                        break
                    }
                    
                    if (input.isNotBlank()) {
                        sendMessage(client, session.id, input)
                    }
                }
            }
            
        } catch (e: Exception) {
            terminal.println(red("✗ Failed to start chat: ${e.message}"))
        }
    }
    
    private suspend fun sendMessage(client: AmxHubClient, sessionId: String, content: String) {
        try {
            // Send the message
            val message = client.sendMessage(
                sessionId,
                CreateMessageRequest(
                    content = content,
                    parentMessageId = null
                )
            )
            
            // Simulate streaming response (in real implementation, this would use WebSocket or SSE)
            terminal.print(green("Assistant: "))
            
            if (stream) {
                // Simulate streaming effect
                val response = "I received your message: \"$content\". This is a placeholder response from the agent."
                for (char in response) {
                    terminal.print(char.toString())
                    delay(20) // Simulate typing delay
                }
                terminal.println()
            } else {
                terminal.println("Response would appear here")
            }
            
            terminal.println()
            
        } catch (e: Exception) {
            terminal.println(red("✗ Failed to send message: ${e.message}"))
        }
    }
}