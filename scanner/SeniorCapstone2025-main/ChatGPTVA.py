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
SAMPLE_RATE = 44100  # Audio sample rate
CHANNELS = 1  # Mono audio
DURATION = 5  # Default recording duration in seconds
AUDIO_FILE = "user_input.wav"  # Temporary file for audio

def record_audio():
    """Record audio from microphone until 's' is pressed again.
    
    Returns:
        numpy array of audio data
    """
    if not HAVE_SOUNDDEVICE:
        raise ImportError("sounddevice package is required for audio recording")
    
    # Initialize list to store audio chunks
    recorded_chunks = []
    recording = True
    
    def callback(indata, frames, time, status):
        """This function is called for each audio block."""
        if status:
            print(status)
        if recording:
            recorded_chunks.append(indata.copy())
    
    # Create an input stream
    stream = sd.InputStream(
        samplerate=SAMPLE_RATE,
        channels=CHANNELS,
        callback=callback
    )
    
    print("\nRecording... Press 's' to stop")
    
    # Clear any pending keystrokes
    while msvcrt.kbhit():
        msvcrt.getch()
    
    with stream:
        while recording:
            if msvcrt.kbhit():
                key = msvcrt.getch().decode('utf-8').lower()
                if key == 's':
                    recording = False
            time.sleep(0.1)  # Small sleep to prevent high CPU usage
    
    print("Recording complete!")
    
    if not recorded_chunks:
        raise RuntimeError("No audio was recorded")
    
    # Combine all recorded chunks into one array
    return np.concatenate(recorded_chunks)

def save_audio(audio_data, filename=AUDIO_FILE):
    """Save audio data to WAV file.
    
    Args:
        audio_data: numpy array of audio data
        filename: output WAV file path
    """
    if not HAVE_WAVIO:
        raise ImportError("wavio package is required for saving audio")
    
    wavio.write(filename, audio_data, SAMPLE_RATE, sampwidth=2)

def transcribe_audio(filename=AUDIO_FILE):
    """Transcribe audio file using OpenAI Whisper API.
    
    Args:
        filename: path to WAV file
        
    Returns:
        transcribed text
    """
    client = OpenAI(api_key=get_api_key())
    
    with open(filename, "rb") as audio_file:
        transcript = client.audio.transcriptions.create(
            model="whisper-1",
            file=audio_file
        )
    return transcript.text

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

def main():
    """Main program flow implementing the 4 core requirements."""
    try:
        # Check for required audio packages
        can_do_audio = HAVE_SOUNDDEVICE and HAVE_WAVIO
        if not can_do_audio:
            print('\nNote: Audio features require sounddevice and wavio packages.')
            print('To enable audio features, install them with:')
            print('pip install sounddevice wavio')
            print()
        
        # Ask user for input method
        while True:
            method = input("Choose input method (t)ext or (s)peech [t]: ").lower()
            if method in ('', 't'):
                # Text input
                prompt = input("\nWhat would you like to ask ChatGPT?\n")
                break
            elif method == 's':
                if not can_do_audio:
                    print("Speech input is not available without audio packages.")
                    continue
                
                # Speech input
                try:
                    print("Starting recording when you press 's'...")
                    while not msvcrt.kbhit() or msvcrt.getch().decode('utf-8').lower() != 's':
                        time.sleep(0.1)
                    
                    audio_data = record_audio()  # Will record until 's' is pressed again
                    save_audio(audio_data)
                    prompt = transcribe_audio()
                    print(f"\nTranscribed text: {prompt}")
                    break
                except Exception as e:
                    print(f"Error recording/transcribing audio: {e}")
                    continue
            else:
                print("Please choose 't' for text or 's' for speech")
        
        # 2. Send prompt to ChatGPT
        # 3. Return response to variable 
        response = ask_chatgpt(prompt)
        
        # 4. Print the response
        print("\nChatGPT's response:\n")
        print(response)
        
    except ValueError as e:
        # Handle missing API key
        print(f"Error: {e}")
    except Exception as e:
        # Handle other errors
        print(f"An error occurred: {e}")
