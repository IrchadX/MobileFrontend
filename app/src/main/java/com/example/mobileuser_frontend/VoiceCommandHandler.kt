package com.example.mobileuser_frontend

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.mobileuser_frontend.functions.makePhoneCall
import com.example.mobileuser_frontend.module.RetrofitClient
import com.example.mobileuser_frontend.module.ListItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Handles voice command processing and executes appropriate actions
 */
class VoiceCommandHandler(private val context: Context) {
    private val TAG = "VoiceCommandHandler"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Navigation controller reference - will be set from MainActivity
    private var navController: NavController? = null

    // Set the navigation controller
    fun setNavController(controller: NavController) {
        this.navController = controller
        Log.d(TAG, "NavController set in VoiceCommandHandler")
    }

    // Keywords that should trigger a call
    private val callTriggerKeywords = listOf(
        "faire appel",
        "call assistant",
        "appeler assistance",
        "demande aide",
        "demande d'aide",
        "trigger_call",
        "call_assistant"
    )

    /**
     * Process incoming messages from the WebSocket server
     */
    fun processWebSocketMessage(message: String) {
        Log.d(TAG, "Processing message: '$message'")

        // Show all incoming messages as toast for debugging
        showDebugToast("Voice Server: $message")

        // Handle direct command messages from server
        if (message.startsWith("COMMAND:")) {
            val commandFull = message.substringAfter("COMMAND:")
            Log.d(TAG, "Direct command received: $commandFull")

            // Check for NAVIGATE_TO command pattern
            if (commandFull.startsWith("NAVIGATE_TO:")) {
                val screenName = commandFull.substringAfter("NAVIGATE_TO:")
                Log.d(TAG, "Navigation command received for screen: $screenName")
                handleDirectNavigation(screenName)
                return
            }

            // Other commands
            when (commandFull.uppercase()) {
                "CALL_ASSISTANT" -> {
                    Log.d(TAG, "Call assistant command received, initiating api call")
                    handleCallCommand()
                    return
                }
                // Add other direct commands as needed
            }
            return
        }

        // Try to parse as JSON (the Python server might send JSON)
        try {
            val jsonObj = JSONObject(message)

            // Handle text recognition results from Vosk
            if (jsonObj.has("text")) {
                val recognizedText = jsonObj.getString("text").lowercase()
                Log.d(TAG, "Parsed JSON recognition: '$recognizedText'")

                // Check if any trigger keywords are in the recognized text
                if (callTriggerKeywords.any { it in recognizedText }) {
                    Log.d(TAG, "Call trigger found in JSON recognition")
                    handleCallCommand()
                    return
                }

                // Check for navigation commands
                handleNavigationCommand(recognizedText)
                return
            }

            // Handle command object format
            if (jsonObj.has("command")) {
                val command = jsonObj.getString("command").uppercase()
                Log.d(TAG, "JSON command received: $command")

                if (command == "CALL_ASSISTANT") {
                    handleCallCommand()
                    return
                }
            }

            return
        } catch (e: JSONException) {
            // Not JSON, continue with string processing
            Log.d(TAG, "Not a JSON message, continuing with string processing")
        }

        // Handle plain text messages with recognition results
        val lowerMessage = message.lowercase()

        // Check if any of our trigger keywords are in the message
        if (callTriggerKeywords.any { it in lowerMessage }) {
            Log.d(TAG, "Call trigger found in plain text message")
            handleCallCommand()
            return
        }

        // Look for recognition patterns from Python script
        if (message.contains("Recognized:") || message.contains("Command:")) {
            // Extract the recognized text part
            val recognizedText = when {
                message.contains("Recognized:") -> {
                    val start = message.indexOf("'")
                    val end = message.lastIndexOf("'")
                    if (start >= 0 && end > start) {
                        message.substring(start + 1, end).lowercase()
                    } else {
                        message.lowercase()
                    }
                }
                else -> message.lowercase()
            }

            Log.d(TAG, "Extracted recognition text: '$recognizedText'")

            // Check for call triggers
            if (callTriggerKeywords.any { it in recognizedText }) {
                Log.d(TAG, "Call trigger found in extracted recognition")
                handleCallCommand()
                return
            }

            // Check for navigation or other commands
            if (handleNavigationCommand(recognizedText)) {
                return
            }
        }

        // For commands that come from the Python script with the "Command:" prefix
        if (message.contains("Command:")) {
            val commandText = message.substringAfter("Command:").trim().lowercase()
            Log.d(TAG, "Command text extracted: '$commandText'")

            // Process navigation commands
            handlePythonCommandMessage(commandText)
        }
    }

