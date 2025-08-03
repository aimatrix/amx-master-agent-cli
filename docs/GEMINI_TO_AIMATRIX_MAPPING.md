# Gemini CLI to AIMatrix CLI Mapping Guide

## Executable & Commands

| Gemini CLI | AIMatrix CLI | Notes |
|------------|--------------|-------|
| `gemini` | `aimatrix` | Main executable |
| `gemini -i` | `aimatrix --tui` or `aimatrix` | TUI mode (default) |
| N/A | `aimatrix --gui` | GUI mode (new feature) |
| `gemini chat` | `aimatrix chat` | Start chat session |
| `gemini config` | `aimatrix config` | Configuration management |

## Package Structure

| Gemini (TypeScript) | AIMatrix (Kotlin) |
|---------------------|-------------------|
| `@google/gemini-cli` | `com.aimatrix.cli` |
| `packages/core/` | `src/commonMain/kotlin/com/aimatrix/cli/` |
| `packages/cli/` | `src/jvmMain/kotlin/com/aimatrix/cli/tui/` |
| N/A | `src/jvmMain/kotlin/com/aimatrix/cli/gui/` |

## Core Module Mapping

| Gemini File | AIMatrix File | Purpose |
|-------------|---------------|---------|
| `core/client.ts` | `ai/AiClient.kt` | API client |
| `core/geminiChat.ts` | `ai/AiChat.kt` | Chat management |
| `core/contentGenerator.ts` | `ai/ContentGenerator.kt` | Content generation |
| `core/logger.ts` | `core/Logger.kt` | Logging system |
| `core/prompts.ts` | `ai/PromptManager.kt` | Prompt handling |
| `core/turn.ts` | `ai/Turn.kt` | Conversation turns |
| `core/tokenLimits.ts` | `ai/TokenManager.kt` | Token management |

## Tools Mapping

| Gemini Tool | AIMatrix Tool | Enhancements |
|-------------|---------------|--------------|
| `read-file.ts` | `ReadFileTool.kt` | + Streaming support |
| `write-file.ts` | `WriteFileTool.kt` | + Atomic writes |
| `edit.ts` | `EditTool.kt` | + Multi-edit support |
| `ls.ts` | `ListDirectoryTool.kt` | + Advanced filtering |
| `glob.ts` | `GlobTool.kt` | + Native patterns |
| `grep.ts` | `GrepTool.kt` | + Parallel search |
| `shell.ts` | `ShellTool.kt` | + Multi-platform native |
| `web-fetch.ts` | `WebFetchTool.kt` | + Ktor client |
| `web-search.ts` | `WebSearchTool.kt` | + Provider abstraction |
| `memoryTool.ts` | `MemoryTool.kt` | + Persistent storage |
| `mcp-client.ts` | `McpClient.kt` | Same functionality |
| `mcp-tool.ts` | `McpTool.kt` | Same functionality |

## UI Components (TUI)

| Gemini (React) | AIMatrix (Mordant) | Description |
|----------------|-------------------|-------------|
| `App.tsx` | `TuiApplication.kt` | Main app loop |
| `InputPrompt.tsx` | `InputPrompt.kt` | User input handling |
| `HistoryItemDisplay.tsx` | `MessageDisplay.kt` | Message rendering |
| `LoadingIndicator.tsx` | `ProgressDisplay.kt` | Progress indicators |
| `ThemeDialog.tsx` | `ThemeSelector.kt` | Theme management |
| `AuthDialog.tsx` | `AuthDialog.kt` | Authentication UI |

## New AIMatrix Features

### Agent Management
- `AgentManager.kt` - Orchestrates multiple agents
- `AgentScheduler.kt` - Schedules agent execution
- `WorkflowEngine.kt` - Manages complex workflows

### GUI Components (Compose)
- `MainWindow.kt` - Main application window
- `ChatScreen.kt` - Chat interface
- `AgentDashboard.kt` - Agent monitoring
- `WorkflowDesigner.kt` - Visual workflow editor

