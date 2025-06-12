package com.example.mobileuser_frontend

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.navigation.NavController
import com.example.mobileuser_frontend.functions.fetchDeviceInfo
import com.example.mobileuser_frontend.functions.makePhoneCall
import com.example.mobileuser_frontend.module.RetrofitClient
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class VoiceCommandHandler(private val context: Context) {

    private var ttsHandler: TTSHandler? = null
    private val TAG = "VoiceCommandHandler"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var navController: NavController? = null
    private val sharedPref: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private var webSocketManager: WebSocketManager? = null

    // Add AuthRepository to get userId consistently
    private val authRepository = AuthRepository(context)

    init {
        ttsHandler = TTSHandler(context)
        loadTTSPreferences()
    }

    // Load TTS preferences and apply them
    private fun loadTTSPreferences() {
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val languageCode = sharedPref.getString("selected_language", "fr") ?: "fr"
        val voiceGender = sharedPref.getString("selected_voice", "female") ?: "female"

        Log.d("VoiceCommandHandler", "Loading TTS preferences: language=$languageCode, voice=$voiceGender")
        ttsHandler?.updateFromPreferences(languageCode, voiceGender)
    }

    // Call this method when language changes in preferences


    // Call this method when voice preference changes
    fun onVoiceChanged() {
        Log.d("VoiceCommandHandler", "Voice changed, reloading TTS preferences")
        loadTTSPreferences()
    }

    // Method to test voice with current settings
    fun testVoice() {
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val languageCode = sharedPref.getString("selected_language", "fr") ?: "fr"

        val testMessage = when (languageCode) {
            "en" -> "Voice test successful"
            else -> "Test de voix réussi"
        }

        ttsHandler?.speak(testMessage)
    }

    private fun sendLanguageUpdateToServer() {
        // Your existing server communication code here
        // This sends the language change to your Python server
    }

    fun setNavController(controller: NavController) {
        this.navController = controller
        Log.d(TAG, "NavController set")
    }

    fun setWebSocketManager(manager: WebSocketManager) {
        this.webSocketManager = manager
        Log.d(TAG, "WebSocket manager set - checking connection status")

        // Check if WebSocket is connected
        if (manager.isWebSocketConnected()) {
            Log.d(TAG, "WebSocket is connected, sending initial language update")
            sendLanguageUpdate()
        } else {
            Log.w(TAG, "WebSocket is not connected yet, language will be sent when connection is established")
        }
    }

    // FIXED: Updated function to match Python server expectations
    fun sendLanguageUpdate() {
        val currentLang = getCurrentLanguage()
        Log.d(TAG, "=== LANGUAGE UPDATE START ===")
        Log.d(TAG, "Attempting to send language update: $currentLang")

        webSocketManager?.let { manager ->
            // Check WebSocket connection status
            if (!manager.isWebSocketConnected()) {
                Log.w(TAG, "WebSocket not connected, attempting to reconnect...")
                manager.reconnect()

                // Schedule retry after reconnection attempt
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    retryLanguageUpdate(currentLang)
                }, 3000)
                return
            }

            // FIXED: Create the JSON message that exactly matches Python server expectations
            // Python expects: {"language": "fr"} or {"language": "en"}
            val languageMessage = JSONObject().apply {
                put("language", currentLang)  // This is the key field Python looks for
            }.toString()

            // Log the exact message being sent
            Log.d(TAG, "Sending JSON message to Python server:")
            Log.d(TAG, "Message: $languageMessage")

            try {
                // Send the JSON message
                manager.sendJsonMessage(languageMessage)
                Log.d(TAG, "✅ Language JSON sent successfully: $languageMessage")

                // FIXED: Also send a direct command that Python will recognize
                val directCommand = "COMMAND:LANGUAGE_CHANGED:$currentLang"
                manager.sendMessage(directCommand)
                Log.d(TAG, "✅ Direct language command sent: $directCommand")

                // Show confirmation to user
                showToast("Language update sent to server: $currentLang")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error sending language update: ${e.message}", e)
                showToast("Error sending language update: ${e.message}")
            }

        } ?: run {
            Log.e(TAG, "❌ WebSocket manager is NULL - cannot send language update")
            showToast("Connection error - language change may not take effect")
        }

        Log.d(TAG, "=== LANGUAGE UPDATE END ===")
    }

    private fun retryLanguageUpdate(language: String) {
        Log.d(TAG, "Retrying language update for: $language")

        webSocketManager?.let { manager ->
            if (manager.isWebSocketConnected()) {
                Log.d(TAG, "Retry successful - WebSocket is now connected")
                sendLanguageUpdate()
            } else {
                Log.w(TAG, "Retry failed - WebSocket still not connected")
                showToast("Connection issue - please try changing language again")
            }
        }
    }

    // Enhanced onLanguageChanged with more logging
    fun onLanguageChanged() {
        val currentLang = getCurrentLanguage()
        Log.d(TAG, "=== LANGUAGE CHANGE EVENT ===")
        Log.d(TAG, "onLanguageChanged() called")
        Log.d(TAG, "Current language from SharedPreferences: $currentLang")
        Log.d(TAG, "WebSocket manager available: ${webSocketManager != null}")

        if (webSocketManager != null) {
            Log.d(TAG, "WebSocket connected: ${webSocketManager!!.isWebSocketConnected()}")
        }

        sendLanguageUpdate()
        showToast(getLocalizedMessage("language_changed"))
        // Sync TTS language
        ttsHandler?.setLanguage(currentLang)

        Log.d(TAG, "=== LANGUAGE CHANGE EVENT END ===")
    }

    // Get current language from SharedPreferences
    private fun getCurrentLanguage(): String {
        val lang = sharedPref.getString("selected_language", "fr") ?: "fr"
        Log.d(TAG, "getCurrentLanguage() returning: $lang")
        return lang
    }

    // FIXED: Helper function to get userId from AuthRepository
    private suspend fun getUserId(): String? {
        return try {
            val userId = authRepository.getUserId().firstOrNull()
            Log.d(TAG, "Retrieved userId from AuthRepository: '$userId'")
            userId
        } catch (e: Exception) {
            Log.e(TAG, "Error getting userId from AuthRepository: ${e.message}", e)
            null
        }
    }

    // Emergency call commands with language support
    private fun getEmergencyCallCommands(): Map<String, String> {
        return when (getCurrentLanguage()) {
            "en" -> mapOf(
                "call assistant" to "CALL_ASSISTANT",
                "emergency" to "CALL_EMERGENCY",
                "police" to "CALL_POLICE",
                "fire department" to "CALL_FIRE",
                "ambulance" to "CALL_AMBULANCE",
                "relative" to "CALL_RELATIVE"
            )
            else -> mapOf( // French (default)
                "faire appel" to "CALL_ASSISTANT",
                "appeler assistance" to "CALL_ASSISTANT",
                "appel urgence" to "CALL_EMERGENCY",
                "urgence" to "CALL_EMERGENCY",
                "police" to "CALL_POLICE",
                "pompiers" to "CALL_FIRE",
                "ambulance" to "CALL_AMBULANCE",
                "proche" to "CALL_RELATIVE",
                "aidant" to "CALL_ASSISTANT"
            )
        }
    }

    // Navigation commands with language support
    private fun getNavigationCommands(): Map<String, String> {
        return when (getCurrentLanguage()) {
            "en" -> mapOf(
                "main menu" to "MainScreen",
                "home" to "MainScreen",
                "profile" to "Profil",
                "device" to "Appareil",
                "settings" to "Parametre",
                "preferences" to "Preferences",
                "information" to "Information",
                "navigation" to "NavigationScreen"
            )
            else -> mapOf( // French (default)
                "menu principal" to "MainScreen",
                "accueil" to "MainScreen",
                "profil" to "Profil",
                "appareil" to "Appareil",
                "parametre" to "Parametre",
                "paramètres" to "Parametre",
                "preferences" to "Preferences",
                "préférences" to "Preferences",
                "information" to "Information",
                "informations" to "Information",
                "navigation" to "NavigationScreen"
            )
        }
    }

    // Device commands with language support
    private fun getDeviceCommands(): Map<String, String> {
        return when (getCurrentLanguage()) {
            "en" -> mapOf(
                "battery" to "CHECK_BATTERY",
                "device status" to "CHECK_DEVICE_STATUS",
                "connection" to "CHECK_CONNECTION",
                "device type" to "CHECK_DEVICE_TYPE",
                "help" to "SHOW_HELP"
            )
            else -> mapOf( // French (default)
                "batterie" to "CHECK_BATTERY",
                "état appareil" to "CHECK_DEVICE_STATUS",
                "connexion" to "CHECK_CONNECTION",
                "type appareil" to "CHECK_DEVICE_TYPE",
                "aide" to "SHOW_HELP",
            )
        }
    }

    // Get localized messages
    private fun getLocalizedMessage(key: String): String {
        return when (getCurrentLanguage()) {
            "en" -> when (key) {
                "calling_assistant" -> "Calling assistant..."
                "emergency_call" -> "Emergency call - calling assistant..."
                "calling_police" -> "Calling police..."
                "calling_fire" -> "Calling fire department..."
                "calling_ambulance" -> "Calling ambulance..."
                "calling_relative" -> "Calling relative..."
                "navigating_to" -> "Navigating to:"
                "navigation_failed" -> "Navigation failed: Unknown screen"
                "no_phone_number" -> "No phone number available for"
                "failed_contacts" -> "Failed to retrieve emergency contacts"
                "network_error" -> "Network error:"
                "language_changed" -> "Language changed to English successfully"
                "switching_to_french" -> "Switching to French..."
                "switching_to_english" -> "Switching to English..."
                "voice_language_changed" -> "Voice recognition language changed"
                "help_intro" -> "Available voice commands:"
                "help_device" -> "Device commands: battery, device status, connection, device type"
                "help_navigation" -> "Navigation: main menu, home, profile, device, settings, preferences"
                "help_emergency" -> "Emergency: call assistant, emergency, police, fire department, ambulance, relative"
                "help_language" -> "Language: switch to french"
                "help_system" -> "System: stop, help"

                else -> key
            }
            else -> when (key) { // French (default)
                "calling_assistant" -> "Appel de l'assistant..."
                "emergency_call" -> "Appel d'urgence - appel de l'aidant..."
                "calling_police" -> "Appel de la police..."
                "calling_fire" -> "Appel des pompiers..."
                "calling_ambulance" -> "Appel de l'ambulance..."
                "calling_relative" -> "Appel du proche..."
                "navigating_to" -> "Navigation vers:"
                "navigation_failed" -> "Échec de navigation: Écran inconnu"
                "no_phone_number" -> "Aucun numéro de téléphone disponible pour"
                "failed_contacts" -> "Échec de récupération des contacts d'urgence"
                "network_error" -> "Erreur réseau:"
                "language_changed" -> "Langue changée vers le français avec succès"
                "switching_to_french" -> "Passage au français..."
                "switching_to_english" -> "Passage à l'anglais..."
                "voice_language_changed" -> "Langue de reconnaissance vocale changée"
                "help_intro" -> "Commandes vocales disponibles:"
                "help_device" -> "Commandes appareil: batterie, état appareil, connexion, type appareil"
                "help_navigation" -> "Navigation: menu principal, accueil, profil, appareil, paramètres, préférences"
                "help_emergency" -> "Urgence: faire appel, appeler assistance, urgence, police, pompiers, ambulance, proche"
                "help_language" -> "Langue: changer en anglais"
                "help_system" -> "Système: arrêter, aide"
                else -> key
            }
        }
    }

    fun processWebSocketMessage(message: String) {
        Log.d(TAG, "Processing WebSocket message: '$message'")

        // UPDATED: Handle voice-initiated language changes from Python server
        if (message.contains("VOICE_LANGUAGE_CHANGED") || message.contains("voice_command")) {
            Log.d(TAG, "✅ Voice command language change detected: $message")

            try {
                // Handle JSON format from voice commands
                val jsonObj = JSONObject(message)
                if (jsonObj.has("language") && jsonObj.has("source")) {
                    val serverLang = jsonObj.getString("language")
                    val source = jsonObj.getString("source")

                    if (source == "voice_command") {
                        Log.d(TAG, "Voice command triggered language change to: $serverLang")
                        handleVoiceLanguageChange(serverLang)
                        return
                    }
                }
            } catch (e: JSONException) {
                // Handle command format
                if (message.startsWith("COMMAND:VOICE_LANGUAGE_CHANGED:")) {
                    val newLang = message.substringAfter("COMMAND:VOICE_LANGUAGE_CHANGED:")
                    Log.d(TAG, "Voice command format language change to: $newLang")
                    handleVoiceLanguageChange(newLang)
                    return
                }
            }
        }

        // Handle server language change acknowledgments (UI changes)
        if (message.contains("language_changed") || message.contains("LANGUAGE_CHANGED")) {
            Log.d(TAG, "✅ Server acknowledged language change: $message")
            try {
                val jsonObj = JSONObject(message)
                if (jsonObj.has("language") && jsonObj.has("source")) {
                    val serverLang = jsonObj.getString("language")
                    val source = jsonObj.getString("source")

                    if (source == "ui") {
                        Log.d(TAG, "UI-initiated language change confirmed: $serverLang")
                        showToast("Server language updated to: $serverLang")
                    }
                } else if (jsonObj.has("language")) {
                    val serverLang = jsonObj.getString("language")
                    Log.d(TAG, "Server confirmed language: $serverLang")
                    showToast("Server language updated to: $serverLang")
                }
            } catch (e: JSONException) {
                Log.d(TAG, "Non-JSON language confirmation: $message")
            }
            return
        }

        // Handle direct command format from Python server
        if (message.startsWith("COMMAND:")) {
            val commandFull = message.substringAfter("COMMAND:")

            if (commandFull.startsWith("NAVIGATE_TO:")) {
                val screenName = commandFull.substringAfter("NAVIGATE_TO:")
                handleNavigation(screenName)
                return
            }

            // Handle language change commands from server (UI initiated)
            if (commandFull.startsWith("LANGUAGE_CHANGED:")) {
                val newLang = commandFull.substringAfter("LANGUAGE_CHANGED:")
                Log.d(TAG, "Server confirmed UI language change to: $newLang")
                showToast("Voice recognition language changed to: $newLang")
                return
            }

            // ✅ Handle device commands from server
            when (commandFull) {
                "CHECK_BATTERY", "CHECK_DEVICE_STATUS", "CHECK_CONNECTION", "CHECK_DEVICE_TYPE" -> {
                    handleDeviceCommand(commandFull)
                    return
                }

            }

            // Handle emergency commands
            handleEmergencyCommand(commandFull)
            return
        }

        // Try JSON parsing for Vosk recognition results
        try {
            val jsonObj = JSONObject(message)
            if (jsonObj.has("text")) {
                val recognizedText = jsonObj.getString("text").lowercase().trim()
                processRecognizedText(recognizedText)
                return
            }
        } catch (e: JSONException) {
            // Not JSON, continue with string processing
        }

        // Handle plain text messages
        val lowerMessage = message.lowercase()

        // Extract text from recognition patterns
        val recognizedText = when {
            message.contains("Recognized:") -> {
                val start = message.indexOf("'")
                val end = message.lastIndexOf("'")
                if (start >= 0 && end > start) {
                    message.substring(start + 1, end).lowercase().trim()
                } else lowerMessage
            }
            else -> lowerMessage
        }

        processRecognizedText(recognizedText)
    }

    // NEW: Handle voice-initiated language changes
    private fun handleVoiceLanguageChange(targetLanguage: String) {
        Log.d(TAG, "=== VOICE LANGUAGE CHANGE ===")
        Log.d(TAG, "Voice command triggered language change to: $targetLanguage")

        val currentLanguage = getCurrentLanguage()
        if (currentLanguage == targetLanguage) {
            Log.d(TAG, "Language already set to $targetLanguage, no change needed")
            showToast(getLocalizedMessage("voice_language_changed"))
            return
        }

        // Update SharedPreferences to match voice command
        val editor = sharedPref.edit()
        editor.putString("selected_language", targetLanguage)
        val saved = editor.commit() // Use commit for immediate save

        Log.d(TAG, "SharedPreferences updated: $saved (from $currentLanguage to $targetLanguage)")

        ttsHandler?.setLanguage(targetLanguage)
        Log.d(TAG, "TTS language updated to: $targetLanguage")

        // Show appropriate message based on target language
        val message = when (targetLanguage) {
            "fr" -> "Changement vers le français par commande vocale"
            "en" -> "Voice command changed language to English"
            else -> getLocalizedMessage("voice_language_changed")
        }
        showToast(message)

        // Send confirmation back to server
        webSocketManager?.let { manager ->
            try {
                val confirmMessage = JSONObject().apply {
                    put("type", "language_change_confirmed")
                    put("language", targetLanguage)
                    put("previous", currentLanguage)
                    put("source", "voice_command_processed")
                }.toString()

                manager.sendJsonMessage(confirmMessage)
                Log.d(TAG, "Sent voice language change confirmation: $confirmMessage")
            } catch (e: Exception) {
                Log.e(TAG, "Error sending voice language confirmation: ${e.message}", e)
            }
        }

        Log.d(TAG, "=== VOICE LANGUAGE CHANGE COMPLETE ===")
    }

    private fun processRecognizedText(text: String) {
        Log.d(TAG, "Processing recognized text: '$text' in language: ${getCurrentLanguage()}")

        val emergencyCommands = getEmergencyCallCommands()
        val navigationCommands = getNavigationCommands()

        // NOTE: Language change commands are now
        // by the Python server
        // The server will process voice commands and send us the language change notification

        // Priority 1: Check emergency/call commands first with exact phrase matching
        val exactEmergencyMatch = emergencyCommands.keys.find { trigger ->
            text == trigger || text.startsWith("$trigger ") || text.endsWith(" $trigger") ||
                    text.contains(" $trigger ")
        }

        if (exactEmergencyMatch != null) {
            val action = emergencyCommands[exactEmergencyMatch]!!
            Log.d(TAG, "Emergency command matched: $exactEmergencyMatch -> $action")
            handleEmergencyCommand(action)
            return
        }

        // Special case: handle compound emergency phrases more strictly based on language
        when (getCurrentLanguage()) {
            "en" -> {
                when {
                    text.matches(Regex(".*\\bcall assistant\\b.*")) -> {
                        Log.d(TAG, "Matched 'call assistant' phrase")
                        handleEmergencyCommand("CALL_ASSISTANT")
                        return
                    }
                    text.matches(Regex(".*\\bemergency\\b.*")) && !text.contains("device") -> {
                        Log.d(TAG, "Matched 'emergency' phrase")
                        handleEmergencyCommand("CALL_EMERGENCY")
                        return
                    }
                }
            }
            else -> { // French
                when {
                    text.matches(Regex(".*\\bfaire appel\\b.*")) -> {
                        Log.d(TAG, "Matched 'faire appel' phrase")
                        handleEmergencyCommand("CALL_ASSISTANT")
                        return
                    }
                    text.matches(Regex(".*\\bappel urgence\\b.*")) -> {
                        Log.d(TAG, "Matched 'appel urgence' phrase")
                        handleEmergencyCommand("CALL_EMERGENCY")
                        return
                    }
                    text.matches(Regex(".*\\burgence\\b.*")) && !text.contains("appareil") -> {
                        Log.d(TAG, "Matched 'urgence' phrase")
                        handleEmergencyCommand("CALL_EMERGENCY")
                        return
                    }
                }
            }
        }

        // Priority 2: Check navigation commands with word boundary matching
        for ((trigger, screen) in navigationCommands) {
            if (text.matches(Regex(".*\\b$trigger\\b.*"))) {
                Log.d(TAG, "Navigation command matched: $trigger -> $screen")
                handleNavigation(screen)
                return
            }
        }

        // Priority 3: Check device commands
        val deviceCommands = getDeviceCommands()
        for ((trigger, command) in deviceCommands) {
            if (text.matches(Regex(".*\\b$trigger\\b.*"))) {
                Log.d(TAG, "Device command matched: $trigger -> $command")
                handleDeviceCommand(command)
                return
            }
        }

        Log.d(TAG, "No command matched for: '$text'")
    }

    private fun handleEmergencyCommand(command: String) {
        Log.d(TAG, "Handling emergency command: $command")

        when (command) {
            "CALL_ASSISTANT" -> {
                showToast(getLocalizedMessage("calling_assistant"))
                callEmergencyContact("Assistant")
            }
            "CALL_EMERGENCY" -> {
                showToast(getLocalizedMessage("emergency_call"))
                callEmergencyContact("Aidant")
            }
            "CALL_POLICE" -> {
                showToast(getLocalizedMessage("calling_police"))
                callEmergencyContact("Police")
            }
            "CALL_FIRE" -> {
                showToast(getLocalizedMessage("calling_fire"))
                callEmergencyContact("Pompiers")
            }
            "CALL_AMBULANCE" -> {
                showToast(getLocalizedMessage("calling_ambulance"))
                callEmergencyContact("Ambulance")
            }
            "CALL_RELATIVE" -> {
                showToast(getLocalizedMessage("calling_relative"))
                callEmergencyContact("Proche")
            }
        }

        // Navigate to call screen for visual feedback
        navigateToScreen(Screens.Appel.route)
    }

    private fun callEmergencyContact(contactType: String) {
        coroutineScope.launch {
            val call = RetrofitClient.instance.getEmergencyList()

            call.enqueue(object : Callback<List<ListItems>> {
                override fun onResponse(call: Call<List<ListItems>>, response: Response<List<ListItems>>) {
                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        val contacts = response.body()!!

                        // Find contact by type or use first available
                        val contact = contacts.find {
                            it.label.contains(contactType, ignoreCase = true)
                        } ?: contacts.first()

                        if (contact.number.isNotEmpty()) {
                            Log.d(TAG, "Calling ${contact.label}: ${contact.number}")
                            makePhoneCall(context, contact.number)
                        } else {
                            showToast("${getLocalizedMessage("no_phone_number")} ${contact.label}")
                        }
                    } else {
                        Log.e(TAG, "Failed to get emergency contacts: ${response.code()}")
                        showToast(getLocalizedMessage("failed_contacts"))
                    }
                }

                override fun onFailure(call: Call<List<ListItems>>, t: Throwable) {
                    Log.e(TAG, "Network error when fetching emergency contacts", t)
                    showToast("${getLocalizedMessage("network_error")} ${t.message}")
                }
            })
        }
    }

    private fun handleNavigation(screenName: String) {
        val route = when (screenName.trim()) {
            "MainScreen" -> Screens.MainScreen.route
            "Profil" -> Screens.Profil.route
            "Appareil" -> Screens.Appareil.route
            "Parametre" -> Screens.Parametre.route
            "Preferences" -> Screens.Preferences.route
            "Information" -> Screens.Information.route
            "NavigationScreen" -> Screens.NavigationScreen.route
            "Appel" -> Screens.Appel.route
            else -> {
                Log.e(TAG, "Unknown screen: $screenName")
                showToast("${getLocalizedMessage("navigation_failed")} '$screenName'")
                return
            }
        }

        showToast("${getLocalizedMessage("navigating_to")} $screenName")
        navigateToScreen(route)
    }

    // FIXED: Updated device command handler to use AuthRepository for userId
    private fun handleDeviceCommand(command: String) {
        Log.d(TAG, "Handling device command: $command")

        coroutineScope.launch {
            val userId = getUserId()

            if (userId.isNullOrEmpty()) {
                Log.w(TAG, "User ID not found in AuthRepository")
                val message = when (getCurrentLanguage()) {
                    "en" -> "Unable to access device information - user not authenticated"
                    else -> "Impossible d'accéder aux informations - utilisateur non authentifié"
                }
                ttsHandler?.speak(message)
                return@launch
            }

            Log.d(TAG, "Using userId from AuthRepository: '$userId'")

            when (command) {
                "CHECK_BATTERY" -> {
                    fetchDeviceInfo(userId) { deviceData ->
                        Log.d(TAG, "Battery check - Device data received: $deviceData")
                        deviceData?.let { data ->
                            Log.d(TAG, "Database battery level: ${data.battery_capacity}%")
                            ttsHandler?.announceBattery(data.battery_capacity, getCurrentLanguage())
                        } ?: run {
                            Log.w(TAG, "Device data is null - cannot get battery from database")
                            val message = when (getCurrentLanguage()) {
                                "en" -> "Battery information not available from device database"
                                else -> "Information de batterie non disponible dans la base de données"
                            }
                            ttsHandler?.speak(message)
                        }
                    }
                }
                "SHOW_HELP" -> {
                    showHelpCommands()
                }

                "CHECK_DEVICE_STATUS" -> {
                    fetchDeviceInfo(userId) { deviceData ->
                        Log.d(TAG, "Device status check - Data received: $deviceData")
                        deviceData?.let { data ->
                            ttsHandler?.announceDeviceState(data.state, getCurrentLanguage())
                        } ?: run {
                            Log.w(TAG, "Device status data is null")
                            val message = when (getCurrentLanguage()) {
                                "en" -> "Device status not available from database"
                                else -> "État de l'appareil non disponible dans la base de données"
                            }
                            ttsHandler?.speak(message)
                        }
                    }
                }

                "CHECK_CONNECTION" -> {
                    // FIXED: Check device connection status from database, not WebSocket
                    fetchDeviceInfo(userId) { deviceData ->
                        Log.d(TAG, "Connection check - Data received: $deviceData")
                        deviceData?.let { data ->
                            // Check if device state indicates connection
                            val isConnected = data.state.equals("Connected", ignoreCase = true) ||
                                    data.state.equals("Connecté", ignoreCase = true) ||
                                    !data.state.equals("Deconnected", ignoreCase = true)
                            Log.d(TAG, "Device connection status from DB: $isConnected (state: ${data.state})")
                            ttsHandler?.announceConnectionStatus(isConnected, getCurrentLanguage())
                        } ?: run {
                            Log.w(TAG, "Device connection data is null")
                            val message = when (getCurrentLanguage()) {
                                "en" -> "Connection status not available from database"
                                else -> "État de connexion non disponible dans la base de données"
                            }
                            ttsHandler?.speak(message)
                        }
                    }
                }

                "CHECK_DEVICE_TYPE" -> {
                    fetchDeviceInfo(userId) { deviceData ->
                        Log.d(TAG, "Device type check - Data received: $deviceData")
                        deviceData?.let { data ->
                            ttsHandler?.announceDeviceType(data.type, getCurrentLanguage())
                        } ?: run {
                            Log.w(TAG, "Device type data is null")
                            val message = when (getCurrentLanguage()) {
                                "en" -> "Device type not available from database"
                                else -> "Type d'appareil non disponible dans la base de données"
                            }
                            ttsHandler?.speak(message)
                        }
                    }
                }
            }
        }
    }
    private fun showHelpCommands() {
        Log.d(TAG, "Showing help commands in language: ${getCurrentLanguage()}")

        val helpMessages = listOf(
            getLocalizedMessage("help_intro"),
            getLocalizedMessage("help_device"),
            getLocalizedMessage("help_navigation"),
            getLocalizedMessage("help_emergency"),
            getLocalizedMessage("help_language"),
            getLocalizedMessage("help_system")
        )

        // Combine all help messages with pauses
        val fullHelpText = helpMessages.joinToString(". ")

        ttsHandler?.speak(fullHelpText, getCurrentLanguage())

        // Also show toast for visual feedback
        showToast(getLocalizedMessage("help_intro"))
    }
    private fun navigateToScreen(route: String) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                navController?.navigate(route)
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error: ${e.message}", e)
                showToast("${getLocalizedMessage("navigation_failed")}: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // FIXED: Check device connection status from database instead of WebSocket
    fun checkConnectionStatus(): Boolean {
        // This method should be updated to use the database connection status
        // For now, returning WebSocket status as fallback, but ideally should fetch from DB
        val isConnected = webSocketManager?.isWebSocketConnected() ?: false
        Log.d(TAG, "WebSocket connection status check: $isConnected")
        return isConnected
    }

    // Add method to force reconnection if needed
    fun ensureConnection() {
        Log.d(TAG, "Ensuring WebSocket connection...")
        webSocketManager?.let { manager ->
            if (!manager.isWebSocketConnected()) {
                Log.d(TAG, "Connection not available, attempting reconnect")
                manager.reconnect()
            } else {
                Log.d(TAG, "Connection already established")
            }
        }
    }

    fun cleanup() {
        ttsHandler?.shutdown()
        ttsHandler = null
    }
}