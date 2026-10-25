#!/usr/bin/env python3
"""
Telegram LangPack Downloader
=============================
Downloads official Telegram translation strings via the MTProto API
and converts them to Android strings.xml format.

No user login required — langpack API is available without authentication.

Usage:
    python3 download_langpack.py [--lang LANG_CODE] [--output OUTPUT_DIR]

Examples:
    python3 download_langpack.py --lang he
    python3 download_langpack.py --lang he --output ./output
    python3 download_langpack.py --list
"""

import argparse
import asyncio
import os
import sys

try:
    from telethon import TelegramClient
    from telethon.tl.functions.langpack import GetLangPackRequest, GetLanguagesRequest
    from telethon.tl.types import (
        LangPackString,
        LangPackStringPluralized,
        LangPackStringDeleted,
    )
except ImportError:
    print("Error: Telethon is not installed.")
    print("Install it with: pip install telethon")
    print("Or in a venv:    python3 -m venv venv && source venv/bin/activate && pip install telethon")
    sys.exit(1)

# Telegram API credentials (fill in your own from my.telegram.org)
API_ID = 0
API_HASH = ""

# Session file will be created in the script's directory
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
SESSION_FILE = os.path.join(SCRIPT_DIR, "langpack_session")


def escape_android_string(s: str) -> str:
    """Escape a string for use in Android strings.xml."""
    if s is None:
        return ""
    
    # Handle XML entities
    s = s.replace("&", "&amp;")
    s = s.replace("<", "&lt;")
    s = s.replace(">", "&gt;")
    s = s.replace('"', '\\"')
    s = s.replace("'", "\\'")
    s = s.replace("\n", "\\n")
    
    # Android-specific escapes
    if s.startswith("@"):
        s = "\\@" + s[1:]
    if s.startswith("?"):
        s = "\\?" + s[1:]
    
    return s


def build_strings_xml(strings: list, lang_code: str) -> str:
    """Convert a list of Telegram LangPackString objects to Android strings.xml format."""
    
    lines = []
    lines.append('<?xml version="1.0" encoding="utf-8"?>')
    lines.append(f'<!-- Telegram official {lang_code} translation, downloaded via langpack API -->')
    lines.append('<resources>')
    
    regular_count = 0
    plural_count = 0
    deleted_count = 0
    
    for s in strings:
        if isinstance(s, LangPackString):
            escaped = escape_android_string(s.value)
            lines.append(f'    <string name="{s.key}">{escaped}</string>')
            regular_count += 1
            
        elif isinstance(s, LangPackStringPluralized):
            lines.append(f'    <plurals name="{s.key}">')
            if s.zero_value is not None:
                lines.append(f'        <item quantity="zero">{escape_android_string(s.zero_value)}</item>')
            if s.one_value is not None:
                lines.append(f'        <item quantity="one">{escape_android_string(s.one_value)}</item>')
            if s.two_value is not None:
                lines.append(f'        <item quantity="two">{escape_android_string(s.two_value)}</item>')
            if s.few_value is not None:
                lines.append(f'        <item quantity="few">{escape_android_string(s.few_value)}</item>')
            if s.many_value is not None:
                lines.append(f'        <item quantity="many">{escape_android_string(s.many_value)}</item>')
            if s.other_value is not None:
                lines.append(f'        <item quantity="other">{escape_android_string(s.other_value)}</item>')
            lines.append(f'    </plurals>')
            plural_count += 1
            
        elif isinstance(s, LangPackStringDeleted):
            deleted_count += 1
    
    lines.append('</resources>')
    
    print(f"\nStatistics:")
    print(f"  Regular strings: {regular_count}")
    print(f"  Plural strings:  {plural_count}")
    print(f"  Deleted strings: {deleted_count} (skipped)")
    print(f"  Total written:   {regular_count + plural_count}")
    
    return "\n".join(lines) + "\n"


async def list_languages(client):
    """List all available languages."""
    result = await client(GetLanguagesRequest(lang_pack="android"))
    print(f"\nAvailable languages ({len(result)}):\n")
    print(f"{'Code':<10} {'Name':<25} {'Native Name':<25} {'RTL':<5} {'Strings':<10}")
    print("-" * 75)
    for lang in sorted(result, key=lambda x: x.lang_code):
        print(f"{lang.lang_code:<10} {lang.name:<25} {lang.native_name:<25} {'Yes' if lang.rtl else 'No':<5} {lang.strings_count:<10}")


async def download_langpack(client, lang_code: str, output_dir: str):
    """Download a language pack and save it as strings.xml."""
    
    print(f"Downloading langpack for '{lang_code}' (lang_pack='android')...")
    
    try:
        result = await client(GetLangPackRequest(
            lang_pack="android",
            lang_code=lang_code
        ))
    except Exception as e:
        print(f"Error downloading langpack: {e}")
        print(f"\nTip: Use --list to see available language codes.")
        return False
    
    print(f"Received {len(result.strings)} strings (version {result.version})")
    
    # Build XML
    xml_content = build_strings_xml(result.strings, lang_code)
    
    # Determine output path
    os.makedirs(output_dir, exist_ok=True)
    output_file = os.path.join(output_dir, "strings.xml")
    
    with open(output_file, "w", encoding="utf-8") as f:
        f.write(xml_content)
    
    print(f"\nSaved to: {output_file}")
    print(f"File size: {os.path.getsize(output_file):,} bytes")
    
    return True


async def main():
    parser = argparse.ArgumentParser(
        description="Download Telegram official translations via MTProto API"
    )
    parser.add_argument(
        "--lang", "-l",
        default="he",
        help="Language code to download (default: he)"
    )
    parser.add_argument(
        "--output", "-o",
        default=None,
        help="Output directory (default: ./output/<lang_code>)"
    )
    parser.add_argument(
        "--list",
        action="store_true",
        help="List all available languages and exit"
    )
    args = parser.parse_args()
    
    if args.output is None:
        args.output = os.path.join(SCRIPT_DIR, "output", args.lang)
    
    print("Connecting to Telegram (no login required)...")
    
    client = TelegramClient(SESSION_FILE, API_ID, API_HASH)
    
    # Just connect — do NOT start() which forces user login
    # langpack API calls don't require authentication
    await client.connect()
    
    try:
        if args.list:
            await list_languages(client)
        else:
            await download_langpack(client, args.lang, args.output)
    except Exception as e:
        print(f"\nError: {e}")
        print("\nIf you see 'ApiIdInvalidError', the API credentials may be invalid.")
        print("You can get your own at https://my.telegram.org")
    finally:
        await client.disconnect()


if __name__ == "__main__":
    asyncio.run(main())
