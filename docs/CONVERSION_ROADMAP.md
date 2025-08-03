# TypeScript to Kotlin Conversion Roadmap

## Critical Conversion Considerations

### 1. Async/Promise → Coroutines
TypeScript:
```typescript
async function readFile(path: string): Promise<string> {
  return await fs.promises.readFile(path, 'utf-8');
}
```

Kotlin:
```kotlin
suspend fun readFile(path: String): String {
  return okio.FileSystem.SYSTEM.read(path.toPath()) {
    readUtf8()
  }
}
```

### 2. React Components → Mordant/Compose
TypeScript (React):
```tsx
const ChatMessage: React.FC<Props> = ({ message }) => {
  return <Box>{message.content}</Box>;
}
```

Kotlin (Mordant for TUI):
```kotlin
class ChatMessage(private val terminal: Terminal) {
  fun render(message: Message) {
    terminal.println(
      buildAnnotatedString {
        append(message.content)
      }
    )
  }
}
```

Kotlin (Compose for GUI):
```kotlin
@Composable
fun ChatMessage(message: Message) {
  Card {
    Text(message.content)
  }
}
```

### 3. Event Emitters → Kotlin Flow
TypeScript:
```typescript
class EventEmitter extends events.EventEmitter {
  emit(event: string, data: any): boolean
}
```

Kotlin:
```kotlin
class EventBus {
  private val _events = MutableSharedFlow<Event>()
  val events: SharedFlow<Event> = _events.asSharedFlow()
  
  suspend fun emit(event: Event) {
    _events.emit(event)
  }
}
```

---

## Detailed Module Conversion

### Week 1-2: Core Infrastructure

#### Logger System
**Source**: `packages/core/src/core/logger.ts`
**Target**: `src/commonMain/kotlin/com/aimatrix/cli/core/Logger.kt`

```kotlin
object Logger {
  enum class Level { DEBUG, INFO, WARNING, ERROR }
  
  fun log(level: Level, message: String, data: Map<String, Any> = emptyMap()) {
    // Platform-specific implementation
  }
}
```

#### Configuration System
**Source**: `packages/core/src/config/`
**Target**: `src/commonMain/kotlin/com/aimatrix/cli/config/`

Key conversions:
- JSON config files → Kotlinx.serialization
- Environment variables → System.getenv()
- Path handling → Okio Path API

### Week 3-4: Tool System

#### File System Tools
**Conversion mapping**:
```
read-file.ts → ReadFileTool.kt
write-file.ts → WriteFileTool.kt
edit.ts → EditTool.kt
ls.ts → ListDirectoryTool.kt
glob.ts → GlobTool.kt
grep.ts → GrepTool.kt
```

**Key differences**:
- Use Okio for file operations
- Kotlin native regex instead of JS regex
- Structured concurrency for parallel operations

#### Example: ReadFileTool Conversion

TypeScript:
```typescript
export class ReadFileTool extends BaseTool<ReadFileParams, ToolResult> {
  async execute(params: ReadFileParams): Promise<ToolResult> {
    const content = await fs.promises.readFile(params.path, 'utf-8');
    return { llmContent: content };
  }
}
```

Kotlin:
```kotlin
class ReadFileTool : BaseTool<ReadFileParams, ToolResult>(
  name = "read_file",
  displayName = "Read File",
  description = "Reads content from a file"
) {
  override suspend fun execute(
    params: ReadFileParams,
    signal: CancellationSignal,
    onUpdate: ((String) -> Unit)?
  ): ToolResult {
    val content = withContext(Dispatchers.IO) {
      FileSystem.SYSTEM.read(params.path.toPath()) {
        readUtf8()
      }
    }
    return SimpleToolResult(
      llmContent = content,
      displayContent = ToolDisplayContent.Markdown(content)
    )
  }
}
```

### Week 5-6: AI Integration

#### API Client Conversion
**Source**: `packages/core/src/core/client.ts`
**Target**: `src/commonMain/kotlin/com/aimatrix/cli/ai/AiClient.kt`

TypeScript uses fetch API → Kotlin uses Ktor Client:
```kotlin
class AiClient(private val config: AiConfig) {
  private val httpClient = HttpClient {
    install(ContentNegotiation) {
      json()
    }
    install(HttpTimeout) {
      requestTimeoutMillis = 30000
    }
  }
  
  suspend fun generateContent(request: GenerateRequest): GenerateResponse {
    return httpClient.post(config.endpoint) {
      contentType(ContentType.Application.Json)
      setBody(request)
      header("Authorization", "Bearer ${config.apiKey}")
    }.body()
  }
}
```

### Week 7-8: UI Implementation

#### TUI with Mordant
**Key components to implement**:

1. **Main Loop**
```kotlin
class TuiApp(private val terminal: Terminal) {
  private val prompt = InteractivePrompt(terminal)
  
  suspend fun run() {
    terminal.println(
      TextColors.brightBlue("AIMatrix CLI v${Version.current}")
    )
    
    while (true) {
      val input = prompt.ask("> ")
      processCommand(input)
    }
  }
}
```

2. **Rich Output Rendering**
```kotlin
class MessageRenderer(private val terminal: Terminal) {
  fun render(message: Message) {
    when (message) {
      is UserMessage -> renderUser(message)
      is AssistantMessage -> renderAssistant(message)
      is ToolMessage -> renderTool(message)
    }
  }
  
  private fun renderAssistant(message: AssistantMessage) {
    terminal.println(
      Panel(
        content = message.content,
        title = "AI Response",
        borderStyle = BorderStyle.ROUNDED
      )
    )
  }
}
```

