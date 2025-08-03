# AMX Master Agent CLI - Detailed Implementation Plan

## Overview
Convert Google's Gemini CLI (TypeScript) to AMX Master Agent CLI (Kotlin Multiplatform) with enhanced multi-agent capabilities. The executable will be `aimatrix` with both TUI (`aimatrix --tui`) and GUI (`aimatrix --gui`) modes.

## Branding Replacement
- All "gemini" references → "aimatrix" or "amx"
- Package names: `@google/gemini-cli` → `com.aimatrix.cli`
- Executable: `gemini` → `aimatrix`
- Config directory: `~/.gemini` → `~/.aimatrix`

---

## Phase 1: Core Infrastructure Setup (Week 1)

### 1.1 Project Configuration
- [ ] Update build.gradle.kts for native executable generation
- [ ] Configure shadowJar for `aimatrix` executable name
- [ ] Set up native image compilation (GraalVM optional)
- [ ] Create platform-specific launchers

### 1.2 Logging and Error Handling
```kotlin
// Convert from TypeScript
packages/core/src/core/logger.ts → src/commonMain/kotlin/com/aimatrix/amx/core/Logger.kt
```
- [ ] Implement Logger with levels (DEBUG, INFO, WARNING, ERROR)
- [ ] Create platform-specific console output handlers
- [ ] Add structured logging with JSON export option
- [ ] Implement error reporting system

### 1.3 Configuration Management
```kotlin
// Convert from TypeScript
packages/core/src/config/config.ts → src/commonMain/kotlin/com/aimatrix/amx/config/ConfigManager.kt
```
- [ ] Implement config file loading/saving with Okio
- [ ] Add environment variable overrides
- [ ] Create settings migration from Gemini format
- [ ] Implement secure credential storage

### 1.4 Telemetry System
```kotlin
// Convert from TypeScript
packages/core/src/telemetry/ → src/commonMain/kotlin/com/aimatrix/amx/telemetry/
```
- [ ] Port telemetry interfaces
- [ ] Implement opt-in telemetry collection
- [ ] Add performance metrics tracking
- [ ] Create telemetry exporters

---

## Phase 2: Tool System Implementation (Week 2-3)

### 2.1 File System Tools
```kotlin
// Convert from TypeScript
packages/core/src/tools/read-file.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/filesystem/ReadFileTool.kt
packages/core/src/tools/write-file.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/filesystem/WriteFileTool.kt
packages/core/src/tools/edit.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/filesystem/EditTool.kt
packages/core/src/tools/ls.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/filesystem/ListDirectoryTool.kt
packages/core/src/tools/glob.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/filesystem/GlobTool.kt
packages/core/src/tools/grep.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/filesystem/GrepTool.kt
```

Implementation tasks:
- [ ] ReadFileTool with streaming support
- [ ] WriteFileTool with atomic writes
- [ ] EditTool with diff generation
- [ ] ListDirectoryTool with filtering
- [ ] GlobTool with pattern matching
- [ ] GrepTool with regex support

### 2.2 Web Tools
```kotlin
// Convert from TypeScript
packages/core/src/tools/web-fetch.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/web/WebFetchTool.kt
packages/core/src/tools/web-search.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/web/WebSearchTool.kt
```
- [ ] WebFetchTool with Ktor client
- [ ] WebSearchTool with provider abstraction
- [ ] HTML to Markdown conversion
- [ ] Rate limiting and caching

### 2.3 Memory Tool
```kotlin
// Convert from TypeScript
packages/core/src/tools/memoryTool.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/memory/MemoryTool.kt
```
- [ ] Context memory management
- [ ] Memory import/export
- [ ] Memory search functionality

### 2.4 Shell Tool
```kotlin
// Convert from TypeScript
packages/core/src/tools/shell.ts → src/commonMain/kotlin/com/aimatrix/amx/tools/shell/ShellTool.kt
```
- [ ] Command execution with timeout
- [ ] Stream handling (stdout/stderr)
- [ ] Security validation
- [ ] Platform-specific implementations

---

## Phase 3: AI Integration (Week 3-4)

### 3.1 Gemini Client
```kotlin
// Convert from TypeScript
packages/core/src/core/client.ts → src/commonMain/kotlin/com/aimatrix/amx/ai/GeminiClient.kt
packages/core/src/core/geminiChat.ts → src/commonMain/kotlin/com/aimatrix/amx/ai/AiChat.kt
```
- [ ] HTTP client setup with Ktor
- [ ] Authentication handling
- [ ] Request/response models
- [ ] Streaming response support

### 3.2 Content Generation
```kotlin
// Convert from TypeScript
packages/core/src/core/contentGenerator.ts → src/commonMain/kotlin/com/aimatrix/amx/ai/ContentGenerator.kt
packages/core/src/core/turn.ts → src/commonMain/kotlin/com/aimatrix/amx/ai/Turn.kt
```
- [ ] Message formatting
- [ ] Tool call handling
- [ ] Context management
- [ ] Token counting