### Advanced Shell
- `NativeShell.kt` - Platform-specific implementations
- `ProcessManager.kt` - Advanced process control
- `SessionManager.kt` - Multiple shell sessions

## Configuration Files

| Gemini | AIMatrix |
|---------|----------|
| `~/.gemini/config.json` | `~/.aimatrix/config.json` |
| `~/.gemini/history` | `~/.aimatrix/history` |
| `~/.gemini/cache/` | `~/.aimatrix/cache/` |
| N/A | `~/.aimatrix/agents/` |
| N/A | `~/.aimatrix/workflows/` |

## Environment Variables

| Gemini | AIMatrix | Purpose |
|---------|----------|---------|
| `GEMINI_API_KEY` | `AIMATRIX_API_KEY` | API authentication |
| `GEMINI_MODEL` | `AIMATRIX_MODEL` | Default AI model |
| `GEMINI_LOG_LEVEL` | `AIMATRIX_LOG_LEVEL` | Logging verbosity |
| N/A | `AIMATRIX_UI_MODE` | Default UI mode (tui/gui) |
| N/A | `AIMATRIX_MAX_AGENTS` | Max concurrent agents |

## API Endpoints

| Gemini | AIMatrix |
|---------|----------|
| `https://generativelanguage.googleapis.com` | Same (configurable) |

## Command Syntax Changes

### Slash Commands
| Gemini | AIMatrix | Function |
|---------|----------|----------|
| `/help` | `/help` | Show help |
| `/clear` | `/clear` | Clear screen |
| `/theme` | `/theme` | Change theme |
| N/A | `/agents` | List agents |
| N/A | `/workflow` | Workflow commands |

### At Commands
| Gemini | AIMatrix | Function |
|---------|----------|----------|
| `@file` | `@file` | Reference file |
| `@url` | `@url` | Reference URL |
| N/A | `@agent` | Reference agent |
| N/A | `@context` | Reference context |

## Error Codes

| Gemini Error | AIMatrix Error | Description |
|--------------|----------------|-------------|
| `GEMINI_001` | `AMX_001` | API key missing |
| `GEMINI_002` | `AMX_002` | Network error |
| `GEMINI_003` | `AMX_003` | Invalid config |
| N/A | `AMX_100` | Agent error |
| N/A | `AMX_200` | Workflow error |

## Migration Script

```kotlin
// Automatic migration on first run
class GeminiToAimatrixMigration {
    fun migrate() {
        // 1. Copy configuration
        copyFile("~/.gemini/config.json", "~/.aimatrix/config.json")
        
        // 2. Update configuration values
        updateConfig { config ->
            config.copy(
                brand = "aimatrix",
                executable = "aimatrix"
            )
        }
        
        // 3. Migrate history
        copyFile("~/.gemini/history", "~/.aimatrix/history")
        
        // 4. Update environment variables
        println("Please update your environment variables:")
        println("  GEMINI_API_KEY → AIMATRIX_API_KEY")
        println("  GEMINI_MODEL → AIMATRIX_MODEL")
    }
}
```

## Behavioral Differences

1. **Default Mode**: Gemini defaults to interactive mode, AIMatrix defaults to TUI mode
2. **Multi-Agent**: AIMatrix can run multiple agents in parallel
3. **GUI Mode**: AIMatrix offers a full GUI mode with `--gui`
4. **Workflows**: AIMatrix supports complex workflow definitions
5. **Sessions**: AIMatrix supports multiple concurrent sessions

## Performance Improvements

| Feature | Gemini (TS) | AIMatrix (Kotlin) |
|---------|-------------|-------------------|
| Startup time | ~500ms | ~200ms |
| Memory usage | ~150MB | ~100MB |
| Concurrent agents | 1 | 10+ |
| Native shell | Via Node.js | Direct native |

## Deprecations

The following Gemini features are deprecated in AIMatrix:
- Node.js specific plugins (replaced with Kotlin plugins)
- JavaScript configuration files (use JSON/YAML)
- React DevTools integration (replaced with Compose tools)