    /**
     * Handle direct navigation commands from the server
     * This is the new method that handles the direct NAVIGATE_TO commands
     */
    private fun handleDirectNavigation(screenName: String) {
        Log.d(TAG, "Handling direct navigation to screen: $screenName")

        val route = when (screenName.trim()) {
            "MainScreen" -> Screens.MainScreen.route
            "Profil" -> Screens.Profil.route
            "Appareil" -> Screens.Appareil.route
            "Parametre" -> Screens.Parametre.route
            "Preferences" -> Screens.Preferences.route
            "Information" -> Screens.Information.route
            "NavigationScreen" -> Screens.NavigationScreen.route
            "Appel" -> Screens.Appel.route
            else -> null
        }

        if (route != null) {
            navigateTo(route, screenName)
        } else {
            Log.e(TAG, "Unknown screen name: $screenName")
            showErrorToast("Navigation failed: Unknown screen '$screenName'")
        }
    }

    /**
     * Handle commands coming directly from the Python server
     */
    private fun handlePythonCommandMessage(commandText: String): Boolean {
        Log.d(TAG, "Processing Python command: $commandText")

        return when {
            commandText.contains("accessing main menu") -> {
                navigateTo(Screens.MainScreen.route, "main menu")
                true
            }
            commandText.contains("accès au menu principal") -> {
                navigateTo(Screens.MainScreen.route, "menu principal")
                true
            }
            commandText.contains("accessing profile") -> {
                navigateTo(Screens.Profil.route, "profile")
                true
            }
            commandText.contains("accès au profile") -> {
                navigateTo(Screens.Profil.route, "profile")
                true
            }
            commandText.contains("accessing device state") || commandText.contains("device state") -> {
                navigateTo(Screens.Appareil.route, "device")
                true
            }
            commandText.contains("accès a appareil") || commandText.contains("accès à appareil") -> {
                navigateTo(Screens.Appareil.route, "appareil")
                true
            }
            // Brute force - check for any fragment of text that might indicate the screen
            commandText.contains("profil") -> {
                navigateTo(Screens.Profil.route, "profil")
                true
            }
            commandText.contains("appareil") -> {
                navigateTo(Screens.Appareil.route, "appareil")
                true
            }
            commandText.contains("menu") || commandText.contains("principale") -> {
                navigateTo(Screens.MainScreen.route, "menu principal")
                true
            }
            commandText.contains("parametre") || commandText.contains("paramètres") -> {
                navigateTo(Screens.Parametre.route, "paramètres")
                true
            }
            else -> false
        }
    }