### 3.3 Prompt Management
```kotlin
// Convert from TypeScript
packages/core/src/core/prompts.ts → src/commonMain/kotlin/com/aimatrix/amx/ai/PromptManager.kt
```
- [ ] System prompts
- [ ] User prompt processing
- [ ] Prompt templates
- [ ] Context injection

---

## Phase 4: Shell and Process Management (Week 4-5)

### 4.1 Platform-Specific Shell Implementation
```kotlin
// Platform-specific implementations
src/jvmMain/kotlin/com/aimatrix/amx/shell/JvmShell.kt
src/nativeMain/kotlin/com/aimatrix/amx/shell/NativeShell.kt
src/macosMain/kotlin/com/aimatrix/amx/shell/MacOSShell.kt
src/linuxMain/kotlin/com/aimatrix/amx/shell/LinuxShell.kt
src/mingwMain/kotlin/com/aimatrix/amx/shell/WindowsShell.kt
```

For each platform:
- [ ] Shell detection (bash, zsh, powershell, etc.)
- [ ] Process creation and management
- [ ] Environment variable handling
- [ ] Signal handling (SIGINT, SIGTERM)
- [ ] Pipe and redirection support

### 4.2 Interactive Shell Sessions
- [ ] PTY allocation for interactive sessions
- [ ] Input/output stream management
- [ ] Session persistence
- [ ] Multi-session support

---

## Phase 5: TUI Implementation with Mordant (Week 5-6)

### 5.1 Core TUI Components
```kotlin
// Convert from TypeScript React components to Mordant
packages/cli/src/ui/App.tsx → src/jvmMain/kotlin/com/aimatrix/amx/tui/TuiApp.kt
packages/cli/src/ui/components/ → src/jvmMain/kotlin/com/aimatrix/amx/tui/components/
```

Components to implement:
- [ ] Main application loop
- [ ] Input prompt with auto-completion
- [ ] Chat message display
- [ ] Progress indicators
- [ ] Tool execution display
- [ ] Status bar
- [ ] Help system

### 5.2 TUI-Specific Features
- [ ] Keyboard shortcuts (Ctrl+C, Ctrl+L, etc.)
- [ ] Command history with search
- [ ] Syntax highlighting for code
- [ ] Table rendering
- [ ] Multi-pane layout (logs, chat, status)
- [ ] Theme system with color schemes

### 5.3 Interactive Commands
```kotlin
// Convert from TypeScript
packages/cli/src/ui/commands/ → src/jvmMain/kotlin/com/aimatrix/amx/tui/commands/
```
- [ ] Slash commands (/help, /clear, /theme, etc.)
- [ ] At-commands (@file, @url, etc.)
- [ ] Shell mode (!command)
- [ ] Vim mode support

---

## Phase 6: GUI Implementation with Compose Multiplatform (Week 6-7)

### 6.1 GUI Architecture
```kotlin
src/jvmMain/kotlin/com/aimatrix/amx/gui/GuiApp.kt
src/jvmMain/kotlin/com/aimatrix/amx/gui/screens/
src/jvmMain/kotlin/com/aimatrix/amx/gui/components/
```

### 6.2 Main Window Layout
- [ ] Split-pane design (sidebar, main chat, detail panel)
- [ ] Tabbed interface for multiple sessions
- [ ] Dockable panels for tools and agents
- [ ] Status bar with real-time updates

### 6.3 GUI-Specific Features
- [ ] Agent dashboard with visual status
- [ ] Workflow designer (drag-and-drop)
- [ ] Real-time agent execution graphs
- [ ] File tree explorer
- [ ] Integrated terminal emulator
- [ ] Settings dialog with live preview
- [ ] Rich text editing for prompts

### 6.4 Advanced GUI Components
- [ ] Code editor with syntax highlighting
- [ ] Markdown preview
- [ ] Image/media display
- [ ] Chart/graph visualizations for metrics
- [ ] Agent timeline view
- [ ] Resource usage monitors

---

## Phase 7: Agent Management System (Week 7-8)

### 7.1 Agent Implementation Types
```kotlin
src/commonMain/kotlin/com/aimatrix/amx/agents/types/
```
- [ ] TaskAgent - Single task execution
- [ ] WorkflowAgent - Multi-step workflows
- [ ] MonitorAgent - Continuous monitoring
- [ ] SchedulerAgent - Cron-based execution
- [ ] InteractiveAgent - User interaction required
- [ ] BackgroundAgent - Long-running processes

### 7.2 Agent Lifecycle Management
- [ ] State persistence and recovery
- [ ] Checkpoint/restore mechanism
- [ ] Resource allocation and limits
- [ ] Priority-based scheduling
- [ ] Dependency resolution

