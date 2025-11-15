from openai import OpenAI
import os
from pathlib import Path
import numpy as np
import msvcrt
import time

# Make optional audio libraries defensive so the script can run without them
HAVE_SOUNDDEVICE = True
HAVE_WAVIO = True
HAVE_PYGAME = True

try:
    import sounddevice as sd
except ImportError:
    sd = None
    HAVE_SOUNDDEVICE = False

try:
    import wavio
except ImportError:
    wavio = None
    HAVE_WAVIO = False

try:
    import pygame
except ImportError:
    pygame = None
    HAVE_PYGAME = False

def load_env_file():
    """Load OPENAI_KEY from .env file if present."""
    env_path = Path(__file__).parent / '.env'
    if not env_path.exists():
        return
    
    for line in env_path.read_text(encoding='utf-8').splitlines():
        if '=' not in line or line.startswith('#'):
            continue
        key, value = line.strip().split('=', 1)
        key = key.strip()
        value = value.strip().strip("'").strip('"')
        if key:
            os.environ.setdefault(key, value)

def get_api_key():
    """Get OpenAI API key from environment."""
    key = os.getenv('OPENAI_API_KEY') or os.getenv('OPENAI_KEY')
    if not key:
        raise ValueError(
            "OpenAI API key not found. Either:\n"
            "1. Set OPENAI_API_KEY environment variable:\n"
            "   $env:OPENAI_API_KEY='your-key'\n"
            "2. Add OPENAI_KEY=your-key to .env file\n"
        )
    return key

# Configuration
load_env_file()  # Load API key from .env if present
MODEL = "gpt-4"  # or "gpt-3.5-turbo" for faster responses

# --- 4. Send text prompt to ChatGPT ---
def ask_chatgpt(prompt: str) -> str:
    """Send prompt to ChatGPT and return response.
    
    Args:
        prompt: Text prompt to send to ChatGPT
        
    Returns:
        Assistant's text response
        
    Raises:
        ValueError: If API key is missing
        Exception: For API or other errors
    """
    api_key = get_api_key()
    client = OpenAI(api_key=api_key)
    
    messages = [
        {"role": "system", "content": "You are a helpful AI assistant that explains information clearly and concisely."},
        {"role": "user", "content": prompt}
    ]
    
    response = client.chat.completions.create(
        model=MODEL, 
        messages=messages
    )
    
    return response.choices[0].message.content.strip()