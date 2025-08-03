# AIMatrix CLI Feature Mapping Document

## Overview

This document maps Google Gemini CLI features to AIMatrix CLI implementation, ensuring 100% feature parity plus multi-LLM enhancements. Each feature is categorized by implementation status and enhancement opportunities.

**Legend:**
- ✅ **IMPLEMENTED** - Feature is complete in AIMatrix CLI
- 🚧 **IN PROGRESS** - Feature is partially implemented  
- 📋 **PLANNED** - Feature is planned for implementation
- 🔄 **ENHANCED** - Feature implemented with additional multi-LLM capabilities

---

## 1. Installation Methods

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Node.js `npx` execution | 📋 **PLANNED** | Kotlin Multiplatform native binaries |
| Global npm installation | 📋 **PLANNED** | Native package managers (brew, apt, etc.) |
| Homebrew installation | 📋 **PLANNED** | Cross-platform package distribution |
| Node.js 20+ requirement | ✅ **IMPLEMENTED** | JVM/Native runtime requirements |

**AIMatrix Enhancement**: Native binaries for better performance and reduced dependencies.

---

## 2. Authentication System

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Gemini API Key support | ✅ **IMPLEMENTED** | GeminiProvider in providers package |
| Vertex AI API support | 📋 **PLANNED** | Extended GeminiProvider with Vertex AI |
| OAuth authentication | 📋 **PLANNED** | Web-based OAuth flow |
| Cloud Shell auto-auth | 📋 **PLANNED** | Google Cloud integration |
| Environment variable precedence | ✅ **IMPLEMENTED** | AmxConfig hierarchy system |
| `.env` file search order | ✅ **IMPLEMENTED** | ConfigManager handles file precedence |

**AIMatrix Enhancement**: Multi-provider authentication with unified credential management.

### Enhanced Authentication Features
- ✅ **OpenAI API Key** - OpenAIProvider implementation
- ✅ **Claude API Key** - ClaudeProvider implementation  
- ✅ **DeepSeek API Key** - DeepSeekProvider implementation
- 📋 **Provider-specific OAuth** - OAuth flows for each provider
- 📋 **Credential rotation** - Automatic key management

---

## 3. Command Structure

### 3.1 Slash Commands (/)

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/auth` | 🔄 **ENHANCED** | Multi-provider authentication selection |
| `/version` | ✅ **IMPLEMENTED** | AppInfo.VERSION in AIMatrixMain |
| `/about` | ✅ **IMPLEMENTED** | Enhanced with multi-provider info |
| `/help` or `/?` | ✅ **IMPLEMENTED** | Clikt help system integration |
| `/quit` or `/exit` | 📋 **PLANNED** | TUI/GUI session management |
| `/clear` | 📋 **PLANNED** | Terminal clearing functionality |
| `/theme` | 🚧 **IN PROGRESS** | GUI theme switching implemented |
| `/vim` | 📋 **PLANNED** | Vim-style editing mode |

#### Session Management Commands

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/stats` | 🔄 **ENHANCED** | Multi-provider usage statistics |
| `/privacy` | 📋 **PLANNED** | Privacy notice display |
| `/bug` | 📋 **PLANNED** | GitHub issue creation |

#### Conversation Management Commands

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/chat save <tag>` | 📋 **PLANNED** | Chat session persistence |
| `/chat resume <tag>` | 📋 **PLANNED** | Session restoration |
| `/chat list` | 📋 **PLANNED** | Available conversations |
| `/compress` | 📋 **PLANNED** | Context compression |
| `/copy` | 📋 **PLANNED** | Clipboard integration |

#### File and Directory Operations

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/directory` or `/dir` | 📋 **PLANNED** | Workspace management |
| `/dir add <path>` | 📋 **PLANNED** | Directory inclusion |
| `/dir show` | 📋 **PLANNED** | Workspace display |
| `/editor` | 📋 **PLANNED** | Editor selection dialog |
| `/init` | 📋 **PLANNED** | AIMATRIX.md generation |

#### Tool and System Information

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/tools` | ✅ **IMPLEMENTED** | ToolRegistry system |
| `/tools desc` | ✅ **IMPLEMENTED** | Tool descriptions |
| `/tools-verbose` | ✅ **IMPLEMENTED** | Detailed tool info |
| `/mcp` | 📋 **PLANNED** | MCP server integration |
| `/mcp desc` | 📋 **PLANNED** | MCP server descriptions |
| `/mcp schema` | 📋 **PLANNED** | MCP schema validation |
| `/extensions` | 📋 **PLANNED** | Extension management |

#### Memory and Context Management

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/memory add` | 📋 **PLANNED** | Context memory storage |
| `/memory show` | 📋 **PLANNED** | Memory display |
| `/memory refresh` | 📋 **PLANNED** | Memory context refresh |

