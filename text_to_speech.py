print("Script started") # Debugging statement
import speech_recognition as sr

# Initialize Recognizer
r = sr.Recognizer()
# Improve pause and energy thresholds for long sentences and natural pauses
r.pause_threshold = 1.5  # Wait longer before considering speech ended (default is 0.8)
r.energy_threshold = 300  # You may need to tune this for your environment

def record_text():
    print("record_text() called") # Debugging statement
    while True:
        try:
            with sr.Microphone() as source2: 
                print("Adjusting for ambient noise...") 
                r.adjust_for_ambient_noise(source2, duration=1)  # Longer duration for better calibration
                print(f"Energy threshold set to: {r.energy_threshold}")
                print("I'm listening...")
                # Listen with a longer phrase time limit to allow long sentences
                audio2 = r.listen(source2, phrase_time_limit=20)
                print("Recognizing...")
                MyText = r.recognize_google(audio2)
                print(f"You said: {MyText}") 
                return MyText
        except sr.RequestError as e:
            print(f"Could not request results: {e}")
        except sr.UnknownValueError as e:
            print(f"Could not understand audio: {e}")
        except Exception as e:
            print(f"An unexpected error occurred: {e}")
        print("Please try speaking again.")
    return

def output_text(text):
    with open("output.txt", "a", encoding="utf-8") as f:
        f.write(text)
        f.write("\n")
    return

if __name__ == "__main__":
    print("Entering main loop") # Debugging statement
    while True:
        text = record_text()
        if text:
            output_text(text)