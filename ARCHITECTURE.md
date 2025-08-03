# AIMatrix CLI Architecture

This document provides a comprehensive overview of the AIMatrix CLI architecture, design patterns, and implementation details.

## Table of Contents

1. [Overview](#overview)
2. [Architecture Principles](#architecture-principles)
3. [System Components](#system-components)
4. [Layer Architecture](#layer-architecture)
5. [Design Patterns](#design-patterns)
6. [Data Flow](#data-flow)
7. [Performance Considerations](#performance-considerations)
8. [Security Architecture](#security-architecture)
9. [Extensibility](#extensibility)
10. [Testing Strategy](#testing-strategy)

## Overview

AIMatrix CLI is built using Kotlin Multiplatform, enabling native compilation for multiple operating systems while sharing core business logic. The architecture follows clean architecture principles with clear separation of concerns and dependency inversion.

### Key Architectural Goals

- **Multi-Platform Support**: Single codebase targeting JVM, Native (Linux, macOS, Windows)
- **Provider Agnostic**: Unified interface for multiple LLM providers
- **Performance Optimized**: Intelligent caching, lazy loading, and resource management
- **Extensible**: Plugin system for tools, providers, and UI components
- **Resilient**: Circuit breakers, retry logic, and graceful error handling
- **Observable**: Comprehensive metrics, logging, and monitoring

## Core Design Principles

1. **Multi-Model AI Integration**: Support for multiple LLM providers (Gemini, Claude, GPT, DeepSeek, Qwen, Grok, etc.) with intelligent selection
2. **Performance-Based Learning**: Tracks and learns from model performance to optimize future selections
3. **Multi-Agent Orchestration**: Support for running multiple AI agents in parallel with centralized management
4. **Cross-Platform Native Shell**: Native shell execution across Linux, macOS, and Windows
5. **Reactive Architecture**: Built on Kotlin Coroutines and Flow for responsive, non-blocking operations
6. **Modular Tool System**: Extensible tool registry allowing easy addition of new capabilities
7. **Session Management**: Advanced session handling with suspend/resume capabilities
8. **Language Server Protocol**: Deep code intelligence through LSP integration

## Architecture Layers

### 1. CLI Layer (Presentation)
- **Clikt Framework**: Command parsing and routing
- **Interactive Terminal UI**: Rich terminal interface with syntax highlighting
- **Command Processors**: Slash commands, at-commands, shell integration
- **Theme System**: Customizable color schemes and formatting

### 2. Agent Orchestration Layer
- **AgentManager**: Central coordinator for multiple agent instances
- **AgentContext**: Isolated execution context for each agent
- **WorkflowEngine**: Manages agent workflows and dependencies
- **ProcessPool**: Manages background processes with lifecycle control

Key Classes:
```kotlin
interface Agent {
    suspend fun execute(context: AgentContext): AgentResult
    suspend fun suspend()
    suspend fun resume()
    suspend fun stop()
}

class AgentManager {
    fun launchAgent(config: AgentConfig): AgentHandle
    fun listAgents(): List<AgentInfo>
    suspend fun suspendAgent(id: String)
    suspend fun resumeAgent(id: String)
    suspend fun stopAgent(id: String)
}
```

### 3. Core Services Layer
- **Multi-LLM Integration**: Unified interface for multiple AI providers
  - LLMProvider abstraction for all models
  - Intelligent model selection based on task type
  - Performance analytics and tracking
  - Cost optimization
- **ToolRegistry**: Dynamic tool registration and execution
- **FileDiscoveryService**: Intelligent file system navigation
- **GitService**: Version control integration
- **ShellExecutionService**: Cross-platform shell command execution
- **LSP Integration**: Language Server Protocol for code intelligence

### 4. Shell Abstraction Layer
- **NativeShell**: Platform-specific shell implementations
- **ProcessManager**: Process lifecycle management
- **EnvironmentManager**: Environment variable handling
- **StreamHandler**: I/O stream management for processes

Key Components:
```kotlin
interface NativeShell {
    suspend fun execute(command: String, env: Map<String, String>): ShellResult
    fun createInteractiveSession(): InteractiveShell
    fun detectShellType(): ShellType
}

class CrossPlatformShell {
    fun getShell(): NativeShell = when(Platform.current) {
        Platform.LINUX -> LinuxShell()
        Platform.MACOS -> MacOSShell()
        Platform.WINDOWS -> WindowsShell()
    }
}
```

### 5. Tool System
Tools are modular components that agents can use:

- **FileTools**: Read, write, edit, glob, grep
- **WebTools**: Web fetch, search
- **MemoryTools**: Context management
- **MCPTools**: Model Context Protocol integration
- **ShellTools**: Command execution

### 6. Communication Layer
- **WebSocket Server**: Real-time agent communication
- **MCP Client/Server**: Model Context Protocol support
- **IDE Integration**: VSCode, IntelliJ, etc.

## AI System Architecture

### Multi-LLM Provider Support
```kotlin
interface LLMProvider {
    suspend fun generateContent(request: GenerateContentRequest): GenerateContentResponse
    fun streamContent(request: GenerateContentRequest): Flow<StreamUpdate>
    suspend fun countTokens(content: String): TokenCount
}
```

Supported providers:
- **Google Gemini**: Best for code generation, function calling
- **Anthropic Claude**: Best for code review, technical writing
- **OpenAI GPT**: Best for creative tasks, general purpose
- **DeepSeek**: Specialized for code completion
- **Alibaba Qwen**: Best for multilingual tasks
- **xAI Grok**: Best for conversational AI
- **Cohere, Llama, Mistral**: Additional options

### Intelligent Model Selection
```kotlin
class IntelligentModelSelector {
    suspend fun selectModel(request: ModelSelectionRequest): ModelSelection {
        // Analyzes context, task type, and historical performance
        // Returns optimal model with reasoning
    }
}
```

Features:
- **Performance Tracking**: Success rates, latency, cost per task type
- **Adaptive Learning**: Improves selection over time
- **Context Analysis**: LSP integration for code tasks
- **Multiple Strategies**: Best score, weighted random, epsilon-greedy, Thompson sampling

### Performance Analytics
```kotlin
class PerformanceAnalytics {
    suspend fun startTask(taskId: String, taskType: TaskType): TaskTracker
    suspend fun getModelStats(providerId: String, modelId: String): ModelPerformanceStats
    suspend fun getBestModelForTask(taskType: TaskType): ModelRecommendation
}
```

Metrics tracked:
- Success rate by task type
- Average latency
- Token usage and costs
- Error patterns
- User feedback ratings

### LSP Integration
Enhanced code intelligence through Language Server Protocol:
- Real-time diagnostics
- Code completions
- Symbol navigation
- Refactoring support
- Multi-language support (100+ languages)

## Key Features Implementation

### 1. Parallel Agent Execution
```kotlin
class ParallelAgentExecutor {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    fun launchAgents(configs: List<AgentConfig>): List<Deferred<AgentResult>> {
        return configs.map { config ->
            scope.async {
                val agent = AgentFactory.create(config)
                agent.execute(createContext(config))
            }
        }
    }
}
```

### 2. Session Management
- Persistent session state
- Hot reload capabilities
- Session sharing and collaboration
- Checkpoint and restore

### 3. Workflow Automation
- YAML/JSON workflow definitions
- Conditional execution
- Event-driven triggers
- Scheduled tasks

## Platform-Specific Implementations

### macOS
- Uses `/bin/zsh` or `/bin/bash`
- FSEvents for file watching
- Native keychain integration

### Linux
- Supports bash, zsh, fish
- inotify for file watching
- Secret service integration

### Windows
- PowerShell and CMD support
- Windows file system events
- Credential manager integration

## Data Flow

1. User inputs command → Clikt parses → Command handler
2. Command handler → Agent creation → Agent execution
3. Agent → Tool execution → Native shell/API calls
4. Results → Stream processing → Terminal display
5. Background agents → Event system → UI updates

## Security Considerations

- Sandboxed agent execution
- Permission system for tool access
- Secure credential storage
- Audit logging for all operations

## Performance Optimizations

- Lazy loading of tools and services
- Connection pooling for API calls
- Efficient file system caching
- Stream-based processing for large outputs

## Extension Points

1. **Custom Tools**: Implement `Tool` interface
2. **Custom Agents**: Extend `Agent` base class
3. **Shell Adapters**: Implement `NativeShell`
4. **UI Themes**: Create custom color schemes
5. **Workflow Steps**: Add custom workflow actions

## Future Enhancements

1. **Distributed Agents**: Run agents across multiple machines
2. **Agent Marketplace**: Share and download agent configurations
3. **Visual Workflow Editor**: GUI for creating workflows
4. **Plugin System**: Dynamic loading of extensions
5. **Cloud Integration**: Seamless cloud provider integration