#### Restoration and Checkpointing

| Gemini CLI Command | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `/restore` | 📋 **PLANNED** | File state restoration |

### 3.2 At Commands (@)

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| `@<path>` file injection | ✅ **IMPLEMENTED** | ReadFileTool handles file content |
| Directory content injection | ✅ **IMPLEMENTED** | FileSystem utilities support directories |
| Git-aware filtering | 📋 **PLANNED** | .gitignore respect in file operations |
| Space handling in paths | ✅ **IMPLEMENTED** | Path utilities handle spaces |

---

## 4. Configuration System

### 4.1 Configuration Hierarchy

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| System-level config | ✅ **IMPLEMENTED** | AmxConfig.load() supports system paths |
| User-level config | ✅ **IMPLEMENTED** | ~/.aimatrix/config.json |
| Project-level config | ✅ **IMPLEMENTED** | .aimatrix/config.json |
| Environment variables | ✅ **IMPLEMENTED** | ConfigManager handles env vars |
| Command-line args | ✅ **IMPLEMENTED** | Clikt argument parsing |

### 4.2 Configuration Parameters

| Gemini CLI Parameter | AIMatrix CLI Status | Implementation Notes |
|---------------------|-------------------|---------------------|
| `theme` | 🚧 **IN PROGRESS** | GUI theme system implemented |
| `autoAccept` | 📋 **PLANNED** | Tool auto-approval system |
| `sandbox` | 🚧 **IN PROGRESS** | ShellTool security validation |
| `checkpointing` | 📋 **PLANNED** | File state management |
| `fileFiltering` | 📋 **PLANNED** | Git-aware file filtering |
| `usageStatisticsEnabled` | ✅ **IMPLEMENTED** | PerformanceAnalytics system |
| `contextFileName` | 📋 **PLANNED** | AIMATRIX.md support |
| `coreTools` | ✅ **IMPLEMENTED** | ToolRegistry configuration |
| `mcpServers` | 📋 **PLANNED** | MCP integration |
| `telemetry` | ✅ **IMPLEMENTED** | Analytics and monitoring |
| `maxSessionTurns` | 📋 **PLANNED** | Session turn limits |
| `vimMode` | 📋 **PLANNED** | Vim-style editing |

**AIMatrix Enhancement**: Multi-provider specific configurations.

---

## 5. Built-in Tools System

### 5.1 File Operations

| Gemini CLI Tool | AIMatrix CLI Status | Implementation Notes |
|----------------|-------------------|---------------------|
| ReadFolder (ls) | ✅ **IMPLEMENTED** | FileSystem.listDirectory |
| ReadFile (read-file) | ✅ **IMPLEMENTED** | ReadFileTool complete |
| ReadManyFiles | 📋 **PLANNED** | Glob pattern file reading |
| WriteFile (write-file) | ✅ **IMPLEMENTED** | FileSystem.writeStringToFile |
| Edit (edit) | 📋 **PLANNED** | Code diff and editing system |

### 5.2 Search and Discovery

| Gemini CLI Tool | AIMatrix CLI Status | Implementation Notes |
|----------------|-------------------|---------------------|
| FindFiles (glob) | 📋 **PLANNED** | Pattern-based file search |
| SearchText (grep) | 📋 **PLANNED** | Content search within files |

### 5.3 Terminal Operations

| Gemini CLI Tool | AIMatrix CLI Status | Implementation Notes |
|----------------|-------------------|---------------------|
| Shell (shell) | ✅ **IMPLEMENTED** | ShellTool with security validation |

### 5.4 Web Tools

| Gemini CLI Tool | AIMatrix CLI Status | Implementation Notes |
|----------------|-------------------|---------------------|
| WebFetch | 📋 **PLANNED** | HTTP content fetching |
| GoogleWebSearch | 📋 **PLANNED** | Google search integration |
| WebSearch | 📋 **PLANNED** | General web search |

### 5.5 Memory Tools

| Gemini CLI Tool | AIMatrix CLI Status | Implementation Notes |
|----------------|-------------------|---------------------|
| SaveMemory | 📋 **PLANNED** | Persistent memory storage |
| Memory Management | 📋 **PLANNED** | Cross-session context |

**AIMatrix Enhancement**: Provider-specific tool optimization and additional tools.

---

## 6. Model Context Protocol (MCP) Integration

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| MCP server support | 📋 **PLANNED** | Protocol implementation |
| Stdio transport | 📋 **PLANNED** | Subprocess communication |
| SSE transport | 📋 **PLANNED** | Server-sent events |
| HTTP streaming | 📋 **PLANNED** | HTTP-based transport |
| Tool discovery | 📋 **PLANNED** | Automatic tool registration |
| OAuth 2.0 support | 📋 **PLANNED** | Secure authentication |
| Schema validation | 📋 **PLANNED** | Tool compatibility checks |

