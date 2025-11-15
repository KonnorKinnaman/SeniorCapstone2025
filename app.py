# app.py
import argparse
import sys

def run_chatgpt():
    try:
        import ChatGPTVA  # Jingwen's file: ChatGPTVA.py
    except Exception as e:
        print(f"[ChatGPT] Import error: {e}")
        return 1

    try:
        # ChatGPTVA already has its own input flow (text or speech)
        ChatGPTVA.main()
        return 0
    except SystemExit as e:
        # in case the other code calls sys.exit
        return int(e.code) if isinstance(e.code, int) else 1
    except Exception as e:
        print(f"[ChatGPT] Runtime error: {e}")
        return 1


def run_barcode(lang: str, voice_index: int, sensors: str, list_voices: bool):
    """
    Call barcode.py's main() while passing arguments by temporarily
    adjusting sys.argv. This avoids editing the original barcode.py.
    """
    try:
        import barcode  # the file: barcode.py
    except Exception as e:
        print(f"[Barcode] Import error: {e}")
        return 1

    # Build a fake argv for barcode.main()
    old_argv = sys.argv
    try:
        argv = ["barcode.py"]
        if list_voices:
            argv += ["--list-voices"]
            if lang:
                argv += ["--lang", lang]
        else:
            if lang:
                argv += ["--lang", lang]
            if voice_index is not None:
                argv += ["--voice-index", str(voice_index)]
            if sensors:
                argv += ["--sensors", sensors]

        sys.argv = argv
        # barcode.py defines main() with argparse; calling it directly is fine
        barcode.main()
        return 0
    except SystemExit as e:
        return int(e.code) if isinstance(e.code, int) else 1
    except Exception as e:
        print(f"[Barcode] Runtime error: {e}")
        return 1
    finally:
        sys.argv = old_argv


def main():
    parser = argparse.ArgumentParser(
        description="Launcher for ChatGPT mode or Barcode mode."
    )
    sub = parser.add_subparsers(dest="mode", required=False)

    # chat subcommand
    p_chat = sub.add_parser("chat", help="Use ChatGPT (text or speech).")
    # no extra flags; ChatGPTVA handles its own prompts

    # barcode subcommand
    p_bc = sub.add_parser("barcode", help="Scan barcode → speak description.")
    p_bc.add_argument("--lang", default="en_US", help="Piper language code (default: en_US)")
    p_bc.add_argument("--voice-index", type=int, default=0, help="Voice index within language (default: 0)")
    p_bc.add_argument("--sensors", default="sensors.json", help="Path to sensors database (default: sensors.json)")
    p_bc.add_argument("--list-voices", action="store_true", help="List voices (optionally filter with --lang)")

    # quick top-level convenience flags (work like defaults for barcode)
    parser.add_argument("--lang", help="Default language for barcode mode")
    parser.add_argument("--voice-index", type=int, help="Default voice index for barcode mode")
    parser.add_argument("--sensors", help="Default sensors.json path for barcode mode")

    args = parser.parse_args()

    # If no subcommand, show simple runtime menu
    if not args.mode:
        while True:
            choice = input("Choose mode: (1) ChatGPT  (2) Barcode: ").strip()
            if choice in ("", "1"):
                return run_chatgpt()
            elif choice == "2":
                # collect optional overrides interactively
                lang = args.lang or input("Piper lang code [en_US]: ").strip() or "en_US"
                vi_raw = input("Voice index [0]: ").strip()
                try:
                    vi = int(vi_raw) if vi_raw else 0
                except ValueError:
                    vi = 0
                sensors = args.sensors or "sensors.json"
                return run_barcode(lang=lang, voice_index=vi, sensors=sensors, list_voices=False)
            else:
                print("Please enter 1 or 2.")
    else:
        # subcommand path
        if args.mode == "chat":
            return run_chatgpt()
        elif args.mode == "barcode":
            lang = args.lang or "en_US"
            vi = args.voice_index if args.voice_index is not None else 0
            sensors = args.sensors or "sensors.json"
            return run_barcode(lang=lang, voice_index=vi, sensors=sensors, list_voices=args.list_voices)

    return 0


if __name__ == "__main__":
    raise SystemExit(main())