    /**
     * Handle navigation commands from recognized speech
     */
    private fun handleNavigationCommand(text: String): Boolean {
        val lowerText = text.lowercase()
        Log.d(TAG, "Checking navigation command: '$lowerText'")

        return when {
            // English commands
            //cbn
            "main menu" in lowerText || "home" in lowerText -> {
                navigateTo(Screens.MainScreen.route, "main menu")
                true
            }
            //cbn
            "profile" in lowerText -> {
                navigateTo(Screens.Profil.route, "profile")
                true
            }
            //cbn
            "device" in lowerText || "state" in lowerText -> {
                navigateTo(Screens.Appareil.route, "device")
                true
            }
            "settings" in lowerText || "parameter" in lowerText -> {
                navigateTo(Screens.Parametre.route, "settings")
                true
            }
            "preferences" in lowerText -> {
                navigateTo(Screens.Preferences.route, "preferences")
                true
            }
            "information" in lowerText || "info" in lowerText -> {
                navigateTo(Screens.Information.route, "information")
                true
            }
            "navigation" in lowerText -> {
                navigateTo(Screens.NavigationScreen.route, "navigation")
                true
            }

            // French commands
            "menu principal" in lowerText || "accueil" in lowerText || "principale" in lowerText -> {
                navigateTo(Screens.MainScreen.route, "menu principal")
                true
            }
            "profil" in lowerText -> {
                navigateTo(Screens.Profil.route, "profil")
                true
            }
            "appareil" in lowerText -> {
                navigateTo(Screens.Appareil.route, "appareil")
                true
            }
            "parametre" in lowerText || "paramètres" in lowerText -> {
                navigateTo(Screens.Parametre.route, "paramètres")
                true
            }
            "preferences" in lowerText || "préférences" in lowerText -> {
                navigateTo(Screens.Preferences.route, "préférences")
                true
            }
            "information" in lowerText || "informations" in lowerText -> {
                navigateTo(Screens.Information.route, "informations")
                true
            }
            "navigation" in lowerText -> {
                navigateTo(Screens.NavigationScreen.route, "navigation")
                true
            }
            "appel" in lowerText -> {
                navigateTo(Screens.Appel.route, "appel")
                true
            }
            else -> false
        }
    }

    /**
     * Navigate to the specified screen
     */
    private fun navigateTo(route: String, commandName: String) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                if (navController != null) {
                    Log.d(TAG, "Navigating to $route from voice command: $commandName")
                    showSuccessToast("Navigating to: $commandName")

                    // Direct navigation without complex options to avoid issues
                    navController?.navigate(route)
                } else {
                    Log.e(TAG, "Cannot navigate: NavController is null")
                    showErrorToast("Navigation failed: NavController is null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error: ${e.message}", e)
                showErrorToast("Navigation failed: ${e.message}")
            }
        }
    }

    /**
     * Handle the call command by fetching emergency contacts and making a call
     */
    private fun handleCallCommand() {
        Log.d(TAG, "Handling call command")
        showDebugToast("Voice command detected: Initiating emergency call")

        // First try to navigate to the call screen
        coroutineScope.launch(Dispatchers.Main) {
            if (navController != null) {
                navController?.navigate(Screens.Appel.route)
            }
        }

        // Then follow the same pattern as in the Appel composable
        coroutineScope.launch {
            // Get the emergency list just like in the EmergencyDropdown composable
            val call = RetrofitClient.instance.getEmergencyList()

            call.enqueue(object : Callback<List<ListItems>> {
                override fun onResponse(call: Call<List<ListItems>>, response: Response<List<ListItems>>) {
                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        // Get the first emergency contact from the list, just like when selecting from dropdown
                        val emergencyContact = response.body()!!.first()
                        val phoneNumber = emergencyContact.number // Using number field as in ListItems

                        if (phoneNumber.isNotEmpty()) {
                            Log.d(TAG, "Emergency contact found: ${emergencyContact.label}, number: $phoneNumber")
                            showSuccessToast("Calling emergency: ${emergencyContact.label}")

                            // Make the call directly using makePhoneCall function, just like in the dropdown
                            makePhoneCall(context, phoneNumber)
                        } else {
                            Log.e(TAG, "Empty phone number received")
                            showErrorToast("Failed to initiate call: No phone number available")
                        }
                    } else {
                        Log.e(TAG, "Failed to get emergency contacts: ${response.code()}")
                        showErrorToast("Failed to retrieve emergency contacts")
                    }
                }

                override fun onFailure(call: Call<List<ListItems>>, t: Throwable) {
                    Log.e(TAG, "Network error when fetching emergency contacts", t)
                    showErrorToast("Network error: ${t.message}")
                }
            })
        }
    }

    /**
     * Show debug toast on the main thread
     */
    private fun showDebugToast(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Show success toast on the main thread
     */
    private fun showSuccessToast(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Show error toast on the main thread
     */
    private fun showErrorToast(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
        }
    }
}