import pyttsx3

import os
from dotenv import load_dotenv
from text_to_speech import record_text, output_text
from barcode import get_input, lookup_sensor
from yapper import Yapper, GroqEnhancer, Persona
load_dotenv()
OPENAI_KEY = os.getenv('OPENAI_KEY')

from openai import OpenAI

client = OpenAI(
    api_key = OPENAI_KEY
)

def SpeakText(command):

    engine = pyttsx3.init()
    engine.say(command)
    engine.runAndWait()

#enhancer = GroqEnhancer(persona=Persona.FRIDAY)
#speaker = PiperSpeaker(voice=PiperVoiceUS.BRYCE)
yapper = Yapper()


def send_to_ChatGPT(messages, model='gpt-4o-mini'):

    try:
        response = client.chat.completions.create(
            model = model,
            messages=messages
        )
        return response.choices[0].message.content

    except Exception as e:
        return f"Error: {e}"


def main():
    input_var = input("Scan Sensor to learn more:\n")
    result = lookup_sensor(input_var)
    print(result)
    SpeakText(result)
    #yapper.yap("This is a test sentence.", plain=True)
    #output_text(text)
    assert False
    messages = [] #[{"role": "user", "content": }]
    #text = record_text()
    test_message = "What does IPCC mean"
    messages.append({"role": "user", "content": test_message})
    response = send_to_ChatGPT(messages)
    print(response)

main()
