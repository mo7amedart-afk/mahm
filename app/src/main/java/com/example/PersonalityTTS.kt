package com.example

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class PersonalityTTS(
    private val context: Context,
    private val onInitResult: (Boolean) -> Unit
) {
    private var tts: TextToSpeech? = null
    var isInitialized = false
        private set

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeResult = tts?.setLanguage(Locale("ar"))
                if (localeResult == TextToSpeech.LANG_MISSING_DATA || localeResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("PersonalityTTS", "Arabic TTS is not supported or missing data on this system.")
                    isInitialized = false
                    onInitResult(false)
                } else {
                    // Make it a calming, steady pace representing natural coaching voice
                    tts?.setSpeechRate(0.82f)
                    tts?.setPitch(1.0f)
                    isInitialized = true
                    onInitResult(true)
                }
            } else {
                Log.e("PersonalityTTS", "TTS Initialization failed.")
                isInitialized = false
                onInitResult(false)
            }
        }
    }

    fun speak(text: String, utteranceId: String = "personality_audio") {
        if (isInitialized && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stop() {
        if (isInitialized && tts != null) {
            tts?.stop()
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("PersonalityTTS", "Error during shutdown: ${e.message}")
        }
    }
}
