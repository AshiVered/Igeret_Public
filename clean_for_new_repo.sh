#!/usr/bin/env bash
# ==============================================================================
# Script: clean_for_new_repo.sh
# Purpose: Clean all personal secrets, credentials, caches, and build trash
#          before committing to a brand-new repository.
# Note: Does NOT touch any services.json files or the custom PackageName!
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "🧹 Starting repository cleanup for new clean repo..."
echo "=========================================================="

# 1. Remove Sensitive Local Files
echo "--> [1/5] Removing personal keys and session files..."
rm -f api-keys
rm -f local.properties
rm -f tools/langpack_downloader/*.session
rm -f tools/langpack_downloader/*.session-journal

# 2. Remove Build Artifacts & Compiled Caches
echo "--> [2/5] Removing build artifacts and caches..."
rm -rf .gradle
rm -rf buildSrc/.gradle
rm -rf buildSrc/build
rm -rf obj
rm -rf .cxx
rm -rf TMessagesProj/.cxx
rm -rf TMessagesProj/jniLibs
rm -rf TMessagesProj/jniRes

# Remove all module 'build' and 'afat' directories
find . -type d -name "build" -not -path "*/.git/*" -exec rm -rf {} + 2>/dev/null || true
find . -type d -name "afat" -not -path "*/.git/*" -exec rm -rf {} + 2>/dev/null || true

# 3. Remove IDE and OS Trash
echo "--> [3/5] Removing IDE settings and OS junk..."
rm -rf .idea
rm -f *.iml
rm -f build.log
find . -type f -name "*.apk" -delete 2>/dev/null || true
find . -type f -name "*.aab" -delete 2>/dev/null || true
find . -type f -name ".DS_Store" -delete 2>/dev/null || true
find . -type f -name "Thumbs.db" -delete 2>/dev/null || true

# 4. Remove Python Virtual Environments & Output
echo "--> [4/5] Removing Python venv and temp output..."
rm -rf tools/langpack_downloader/venv
rm -rf tools/langpack_downloader/output
find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true

# 5. Clean historical warning from README.md (no longer relevant in fresh repo)
if [ -f "README.md" ]; then
    echo "--> [5/5] Cleaning outdated API keys warning from README.md..."
    sed -i '/IMPORTANT! dont public this repo, because API keys is aviable in commit history!/d' README.md
fi

echo ""
echo "=========================================================="
echo "✅ Cleanup completed successfully!"
echo "=========================================================="
echo "📌 Next steps:"
echo " 1. Restore your dummy google-services.json files."
echo " 2. Run: git add ."
echo " 3. Verify with: git status"
echo " 4. Commit: git commit -m 'Initial clean commit'"
echo "=========================================================="
