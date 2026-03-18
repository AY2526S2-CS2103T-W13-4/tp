# CoursePilot AI Assistant — Setup & Features

## Setup

1. **Install Ollama:** Download from [ollama.ai](https://ollama.ai) and install.
2. **Pull the model:**
   ```bash
   ollama pull qwen2.5:1.5b
   ```
3. **Start Ollama:**
   ```bash
   ollama serve
   ```
4. **Launch CoursePilot:**
   ```bash
   ./gradlew run
   ```

The AI features activate automatically when Ollama is running. If Ollama is unavailable, the app works normally without AI.

## Features

### 1. Natural Language Command Translation

Type commands in plain English — the AI translates them into CoursePilot commands and pre-fills the command box for your review.

| You type | AI suggests |
|---|---|
| remove the third student | `delete 3` |
| show me all students | `list` |
| search for Alice | `find Alice` |
| change first student phone to 99991111 | `edit 1 p/99991111` |
| add Bob phone 81234567 email bob@u.nus.edu matric A654321 | `add n/Bob p/81234567 e/bob@u.nus.edu m/A654321` |

The suggested command is **not executed automatically** — you can review, edit, then press Enter.

### 2. Conversational Chat

Ask questions about the app and get helpful answers directly in the result display.

| You type | AI responds with |
|---|---|
| how can I get started? | A guide on basic commands |
| what commands are available? | List of commands with syntax |
| how do I add a student with tags? | Example using `add` with `t/` prefix |
| tell me how to search | Explanation of the `find` command |

Questions are detected automatically when your input starts with interrogative words (how, what, why, etc.) or ends with `?`.

### 3. Conversation Memory

The chat assistant remembers previous questions within the same session (up to 10 turns). Follow-up questions benefit from prior context:

1. "how do I add a student?" → AI explains the `add` command
2. "what about editing one?" → AI understands you're asking about `edit`, informed by the previous exchange

Memory resets when the app restarts.

## Architecture

```
User input (unknown command)
    |
    v
classifyInput() — question or command?
    |
    +--> QUESTION --> chat() --> ResultDisplay shows answer
    |                  |
    |                  v
    |            ChatMemory (sliding window, 20 messages)
    |
    +--> COMMAND --> translate() --> CommandBox pre-filled
```

### Key Files

| File | Purpose |
|---|---|
| `logic/ai/ChatModelFactory.java` | Creates LangChain4j OllamaChatModel (localhost:11434, qwen2.5:1.5b) |
| `logic/ai/NaturalLanguageParser.java` | Intent classification, command translation, and conversational chat |
| `ui/MainWindow.java` | Routes unknown input to AI on background thread |

## Configuration

Defaults (in `ChatModelFactory.java`):

| Setting | Default |
|---|---|
| Ollama URL | `http://localhost:11434` |
| Model | `qwen2.5:1.5b` |
| Timeout | 10 seconds |

To use a different model, call `ChatModelFactory.createChatModel(url, modelName, timeout)`.

## Dependencies

- **LangChain4j 0.36.2** — LLM framework (includes chat memory)
- **LangChain4j Ollama 0.36.2** — Ollama connector
- **Ollama** (external) — local LLM runtime

## Known Limitations

- **Latency:** 1–3 seconds per AI call (runs on background thread, UI stays responsive)
- **Model size:** qwen2.5:1.5b is fast but may struggle with complex inputs
- **No app state awareness:** AI doesn't know what students currently exist
- **Session-only memory:** Conversation history is lost on restart
