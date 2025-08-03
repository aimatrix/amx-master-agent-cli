#!/bin/bash

# AIMatrix CLI Build Script
# Builds the project for all supported platforms

set -e

echo "🚀 Building AIMatrix CLI for all platforms..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "build.gradle.kts" ]; then
    print_error "build.gradle.kts not found. Please run this script from the project root."
    exit 1
fi

# Clean previous builds
print_status "Cleaning previous builds..."
./gradlew clean

# Build JVM version
print_status "Building JVM version..."
./gradlew jvmJar
if [ $? -eq 0 ]; then
    print_success "JVM version built successfully"
else
    print_error "JVM build failed"
    exit 1
fi

# Build Shadow JAR
print_status "Building Shadow JAR..."
./gradlew shadowJar
if [ $? -eq 0 ]; then
    print_success "Shadow JAR built successfully"
else
    print_error "Shadow JAR build failed"
    exit 1
fi

# Build Native binaries (if supported on current platform)
PLATFORM=$(uname -s)
ARCH=$(uname -m)

case "$PLATFORM" in
    "Darwin")
        print_status "Building macOS native binaries..."
        if [ "$ARCH" = "arm64" ]; then
            ./gradlew macosArm64Binaries
            print_success "macOS ARM64 binary built"
        else
            ./gradlew macosX64Binaries
            print_success "macOS x64 binary built"
        fi
        ;;
    "Linux")
        print_status "Building Linux native binary..."
        ./gradlew linuxX64Binaries
        print_success "Linux x64 binary built"
        ;;
    "MINGW"*|"MSYS"*|"CYGWIN"*)
        print_status "Building Windows native binary..."
        ./gradlew mingwX64Binaries
        print_success "Windows x64 binary built"
        ;;
    *)
        print_warning "Native binary build not supported on $PLATFORM, skipping..."
        ;;
esac

# Run tests
print_status "Running tests..."
./gradlew test
if [ $? -eq 0 ]; then
    print_success "All tests passed"
else
    print_error "Some tests failed"
    exit 1
fi

# Generate documentation
print_status "Generating documentation..."
./gradlew dokkaHtml
if [ $? -eq 0 ]; then
    print_success "Documentation generated"
else
    print_warning "Documentation generation failed"
fi

# Create distribution directory
DIST_DIR="dist"
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR/bin"
mkdir -p "$DIST_DIR/docs"
mkdir -p "$DIST_DIR/examples"

# Copy JVM JAR
print_status "Creating distribution..."
cp build/libs/aimatrix-cli-*.jar "$DIST_DIR/bin/" 2>/dev/null || true

