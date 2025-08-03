# AMX-Hub Integration

AIMatrix CLI now includes comprehensive integration with AMX-Hub platform, providing a GitHub CLI-like experience for managing AI agents and workspaces.

## Installation

```bash
# Build the CLI
./gradlew fatJar

# Create alias for easy access
alias amx="java -jar build/libs/amx-cli-all-1.0.0.jar"
```

## Authentication

Before using AMX-Hub commands, you need to authenticate:

```bash
# Login to AMX-Hub
amx hub auth login

# Check authentication status
amx hub auth status

# View your access token
amx hub auth token

# Refresh your token
amx hub auth refresh

# Logout
amx hub auth logout
```

## Working with Workspaces

Workspaces in AMX-Hub are similar to repositories in GitHub:

```bash
# List your workspaces
amx hub workspace list

# Create a new workspace
amx hub workspace create my-ai-workspace
amx hub workspace create my-workspace --public --description "AI development workspace"

# View workspace details
amx hub workspace view <workspace-id>

# Clone a workspace locally
amx hub workspace clone <workspace-id>

# Delete a workspace
amx hub workspace delete <workspace-id>

# Manage workspace members
amx hub workspace members <workspace-id>

# View workspace activity
amx hub workspace activity <workspace-id>
```

## Managing Agents

Agents are the core of AMX-Hub, providing AI capabilities:

```bash
# List agents in a workspace
amx hub agent list <workspace-id>

# List all your agents
amx hub agent list --all

# Create a new agent
amx hub agent create my-agent --workspace <workspace-id>

# View agent details
amx hub agent view <agent-id>

# Start/stop agents
amx hub agent start <agent-id>
amx hub agent stop <agent-id>

# Delete an agent
amx hub agent delete <agent-id>
```

## Interactive Chat

Start an interactive chat session with any agent:

```bash
# Interactive chat mode
amx hub agent chat <agent-id>

# Send a single message
amx hub agent chat <agent-id> -m "Hello, can you help me with Python?"
```

## Direct LLM Provider Chat

AIMatrix CLI also supports direct chat with LLM providers:

```bash
# Chat with OpenAI
amx chat "What is quantum computing?" --provider openai

# Chat with Claude
amx chat "Explain machine learning" --provider claude --model claude-3-opus

# Interactive mode
amx chat --provider gemini
```

## Configuration

```bash
# View all configuration
amx config --list

# Set default provider
amx config default.provider openai

# Configure AMX-Hub settings
amx config amxhub.host https://hub.aimatrix.com
```

## Command Structure

All AMX-Hub commands follow this pattern:
```
amx hub <resource> <action> [options]
```

Resources:
- `auth` - Authentication management
- `workspace` - Workspace operations  
- `agent` - Agent management
- `session` - Session management
- `api` - Direct API calls

## Examples

### Complete Workflow

```bash
# 1. Login to AMX-Hub
amx hub auth login

# 2. Create a workspace
amx hub workspace create ai-assistants --description "AI assistant development"

# 3. List available agent types
amx hub agent types

# 4. Create an agent
amx hub agent create code-helper \
  --workspace ai-assistants \
  --type code \
  --model gpt-4

# 5. Start the agent
amx hub agent start code-helper

# 6. Chat with the agent
amx hub agent chat code-helper

# 7. View agent statistics
amx hub agent view code-helper --stats
```

### Working with Multiple Providers

```bash
# Configure multiple LLM providers
amx config provider add openai --key sk-...
amx config provider add claude --key sk-ant-...
amx config provider add gemini --key AIza...

# Set default provider
amx config default.provider claude

# Use specific provider
amx chat "Hello" --provider openai
```

## Environment Variables

You can also configure AMX-Hub using environment variables:

```bash
export AMX_HUB_HOST=https://hub.aimatrix.com
export AMX_HUB_TOKEN=your-access-token
```

## Troubleshooting

If you encounter authentication issues:
```bash
# Check current auth status
amx hub auth status

# Refresh token
amx hub auth refresh

# Re-login if needed
amx hub auth logout
amx hub auth login
```

## Security

- Credentials are stored securely in `~/.aimatrix/`
- Tokens are encrypted using Google Tink
- Never share your access tokens
- Use `amx hub auth logout` when done

## API Compatibility

AMX-Hub CLI is designed to be compatible with the AMX-Hub API v1. It supports:
- RESTful API endpoints
- JWT authentication
- Reactive streams for real-time updates
- WebSocket connections for live chat

## Future Features

- Real-time collaboration
- Agent marketplace integration
- Workflow automation
- CI/CD integration
- Custom agent templates