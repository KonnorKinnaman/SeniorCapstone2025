import os
from dotenv import load_dotenv
from barcode import lookup_sensor, normalize_scan
# If you want less robotic TTS:
from yapper import PiperSpeaker  # (and optionally enhancers/persona)

load_dotenv()

def speak_piper(text: str):
    speaker = PiperSpeaker()  # uses default voice; you can pick a specific one
    speaker.say(text)

def main():
    raw = input("Scan Sensor to learn more:\n")
    code = normalize_scan(raw)
    description = lookup_sensor(code)
    if not description:
        print(f"No sensor found for code: {code}")
        speak_piper("Sorry, I could not find a matching sensor.")
        return

    print(description)
    speak_piper(description)   # Less robotic than pyttsx3

    # If you also want to enhance/“de-roboticize” phrasing before speaking:
    # from yapper import GroqEnhancer, Persona
    # enhancer = GroqEnhancer(api_key=os.environ.get("groq_key"), persona=Persona.FRIDAY)
    # natural = enhancer.enhance(description)
    # speak_piper(natural)

if __name__ == "__main__":
    main()


def main():
    input_var = input("Scan Sensor to learn more:\n")
    result = lookup_sensor(input_var)
    print(result)
    SpeakText(result)
    # yapper.yap("This is a test sentence.", plain=True)
    # output_text(text)
    # assert False
    messages = [] #[{"role": "user", "content": }]
    # text = record_text()
    test_message = "What does IPCC mean"
    messages.append({"role": "user", "content": test_message})
    response = send_to_ChatGPT(messages)
    print(response)

main()


def SpeakText(command):

    engine = pyttsx3.init()
    engine.say(command)
    engine.runAndWait()

#enhancer = GroqEnhancer(persona=Persona.FRIDAY)
#speaker = PiperSpeaker(voice=PiperVoiceUS.BRYCE)