### 7.3 Multi-Agent Features
- [ ] Agent communication channels
- [ ] Shared context management
- [ ] Distributed locking
- [ ] Event bus for agent coordination
- [ ] Load balancing across agents

---

## Phase 8: Advanced Features (Week 8-9)

### 8.1 MCP (Model Context Protocol) Support
```kotlin
// Convert from TypeScript
packages/core/src/tools/mcp-client.ts → src/commonMain/kotlin/com/aimatrix/amx/mcp/McpClient.kt
packages/core/src/tools/mcp-tool.ts → src/commonMain/kotlin/com/aimatrix/amx/mcp/McpTool.kt
```
- [ ] MCP client implementation
- [ ] MCP server support
- [ ] Tool discovery
- [ ] OAuth integration

### 8.2 IDE Integration
```kotlin
// Convert from TypeScript
packages/core/src/ide/ → src/commonMain/kotlin/com/aimatrix/amx/ide/
```
- [ ] VSCode integration
- [ ] IntelliJ integration
- [ ] IDE detection
- [ ] Context sharing with IDEs

### 8.3 Extension System
- [ ] Plugin architecture
- [ ] Dynamic tool loading
- [ ] Custom agent types
- [ ] Theme extensions
- [ ] Language packs

### 8.4 Cloud Integration
- [ ] Cloud storage backends
- [ ] Distributed agent execution
- [ ] Remote session support
- [ ] Collaboration features

---

## Phase 9: Testing and Documentation (Week 9-10)

### 9.1 Testing Framework
```kotlin
// Convert tests from TypeScript
packages/*/src/**/*.test.ts → src/*/test/kotlin/com/aimatrix/amx/**/*Test.kt
```
- [ ] Unit tests for all components
- [ ] Integration tests
- [ ] End-to-end tests
- [ ] Performance benchmarks
- [ ] Security testing

### 9.2 Documentation
- [ ] API documentation
- [ ] User guide
- [ ] Developer guide
- [ ] Migration guide from Gemini CLI
- [ ] Video tutorials

---

## Phase 10: Build and Distribution (Week 10)

### 10.1 Build Configuration
- [ ] Native executable generation
- [ ] Cross-compilation setup
- [ ] Code signing for macOS/Windows
- [ ] Installer creation

### 10.2 Distribution Packages
- [ ] Homebrew formula (macOS)
- [ ] Snap package (Linux)
- [ ] MSI installer (Windows)
- [ ] Docker image
- [ ] Portable archives

### 10.3 Release Process
- [ ] Automated CI/CD pipeline
- [ ] Version management
- [ ] Change log generation
- [ ] Update notification system

---

## File Mapping Summary

### Core Module
```
packages/core/src/core/client.ts → com/aimatrix/amx/ai/GeminiClient.kt
packages/core/src/core/contentGenerator.ts → com/aimatrix/amx/ai/ContentGenerator.kt
packages/core/src/core/geminiChat.ts → com/aimatrix/amx/ai/AiChat.kt
packages/core/src/core/logger.ts → com/aimatrix/amx/core/Logger.kt
packages/core/src/core/prompts.ts → com/aimatrix/amx/ai/PromptManager.kt
packages/core/src/core/turn.ts → com/aimatrix/amx/ai/Turn.kt
packages/core/src/core/tokenLimits.ts → com/aimatrix/amx/ai/TokenLimits.kt
```

### Tools Module
```
packages/core/src/tools/*.ts → com/aimatrix/amx/tools/*Tool.kt
```

### CLI Module (TUI)
```
packages/cli/src/ui/App.tsx → com/aimatrix/amx/tui/TuiApp.kt
packages/cli/src/ui/components/*.tsx → com/aimatrix/amx/tui/components/*.kt
packages/cli/src/ui/hooks/*.ts → com/aimatrix/amx/tui/hooks/*.kt
```

### Services
```
packages/core/src/services/*.ts → com/aimatrix/amx/services/*.kt
```

### Utilities
```
packages/core/src/utils/*.ts → com/aimatrix/amx/utils/*.kt
```

---

## Command Line Interface

The final executable will support:
```bash
# Default (interactive TUI mode)
aimatrix

# Explicit TUI mode
aimatrix --tui

# GUI mode
aimatrix --gui

# Other commands work in both modes
aimatrix chat "Hello, AI"
aimatrix agent list
aimatrix workflow run -f workflow.yaml

# Mode-specific options
aimatrix --tui --theme=dracula
aimatrix --gui --window-size=1200x800
```

## Success Metrics

1. **Feature Parity**: All Gemini CLI features implemented
2. **Performance**: Faster execution than TypeScript version
3. **Multi-Agent**: Successfully run 10+ agents in parallel
4. **Cross-Platform**: Native execution on Linux, macOS, Windows
5. **User Experience**: Both TUI and GUI modes fully functional
6. **Extensibility**: Easy to add new tools and agents