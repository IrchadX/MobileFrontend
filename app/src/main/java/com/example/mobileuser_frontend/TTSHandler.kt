package com.example.mobileuser_frontend

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import com.example.mobileuser_frontend.data.model.DeviceData
import java.util.*

class TTSHandler(private val context: Context) {
    private val TAG = "TTSHandler"
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var currentLanguage = "fr"
    private var currentVoiceGender = "female"

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                Log.d(TAG, "TTS initialized successfully")

                // Check available languages
                checkAvailableLanguages()

                // Set language and voice
                setLanguage(currentLanguage)

                Log.d(TAG, "TTS setup completed")
            } else {
                Log.e(TAG, "TTS initialization failed with status: $status")
                isInitialized = false
            }
        }
    }

    private fun checkAvailableLanguages() {
        textToSpeech?.let { tts ->
            val frenchSupport = tts.isLanguageAvailable(Locale.FRENCH)
            val englishSupport = tts.isLanguageAvailable(Locale.ENGLISH)

            Log.d(TAG, "French language support: $frenchSupport")
            Log.d(TAG, "English language support: $englishSupport")
            Log.d(TAG, "Available voices: ${tts.voices?.size ?: 0}")

            // Log all available voices for debugging
            tts.voices?.forEach { voice ->
                Log.d(TAG, "Available voice: ${voice.name} - ${voice.locale} - Quality: ${voice.quality}")
            }
        }
    }

    fun setLanguage(languageCode: String) {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized yet, storing language: $languageCode")
            currentLanguage = languageCode
            return
        }

        currentLanguage = languageCode

        // Create locale with country code for better support
        val locale = when (languageCode) {
            "en" -> Locale("en", "US") // English (US)
            "fr" -> Locale("fr", "FR") // French (France)
            else -> Locale("fr", "FR") // Default to French
        }

        Log.d(TAG, "Attempting to set language to: $locale")

        val result = textToSpeech?.setLanguage(locale)
        when (result) {
            TextToSpeech.LANG_MISSING_DATA -> {
                Log.e(TAG, "Language data missing for $locale")
                // Try without country code
                val simpleLocale = Locale(languageCode)
                val fallbackResult = textToSpeech?.setLanguage(simpleLocale)
                Log.d(TAG, "Fallback language result: $fallbackResult for $simpleLocale")
            }
            TextToSpeech.LANG_NOT_SUPPORTED -> {
                Log.e(TAG, "Language not supported: $locale")
                // Try English as fallback
                if (languageCode != "en") {
                    Log.d(TAG, "Falling back to English")
                    textToSpeech?.setLanguage(Locale.ENGLISH)
                }
            }
            TextToSpeech.LANG_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                Log.d(TAG, "Language successfully set to: $locale")
                // Apply voice gender after language change
                setVoiceGender(currentVoiceGender)
            }
            else -> {
                Log.w(TAG, "Unknown language setting result: $result")
            }
        }
    }

    fun setVoiceGender(gender: String) {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized yet, storing voice gender: $gender")
            currentVoiceGender = gender
            return
        }

        currentVoiceGender = gender
        val voices = textToSpeech?.voices

        if (voices == null || voices.isEmpty()) {
            Log.w(TAG, "No voices available")
            return
        }

        // Get current locale
        val currentLocale = when (currentLanguage) {
            "en" -> Locale("en", "US")
            "fr" -> Locale("fr", "FR")
            else -> Locale("fr", "FR")
        }

        Log.d(TAG, "Setting voice gender: $gender for locale: $currentLocale")

        // Filter voices by language first
        val languageVoices = voices.filter { voice ->
            voice.locale.language == currentLocale.language
        }

        Log.d(TAG, "Found ${languageVoices.size} voices for language ${currentLocale.language}")

        if (languageVoices.isEmpty()) {
            Log.w(TAG, "No voices found for language: ${currentLocale.language}")
            return
        }

        // Try to find gender-specific voice
        val genderVoices = languageVoices.filter { voice ->
            val voiceName = voice.name.lowercase()
            when (gender) {
                "female" -> {
                    voiceName.contains("female") ||
                            voiceName.contains("woman") ||
                            voiceName.contains("femme") ||
                            voiceName.contains("feminine") ||
                            voice.name.contains("#female") ||
                            // Some common female voice patterns
                            voiceName.contains("alice") ||
                            voiceName.contains("marie") ||
                            voiceName.contains("claire")
                }
                "male" -> {
                    voiceName.contains("male") ||
                            voiceName.contains("man") ||
                            voiceName.contains("homme") ||
                            voiceName.contains("masculine") ||
                            voice.name.contains("#male") ||
                            // Some common male voice patterns
                            voiceName.contains("pierre") ||
                            voiceName.contains("jean") ||
                            voiceName.contains("paul")
                }
                else -> true
            }
        }

        // Select the best voice
        val selectedVoice: Voice? = when {
            genderVoices.isNotEmpty() -> {
                Log.d(TAG, "Found ${genderVoices.size} $gender voices")
                // Prefer higher quality voices
                genderVoices.maxByOrNull { it.quality }
            }
            else -> {
                Log.d(TAG, "No $gender voices found, using best available voice for language")
                languageVoices.maxByOrNull { it.quality }
            }
        }

        selectedVoice?.let { voice ->
            Log.d(TAG, "Attempting to set voice: ${voice.name} (${voice.locale})")
            val result = textToSpeech?.setVoice(voice)
            if (result == TextToSpeech.SUCCESS) {
                Log.d(TAG, "Voice set successfully: ${voice.name}")

                // Test the voice immediately
                val testText = when (currentLanguage) {
                    "en" -> "Voice changed successfully"
                    "fr" -> "Voix changée avec succès"
                    else -> "Test"
                }

                // Speak test after a short delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    speak(testText)
                }, 300)
            } else {
                Log.e(TAG, "Failed to set voice: ${voice.name}, result: $result")
            }
        } ?: run {
            Log.w(TAG, "No suitable voice found for language: $currentLanguage, gender: $gender")
        }
    }

    // Method to get available voices for debugging
    fun getAvailableVoices(): List<String> {
        if (!isInitialized) return emptyList()

        return textToSpeech?.voices?.map { voice ->
            "${voice.name} (${voice.locale}) - Quality: ${voice.quality}"
        } ?: emptyList()
    }

    // Method to check if a language is supported
    fun isLanguageSupported(languageCode: String): Boolean {
        if (!isInitialized) return false

        val locale = when (languageCode) {
            "en" -> Locale("en", "US")
            "fr" -> Locale("fr", "FR")
            else -> Locale(languageCode)
        }

        val result = textToSpeech?.isLanguageAvailable(locale)
        return result == TextToSpeech.LANG_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
    }

    // Method to update both language and voice from preferences
    fun updateFromPreferences(languageCode: String, voiceGender: String) {
        Log.d(TAG, "Updating TTS preferences: language=$languageCode, voice=$voiceGender")

        currentLanguage = languageCode
        currentVoiceGender = voiceGender

        if (isInitialized) {
            setLanguage(languageCode)
            // Voice will be set automatically after language is set
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized, cannot speak: $text")
            return
        }

        if (text.isBlank()) {
            Log.w(TAG, "Empty text provided for speaking")
            return
        }

        Log.d(TAG, "Speaking (${currentLanguage}): $text")

        // Use QUEUE_FLUSH to interrupt any current speech
        val result = textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        if (result == TextToSpeech.ERROR) {
            Log.e(TAG, "Error occurred while speaking text: $text")
        }
    }

    // Enhanced speak method with language parameter
    fun speak(text: String, languageCode: String) {
        // Temporarily switch language if different
        if (languageCode != currentLanguage) {
            val originalLanguage = currentLanguage
            setLanguage(languageCode)
            speak(text)
            // Switch back after a delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                setLanguage(originalLanguage)
            }, 2000)
        } else {
            speak(text)
        }
    }

    // Device information announcements
    fun announceBattery(batteryLevel: Int, language: String = currentLanguage) {
        val message = when (language) {
            "en" -> "Battery level is $batteryLevel percent"
            else -> "Le niveau de batterie est de $batteryLevel pour cent"
        }
        speak(message, language)
    }

    fun announceDeviceState(state: String, language: String = currentLanguage) {
        val translatedState = when (language) {
            "en" -> when (state.lowercase()) {
                "connecté", "connected" -> "connected"
                "déconnecté", "disconnected", "deconnected" -> "disconnected"
                "en charge", "charging" -> "charging"
                "faible batterie", "low battery" -> "low battery"
                "actif", "active" -> "active"
                "inactif", "inactive" -> "inactive"
                "en veille", "standby" -> "standby"
                else -> state
            }
            else -> when (state.lowercase()) {
                "connected", "connecté" -> "connecté"
                "disconnected", "déconnecté", "deconnected" -> "déconnecté"
                "charging", "en charge" -> "en charge"
                "low battery", "faible batterie" -> "faible batterie"
                "active", "actif" -> "actif"
                "inactive", "inactif" -> "inactif"
                "standby", "en veille" -> "en veille"
                else -> state
            }
        }

        val message = when (language) {
            "en" -> "Device state is $translatedState"
            else -> "L'état de l'appareil est $translatedState"
        }
        speak(message, language)
    }

    fun announceDeviceType(type: String, language: String = currentLanguage) {
        val translatedType = when (language) {
            "en" -> when (type.lowercase()) {
                "bracelet" -> "bracelet"
                "montre" -> "watch"
                "capteur" -> "sensor"
                "téléphone" -> "phone"
                "tablette" -> "tablet"
                else -> type
            }
            else -> when (type.lowercase()) {
                "bracelet" -> "bracelet"
                "watch", "montre" -> "montre"
                "sensor", "capteur" -> "capteur"
                "phone", "téléphone" -> "téléphone"
                "tablet", "tablette" -> "tablette"
                else -> type
            }
        }

        val message = when (language) {
            "en" -> "Device type is $translatedType"
            else -> "Le type d'appareil est $translatedType"
        }
        speak(message, language)
    }

    fun announceDeviceInfo(deviceData: DeviceData?, language: String = currentLanguage) {
        if (deviceData == null) {
            val message = when (language) {
                "en" -> "No device information available"
                else -> "Aucune information d'appareil disponible"
            }
            speak(message, language)
            return
        }

        // Translate device type and state
        val translatedType = when (language) {
            "en" -> when (deviceData.type.lowercase()) {
                "bracelet" -> "bracelet"
                "montre" -> "watch"
                "capteur" -> "sensor"
                "téléphone" -> "phone"
                "tablette" -> "tablet"
                else -> deviceData.type
            }
            else -> when (deviceData.type.lowercase()) {
                "watch", "montre" -> "montre"
                "sensor", "capteur" -> "capteur"
                "phone", "téléphone" -> "téléphone"
                "tablet", "tablette" -> "tablette"
                else -> deviceData.type
            }
        }

        val translatedState = when (language) {
            "en" -> when (deviceData.state.lowercase()) {
                "connecté", "connected" -> "connected"
                "déconnecté", "disconnected", "deconnected" -> "disconnected"
                "en charge", "charging" -> "charging"
                "faible batterie", "low battery" -> "low battery"
                else -> deviceData.state
            }
            else -> when (deviceData.state.lowercase()) {
                "connected", "connecté" -> "connecté"
                "disconnected", "déconnecté", "deconnected" -> "déconnecté"
                "charging", "en charge" -> "en charge"
                "low battery", "faible batterie" -> "faible batterie"
                else -> deviceData.state
            }
        }

        val message = when (language) {
            "en" -> buildString {
                append("Device information: ")
                append("Type is $translatedType. ")
                append("State is $translatedState. ")
                append("Battery level is ${deviceData.battery_capacity} percent.")
            }
            else -> buildString {
                append("Informations de l'appareil: ")
                append("Type $translatedType. ")
                append("État $translatedState. ")
                append("Niveau de batterie ${deviceData.battery_capacity} pour cent.")
            }
        }
        speak(message, language)
    }

    fun announceConnectionStatus(isConnected: Boolean, language: String = currentLanguage) {
        val message = when (language) {
            "en" -> if (isConnected) "Device is connected" else "Device is not connected"
            else -> if (isConnected) "L'appareil est connecté" else "L'appareil n'est pas connecté"
        }
        speak(message, language)
    }

    fun announceError(error: String, language: String = currentLanguage) {
        val message = when (language) {
            "en" -> "Error: $error"
            else -> "Erreur: $error"
        }
        speak(message, language)
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
        Log.d(TAG, "TTS shutdown completed")
    }
}