**AIMatrix Enhancement**: MCP support across multiple AI providers.

---

## 7. Context Management System

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| GEMINI.md files | 📋 **PLANNED** | AIMATRIX.md equivalent |
| Global context | 📋 **PLANNED** | ~/.aimatrix/AIMATRIX.md |
| Project context | 📋 **PLANNED** | Project root AIMATRIX.md |
| Hierarchical loading | 📋 **PLANNED** | Context file merging |
| Sub-directory context | 📋 **PLANNED** | Component-specific instructions |

**AIMatrix Enhancement**: Provider-specific context instructions.

---

## 8. Command-Line Arguments

| Gemini CLI Argument | AIMatrix CLI Status | Implementation Notes |
|--------------------|-------------------|---------------------|
| `--model <model>` | 🔄 **ENHANCED** | Multi-provider model selection |
| `--prompt <text>` | ✅ **IMPLEMENTED** | ChatCommand non-interactive mode |
| `--sandbox` | 🚧 **IN PROGRESS** | Security sandbox implementation |
| `--debug` | ✅ **IMPLEMENTED** | Logger debug level |
| `--telemetry` | ✅ **IMPLEMENTED** | Analytics opt-in |
| `--checkpointing` | 📋 **PLANNED** | File restoration |
| `--help [command]` | ✅ **IMPLEMENTED** | Clikt help system |
| `--version` or `-v` | ✅ **IMPLEMENTED** | Version display |
| `--yolo` | 📋 **PLANNED** | Aggressive automation mode |

**AIMatrix Enhancement**: Provider-specific arguments and multi-model support.

---

## 9. Environment Variables

### 9.1 Authentication Variables

| Gemini CLI Variable | AIMatrix CLI Status | Implementation Notes |
|--------------------|-------------------|---------------------|
| `GEMINI_API_KEY` | ✅ **IMPLEMENTED** | GeminiProvider configuration |
| `GOOGLE_API_KEY` | 📋 **PLANNED** | Google services integration |
| `GOOGLE_CLOUD_PROJECT` | 📋 **PLANNED** | Vertex AI support |
| `GOOGLE_CLOUD_LOCATION` | 📋 **PLANNED** | Regional configuration |
| `GOOGLE_GENAI_USE_VERTEXAI` | 📋 **PLANNED** | Vertex AI toggle |

### 9.2 Behavior Control Variables

| Gemini CLI Variable | AIMatrix CLI Status | Implementation Notes |
|--------------------|-------------------|---------------------|
| `GEMINI_MODEL` | 🔄 **ENHANCED** | Multi-provider model defaults |
| `GEMINI_SANDBOX` | 🚧 **IN PROGRESS** | Sandbox behavior control |

**AIMatrix Enhancement**: Provider-specific environment variables.

### Enhanced Environment Variables
- ✅ **OPENAI_API_KEY** - OpenAI authentication
- ✅ **CLAUDE_API_KEY** - Anthropic Claude authentication
- ✅ **DEEPSEEK_API_KEY** - DeepSeek authentication
- 📋 **AIMATRIX_DEFAULT_PROVIDER** - Default provider selection
- 📋 **AIMATRIX_MODEL_SELECTOR** - Intelligent selection mode

---

## 10. Advanced Features

### 10.1 Multimodal Capabilities

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Text input | ✅ **IMPLEMENTED** | All providers support text |
| Image input | 🔄 **ENHANCED** | Provider-specific image support |
| Audio input | 📋 **PLANNED** | Audio processing capabilities |
| Video input | 📋 **PLANNED** | Video analysis features |
| PDF input | 📋 **PLANNED** | Document processing |

### 10.2 Streaming and Real-time

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Live API support | 📋 **PLANNED** | Real-time interactions |
| Streaming content | 🚧 **IN PROGRESS** | Provider streaming support |
| Real-time processing | 📋 **PLANNED** | Continuous processing |

### 10.3 Large Context Handling

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| 1M+ token context | 🔄 **ENHANCED** | Provider-specific context limits |
| Context compression | 📋 **PLANNED** | Intelligent summarization |
| Memory management | ✅ **IMPLEMENTED** | Persistent storage |

### 10.4 Development Integration

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Git awareness | 📋 **PLANNED** | Repository integration |
| IDE integration | 📋 **PLANNED** | Editor compatibility |
| File watching | 📋 **PLANNED** | Automatic updates |
| Diff preview | 📋 **PLANNED** | Change visualization |

**AIMatrix Enhancement**: Cross-provider feature support with intelligent fallbacks.

