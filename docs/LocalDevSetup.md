# Local Development Setup

## Known Issues & Fixes

### 1. Wrong Java version (Java 25 instead of Java 17)

**Symptom:** `Unsupported class file major version 69` or Gradle errors on startup.

**Cause:** Your default `JAVA_HOME` points to Java 25, but the project requires Java 17.

**Fix:** Always run Gradle with Java 17:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home ./gradlew run
```

Or set it permanently in your shell profile (`~/.zshrc`):

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
```

### 2. JavaFX architecture mismatch on Apple Silicon

**Symptom:**
```
incompatible architecture (have 'x86_64', need 'arm64e' or 'arm64')
Error initializing QuantumRenderer: no suitable pipeline found
```

**Cause:** The upstream `build.gradle` lists JavaFX JARs for all platforms (`win`, `mac`, `linux`) with hardcoded classifiers. The `mac` classifier only provides x86_64 native libraries. On Apple Silicon Macs, the ARM64 `mac-aarch64` classifier is needed instead. When both `mac` and `mac-aarch64` JARs are on the classpath together, JavaFX extracts the x86_64 natives first and fails.

**Fix:** The `build.gradle` now uses runtime platform detection to include only the correct JavaFX classifier for the current OS and architecture. This ensures only one set of native libraries is on the classpath. After switching to this approach, clear the stale cache once:

```bash
rm -rf ~/.openjfx/cache
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home ./gradlew clean run
```

**Why platform detection is safe for CI:** The CI workflow (`gradle.yml`) uses `setup-java` with `jdk+fx`, which provides the correct platform-specific JavaFX. The platform detection resolves to `linux` on Ubuntu runners, `win` on Windows runners, and `mac-aarch64` on macOS runners (GitHub's `macos-latest` is ARM).

### 3. Stale `config.json` with corrupted paths

**Symptom:**
```
java.nio.file.NoSuchFileException: file:/Users/.../preferences.json
```

**Cause:** A previous run wrote `config.json` with a `file:` URI path instead of a plain path.

**Fix:** Delete it and let the app regenerate:

```bash
rm config.json
```

## Quick Start (Apple Silicon Mac)

```bash
rm -rf ~/.openjfx/cache config.json
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home ./gradlew clean run
```

## Before Committing

Always run checkstyle and tests before committing:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home ./gradlew checkstyleMain checkstyleTest test
```

This catches:
- Unused imports
- Import ordering violations (no blank line between `dev.*` and `seedu.*`)
- `SeparatorWrap` violations (e.g. `(` must be on previous line)
- Line length > 120 characters
