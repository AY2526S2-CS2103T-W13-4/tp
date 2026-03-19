# CoursePilot AI Assistant — Setup & Features

## Setup

### Basic (No API key needed)
The app includes built-in keyword matching that provides instant help responses. No setup required — just type naturally.

### Advanced (Google Gemini AI)
To enable AI-powered conversational assistance:

1. Go to [Google AI Studio](https://aistudio.google.com/apikey) and get a free API key
2. In CoursePilot, click **AI > AI Settings** in the menu bar
3. Paste your API key and click **Save**

The key is saved to your preferences and persists across sessions.

## How It Works

When you type something that isn't a recognized command, CoursePilot uses a three-tier approach:

```
User input (not a valid command)
    |
    v
1. Valid command? --> Execute normally
    |  (no)
    v
2. Keyword match? --> Show usage/help instantly
    |  (no match)
    v
3. Gemini configured? --> Ask Gemini AI (background thread)
    |  (no)
    v
4. Fallback message --> "Type 'help' to see available commands"
```

### Tier 1: Command Execution
Standard commands (`add`, `edit`, `delete`, etc.) are executed normally.

### Tier 2: Keyword Matching (Instant, No Network)
If the input contains recognizable keywords, the app returns the relevant command usage:

| You type | Response |
|---|---|
| how can I get started? | Getting started guide |
| how do I add a student? | `add` command usage and syntax |
| how to delete | `delete` command usage |
| how to search | `find` command usage |
| show me all | `list` command usage |

### Tier 3: Gemini AI (Requires API Key)
If no keyword matches and a Gemini API key is configured, the question is sent to Google Gemini for a conversational response. Features:
- Conversation memory (last 10 turns per session)
- Runs on a background thread (UI stays responsive)
- Knows all CoursePilot commands and can give examples

### Tier 4: Fallback
If nothing matches and Gemini isn't configured, a helpful fallback message is shown.

## Configuration

Click **AI > AI Settings** in the menu bar to:
- Open Google AI Studio to get an API key
- Enter/save your API key
- Clear your API key to disable AI features

The API key is stored in `preferences.json`.

## Architecture

### Key Files

| File | Purpose |
|---|---|
| `logic/ai/KeywordMatcher.java` | Maps keywords to command MESSAGE_USAGE responses |
| `logic/ai/NaturalLanguageParser.java` | Three-tier orchestrator: keyword → Gemini → fallback |
| `logic/ai/ChatModelFactory.java` | Creates LangChain4j Gemini model from API key |
| `ui/AiSettingsWindow.java` | UI for API key configuration |
| `ui/MainWindow.java` | Routes unknown commands to AI handler |

### Dependencies

- **LangChain4j 0.36.2** — LLM framework with chat memory
- **LangChain4j Google AI Gemini 0.36.2** — Gemini API connector