---

## 11. Security Features

### 11.1 Sandboxing

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Docker integration | 📋 **PLANNED** | Container isolation |
| Podman support | 📋 **PLANNED** | Alternative runtime |
| Permission control | 🚧 **IN PROGRESS** | ShellTool security |

### 11.2 Authentication Security

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| OAuth 2.0 | 📋 **PLANNED** | Web-based auth |
| Token management | ✅ **IMPLEMENTED** | Secure credential storage |
| Local caching | ✅ **IMPLEMENTED** | Configuration persistence |

**AIMatrix Enhancement**: Enhanced security across multiple providers.

---

## 12. User Interface Systems

### 12.1 Command Line Interface

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Interactive REPL | ✅ **IMPLEMENTED** | ChatCommand implementation |
| Non-interactive mode | ✅ **IMPLEMENTED** | Prompt argument support |
| Help system | ✅ **IMPLEMENTED** | Clikt comprehensive help |
| Tab completion | 📋 **PLANNED** | Shell completion scripts |

### 12.2 Terminal User Interface (TUI)

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Interactive chat | ✅ **IMPLEMENTED** | TUIManager with Mordant |
| Dashboard view | ✅ **IMPLEMENTED** | Performance metrics display |
| Settings interface | ✅ **IMPLEMENTED** | Configuration management |
| Theme support | ✅ **IMPLEMENTED** | Visual customization |

### 12.3 Graphical User Interface (GUI)

| Gemini CLI Feature | AIMatrix CLI Status | Implementation Notes |
|-------------------|-------------------|---------------------|
| Desktop application | ✅ **IMPLEMENTED** | Compose Multiplatform GUI |
| Chat interface | ✅ **IMPLEMENTED** | Modern chat UI |
| Dashboard | ✅ **IMPLEMENTED** | Performance visualization |
| Provider management | ✅ **IMPLEMENTED** | Multi-provider controls |

**AIMatrix Enhancement**: Three interface modes with seamless switching.

---

## 13. Implementation Priorities

### Phase 11: Advanced Features (In Progress)

#### High Priority - Core Parity
1. **MCP Integration** - Model Context Protocol support
2. **Context Management** - AIMATRIX.md file system
3. **Advanced File Operations** - Edit, glob, grep tools
4. **Web Tools** - WebFetch, WebSearch capabilities
5. **Memory System** - Persistent context storage

#### Medium Priority - Enhanced Features
6. **Checkpointing System** - File state restoration
7. **Sandboxing** - Docker/Podman integration
8. **OAuth Authentication** - Multi-provider OAuth flows
9. **Session Management** - Chat save/resume functionality
10. **Advanced Configuration** - Complete config hierarchy

#### Low Priority - Polish Features
11. **Vim Mode** - Vim-style editing
12. **Tab Completion** - Shell completion scripts
13. **Advanced Theming** - Extended visual customization
14. **Plugin System** - Extension architecture
15. **IDE Integration** - Editor compatibility

### Implementation Strategy

1. **Core Feature Implementation** - Complete essential tools and commands
2. **Multi-Provider Enhancement** - Extend features across all providers
3. **Security Hardening** - Implement comprehensive security measures
4. **Performance Optimization** - Optimize for speed and efficiency
5. **User Experience Polish** - Refine interfaces and interactions

---

## 14. Success Metrics

### Feature Parity Checklist
- [ ] All slash commands implemented
- [ ] Complete authentication system
- [ ] Full tool system parity
- [ ] MCP integration complete
- [ ] Context management operational
- [ ] Configuration hierarchy functional
- [ ] Security features implemented
- [ ] Advanced features operational

### Enhancement Goals
- [ ] Multi-provider support complete
- [ ] Performance analytics operational
- [ ] Cost optimization functional
- [ ] Intelligent model selection active
- [ ] Three UI modes fully functional
- [ ] Enhanced security implemented
- [ ] Extended tool ecosystem

---

## Conclusion

AIMatrix CLI is designed to achieve 100% feature parity with Google Gemini CLI while providing significant enhancements through multi-LLM provider support. The implementation prioritizes core functionality first, followed by enhanced features and polish.

Key differentiators:
1. **Multi-Provider Support** - OpenAI, Claude, Gemini, DeepSeek, and more
2. **Intelligent Model Selection** - Cost and performance optimization
3. **Multiple Interface Modes** - CLI, TUI, and GUI options
4. **Enhanced Analytics** - Cross-provider performance tracking
5. **Unified Experience** - Consistent interface across all providers

The mapping ensures that all Gemini CLI users can seamlessly transition to AIMatrix CLI with all familiar features intact, while gaining access to multiple AI providers and enhanced capabilities.