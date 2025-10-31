import argparse
import json
import os
from typing import Dict, Optional

from yapper import PiperSpeaker
from yapper.constants import piper_enum_to_lang_code  # voice enums -> lang codes


# ---------- Sensors table (JSON-backed) ----------
def load_sensors(json_path: str = "sensors.json") -> Dict[str, str]:
    """
    Load sensor code -> description mapping from sensors.json.
    If file doesn't exist, create a starter file with a couple entries.
    """
    if not os.path.exists(json_path):
        starter = {
            #TODO Add more sensors to this list
            "2540": "Level Sensor: The Ultrasonic Level Sensor is used to measure the volume of liquid in a space using ultrasonic sound.",
            "2543": "Coriolis Sensor: A Coriolis Sensor measures liquid flow rate using vibrating tubes; the phase shift is proportional to mass flow.",
        }
        with open(json_path, "w", encoding="utf-8") as f:
            json.dump(starter, f, indent=2, ensure_ascii=False)
        return starter

    with open(json_path, "r", encoding="utf-8") as f:
        return json.load(f)


def lookup_sensor(code: str, table: Dict[str, str]) -> Optional[str]:
    return table.get(code)


# ---------- Voice selection helpers ----------
def list_voices(lang_filter: Optional[str] = None) -> list[tuple[str, str]]:
    """
    Returns a list of (lang_code, voice_name) for available Piper voices.
    If lang_filter is provided (e.g., 'en_US'), only those are returned.
    """
    results = []
    for voice_enum, lang_code in piper_enum_to_lang_code.items():
        if lang_filter and lang_code != lang_filter:
            continue
        for voice in voice_enum:
            results.append((lang_code, voice.value))
    return results


def pick_voice(lang_code: str = "en_US", voice_index: int = 0):
    """
    Pick the Nth voice for a given language code. If none found, returns None.
    """
    idx = 0
    for voice_enum, code in piper_enum_to_lang_code.items():
        if code == lang_code:
            for voice in voice_enum:
                if idx == voice_index:
                    return voice
                idx += 1
    return None


# ---------- Speaker wrapper ----------
def build_speaker(lang_code: str, voice_index: int) -> PiperSpeaker:
    v = pick_voice(lang_code, voice_index)
    return PiperSpeaker(voice=v) if v else PiperSpeaker()


def speak(speaker: PiperSpeaker, text: str):
    try:
        speaker.say(text)
    except Exception as e:
        print(f"[TTS error] {e}")


# ---------- Main CLI ----------
def main():
    parser = argparse.ArgumentParser(description="Barcode → spoken description")
    parser.add_argument("--list-voices", action="store_true",
                        help="List available voices (optionally filter with --lang)")
    parser.add_argument("--lang", default="en_US",
                        help="Language code for voices (default: en_US)")
    parser.add_argument("--voice-index", type=int, default=0,
                        help="Index of the voice within the chosen language (default: 0)")
    parser.add_argument("--sensors", default="sensors.json",
                        help="Path to sensors JSON (default: sensors.json)")
    args = parser.parse_args()

    if args.list_voices:
        voices = list_voices(args.lang)
        if not voices:
            print(f"No voices found for language {args.lang}. Try a different --lang or omit to list all.")
            return
        print(f"Voices for {args.lang}:")
        # show index numbers for convenience
        current_index = 0
        for lang_code, vname in voices:
            print(f"[{current_index}] {lang_code} -> {vname}") #python barcode.py --list-voices --lang en_US, en_NG
                                #The most British sounding accent.  #python barcode.py --lang en_GB --voice-index 6
            current_index += 1  #The most English accent is 7 (men), 5 (men) sounds cool, 
        return

    # Build TTS
    speaker = build_speaker(args.lang, args.voice_index)

    # Load sensors
    table = load_sensors(args.sensors)

    # Read a scan as raw digits (no hex decoding)
    raw = input("Scan any of the barcodes to learn more:\n").strip()
    description = lookup_sensor(raw, table)

    if description:
        print(description)
        speak(speaker, description)
    else:
        msg = f"Sorry, no sensor found for code. {raw}."
        print(msg)
        speak(speaker, msg)

if __name__ == "__main__":
    main()