#### GUI with Compose Multiplatform
**Main window structure**:
```kotlin
@Composable
fun AimatrixApp() {
  var uiMode by remember { mutableStateOf(UiMode.CHAT) }
  
  MaterialTheme {
    Row(Modifier.fillMaxSize()) {
      // Sidebar
      NavigationRail {
        NavigationRailItem(
          icon = { Icon(Icons.Default.Chat, "Chat") },
          selected = uiMode == UiMode.CHAT,
          onClick = { uiMode = UiMode.CHAT }
        )
        NavigationRailItem(
          icon = { Icon(Icons.Default.Dashboard, "Agents") },
          selected = uiMode == UiMode.AGENTS,
          onClick = { uiMode = UiMode.AGENTS }
        )
      }
      
      // Main content
      when (uiMode) {
        UiMode.CHAT -> ChatScreen()
        UiMode.AGENTS -> AgentDashboard()
        UiMode.WORKFLOWS -> WorkflowDesigner()
      }
    }
  }
}
```

### Week 9-10: Platform-Specific Features

#### Shell Execution
**Platform implementations**:

1. **JVM Platform**
```kotlin
actual class PlatformShell : Shell {
  actual suspend fun execute(command: String, options: ShellOptions): ShellResult {
    val process = ProcessBuilder()
      .command(shellCommand(command))
      .directory(File(options.workingDir))
      .start()
    
    return process.waitFor().let { exitCode ->
      ShellResult.Success(
        output = process.inputStream.readText(),
        errorOutput = process.errorStream.readText(),
        exitCode = exitCode
      )
    }
  }
}
```

2. **Native Platform**
```kotlin
actual class PlatformShell : Shell {
  actual suspend fun execute(command: String, options: ShellOptions): ShellResult {
    memScoped {
      val result = platform.posix.system(command)
      // Native implementation details
    }
  }
}
```

---

## Testing Strategy

### Unit Test Conversion
TypeScript (Jest):
```typescript
describe('ReadFileTool', () => {
  it('should read file content', async () => {
    const result = await tool.execute({ path: 'test.txt' });
    expect(result.llmContent).toBe('content');
  });
});
```

Kotlin:
```kotlin
class ReadFileToolTest {
  @Test
  fun `should read file content`() = runTest {
    val result = tool.execute(ReadFileParams("test.txt"))
    assertEquals("content", result.llmContent)
  }
}
```

---

## Executable Configuration

### Shadow JAR Configuration
```kotlin
tasks.withType<ShadowJar> {
  archiveBaseName.set("aimatrix")
  archiveClassifier.set("")
  mergeServiceFiles()
  
  manifest {
    attributes["Main-Class"] = "com.aimatrix.cli.MainKt"
  }
}
```

### Native Image (Optional - GraalVM)
```kotlin
graalvmNative {
  binaries {
    named("main") {
      imageName.set("aimatrix")
      mainClass.set("com.aimatrix.cli.MainKt")
      
      buildArgs.add("--no-fallback")
      buildArgs.add("--enable-http")
      buildArgs.add("--enable-https")
    }
  }
}
```

### Distribution Script
```bash
#!/bin/bash
# aimatrix launcher script

JAVA_OPTS="-Xmx2g -XX:+UseG1GC"

# Detect if GUI mode is requested
if [[ "$1" == "--gui" || "$1" == "-g" ]]; then
  JAVA_OPTS="$JAVA_OPTS -Daimatrix.ui.mode=gui"
elif [[ "$1" == "--tui" || "$1" == "-t" ]]; then
  JAVA_OPTS="$JAVA_OPTS -Daimatrix.ui.mode=tui"
fi

exec java $JAVA_OPTS -jar /usr/local/lib/aimatrix/aimatrix.jar "$@"
```

---

## Migration Guide for Users

### Configuration Migration
```kotlin
// Automatic migration on first run
class ConfigMigration {
  fun migrateFromGemini() {
    val geminiConfig = Path("~/.gemini/config.json")
    val aimatrixConfig = Path("~/.aimatrix/config.json")
    
    if (geminiConfig.exists() && !aimatrixConfig.exists()) {
      // Parse and transform configuration
      val config = parseGeminiConfig(geminiConfig)
      saveAimatrixConfig(transform(config), aimatrixConfig)
    }
  }
}
```

### Command Compatibility
```
gemini chat → aimatrix chat
gemini -i → aimatrix --tui
gemini config → aimatrix config
```

---

## Performance Optimizations

1. **Coroutine Pools**: Dedicated dispatchers for I/O, CPU, and UI operations
2. **Lazy Loading**: Tools and services loaded on-demand
3. **Memory Management**: Structured concurrency prevents leaks
4. **Native Performance**: Platform-specific optimizations

---

## Deliverables Checklist

- [ ] Executable named `aimatrix`
- [ ] TUI mode fully functional (--tui or default)
- [ ] GUI mode fully functional (--gui)
- [ ] All Gemini CLI features ported
- [ ] Multi-agent parallel execution
- [ ] Cross-platform support (Linux, macOS, Windows)
- [ ] Comprehensive test suite
- [ ] User documentation
- [ ] Migration guide
- [ ] Performance benchmarks