# Copy native binaries if they exist
cp build/bin/*/releaseExecutable/* "$DIST_DIR/bin/" 2>/dev/null || true

# Copy documentation
cp README.md "$DIST_DIR/" 2>/dev/null || true
cp LICENSE "$DIST_DIR/" 2>/dev/null || true
cp -r build/docs/* "$DIST_DIR/docs/" 2>/dev/null || true

# Create launcher scripts
cat > "$DIST_DIR/bin/aimatrix" << 'EOF'
#!/bin/bash
# AIMatrix CLI Launcher Script

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Try native binary first
if [ -x "$SCRIPT_DIR/aimatrix-cli" ]; then
    exec "$SCRIPT_DIR/aimatrix-cli" "$@"
elif [ -x "$SCRIPT_DIR/AIMatrixMain.kexe" ]; then
    exec "$SCRIPT_DIR/AIMatrixMain.kexe" "$@"
elif [ -f "$SCRIPT_DIR/aimatrix-cli-1.0.0.jar" ]; then
    exec java -jar "$SCRIPT_DIR/aimatrix-cli-1.0.0.jar" "$@"
else
    echo "Error: No AIMatrix CLI executable found" >&2
    exit 1
fi
EOF

cat > "$DIST_DIR/bin/aimatrix.bat" << 'EOF'
@echo off
rem AIMatrix CLI Launcher Script for Windows

set SCRIPT_DIR=%~dp0

if exist "%SCRIPT_DIR%aimatrix-cli.exe" (
    "%SCRIPT_DIR%aimatrix-cli.exe" %*
) else if exist "%SCRIPT_DIR%AIMatrixMain.exe" (
    "%SCRIPT_DIR%AIMatrixMain.exe" %*
) else if exist "%SCRIPT_DIR%aimatrix-cli-1.0.0.jar" (
    java -jar "%SCRIPT_DIR%aimatrix-cli-1.0.0.jar" %*
) else (
    echo Error: No AIMatrix CLI executable found
    exit /b 1
)
EOF

chmod +x "$DIST_DIR/bin/aimatrix"

# Create example configuration
cat > "$DIST_DIR/examples/config.json" << 'EOF'
{
  "general": {
    "workingDirectory": ".",
    "logLevel": "INFO"
  },
  "ai": {
    "providers": {
      "gemini": {
        "apiKey": "${GEMINI_API_KEY}",
        "model": "gemini-pro",
        "enabled": true
      },
      "openai": {
        "apiKey": "${OPENAI_API_KEY}",
        "model": "gpt-4",
        "enabled": true
      },
      "claude": {
        "apiKey": "${CLAUDE_API_KEY}",
        "model": "claude-3-opus",
        "enabled": true
      },
      "deepseek": {
        "apiKey": "${DEEPSEEK_API_KEY}",
        "model": "deepseek-coder",
        "enabled": true
      }
    },
    "modelSelection": {
      "algorithm": "CONTEXTUAL_BANDIT",
      "costWeight": 0.3,
      "performanceWeight": 0.7
    }
  },
  "cache": {
    "maxMemorySize": 268435456,
    "defaultTTL": "1h"
  },
  "plugins": {
    "enabled": true,
    "autoLoad": true
  }
}
EOF

# Create installation script
cat > "$DIST_DIR/install.sh" << 'EOF'
#!/bin/bash
# AIMatrix CLI Installation Script

set -e

INSTALL_DIR="/usr/local/bin"
CONFIG_DIR="$HOME/.aimatrix"

echo "Installing AIMatrix CLI..."

# Check if running as root for system-wide installation
if [ "$EUID" -eq 0 ]; then
    echo "Installing system-wide to $INSTALL_DIR"
else
    INSTALL_DIR="$HOME/.local/bin"
    echo "Installing to user directory: $INSTALL_DIR"
    mkdir -p "$INSTALL_DIR"
fi

# Copy executable
if [ -x "bin/aimatrix" ]; then
    cp bin/aimatrix "$INSTALL_DIR/"
    echo "Installed launcher script to $INSTALL_DIR/aimatrix"
fi

# Copy JAR if no native binary exists
if [ -f "bin/aimatrix-cli-1.0.0.jar" ] && [ ! -x "bin/aimatrix-cli" ]; then
    cp bin/aimatrix-cli-1.0.0.jar "$INSTALL_DIR/"
fi

# Create config directory
mkdir -p "$CONFIG_DIR"

# Copy example config if none exists
if [ ! -f "$CONFIG_DIR/config.json" ] && [ -f "examples/config.json" ]; then
    cp examples/config.json "$CONFIG_DIR/"
    echo "Created example configuration at $CONFIG_DIR/config.json"
fi

echo "Installation complete!"
echo ""
echo "To get started:"
echo "1. Set your API keys as environment variables:"
echo "   export GEMINI_API_KEY=your_key_here"
echo "   export OPENAI_API_KEY=your_key_here"
echo "   export CLAUDE_API_KEY=your_key_here"
echo "   export DEEPSEEK_API_KEY=your_key_here"
echo ""
echo "2. Run: aimatrix --help"
echo ""
echo "For more information, see: https://github.com/aimatrix/amx-master-agent-cli"
EOF

chmod +x "$DIST_DIR/install.sh"

# Create archive
print_status "Creating distribution archive..."
tar -czf "aimatrix-cli-$PLATFORM-$(date +%Y%m%d).tar.gz" -C "$DIST_DIR" .

print_success "Build completed successfully!"
print_status "Distribution created in: $DIST_DIR/"
print_status "Archive created: aimatrix-cli-$PLATFORM-$(date +%Y%m%d).tar.gz"

# Show build summary
echo ""
echo "📦 Build Summary:"
echo "=================="
echo "• JVM JAR: $(ls build/libs/aimatrix-cli-*.jar 2>/dev/null | wc -l) file(s)"
echo "• Native binaries: $(ls build/bin/*/releaseExecutable/* 2>/dev/null | wc -l) file(s)"
echo "• Documentation: $([ -d build/docs ] && echo "Generated" || echo "Not generated")"
echo "• Tests: $(./gradlew test --dry-run 2>/dev/null | grep -c ":test" || echo "Unknown") test task(s)"
echo ""
echo "🎉 AIMatrix CLI is ready for distribution!"