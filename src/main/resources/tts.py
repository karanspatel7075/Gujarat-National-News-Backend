from gtts import gTTS
import sys

input_file = sys.argv[1]
lang = sys.argv[2]
output = sys.argv[3]

with open(input_file, "r", encoding="utf-8") as f:
    text = f.read()

tts = gTTS(text=text, lang=lang)
tts.save(output)
