# ArabicChatFix Plugin

Automatically converts Arabic text so Bedrock players can read it properly, while Java players see the original text unchanged.

## Features
- ✅ Automatic Arabic text detection
- ✅ Converts Arabic for Bedrock players only
- ✅ Java players see normal Arabic text (RTL)
- ✅ No commands needed - works automatically
- ✅ Works with Geyser + Floodgate

## Requirements
- Spigot/Paper 1.19+ server
- Geyser plugin
- Floodgate plugin (to detect Bedrock players)

## Installation

1. **Build the plugin:**
   ```bash
   mvn clean package
   ```
   The plugin JAR will be in `target/ArabicChatFix.jar`

2. **Install on your server:**
   - Copy `ArabicChatFix.jar` to your server's `plugins/` folder
   - Make sure Geyser and Floodgate are already installed
   - Restart your server

## How It Works

When a player types a message:
1. Plugin checks if the message contains Arabic characters
2. If YES:
   - **Bedrock players** receive the text converted (reversed + mapped characters)
   - **Java players** receive the original Arabic text (normal RTL)
3. If NO Arabic:
   - Everyone sees the message normally

## Example

**Player types:** مرحبا

**Java players see:** مرحبا (normal RTL Arabic)

**Bedrock players see:** ابحرم (reversed visual form that displays correctly in Bedrock)

## Technical Details

The plugin:
- Maps Arabic characters to their isolated visual forms
- Reverses Arabic text for Bedrock's LTR-only rendering
- Uses Floodgate API to detect Bedrock players
- Handles mixed Arabic/English text properly

## Support

If you have issues:
1. Make sure Floodgate is installed and working
2. Check server logs for errors
3. Test with a simple Arabic message like "مرحبا"

## License
MIT License

Copyright (c) 2026 Sgsysysgsgsg

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
