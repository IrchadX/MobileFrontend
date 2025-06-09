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

class VoiceCommandHandler(private val context: Context) {
    private val TAG = "VoiceCommandHandler"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        this.navController = controller
    }

    // Emergency call commands
    private val emergencyCallCommands = mapOf(
        // French
        "faire appel" to "CALL_ASSISTANT",
        "appeler assistance" to "CALL_ASSISTANT",
        "appel urgence" to "CALL_EMERGENCY",
        "urgence" to "CALL_EMERGENCY",
        "police" to "CALL_POLICE",
        "pompiers" to "CALL_FIRE",
        "ambulance" to "CALL_AMBULANCE",
        "proche" to "CALL_RELATIVE",
        "aidant" to "CALL_ASSISTANT",

        // English
        "call assistant" to "CALL_ASSISTANT",
        "emergency" to "CALL_EMERGENCY",
        "fire department" to "CALL_FIRE",
        "relative" to "CALL_RELATIVE"
    )

    // Navigation commands
    private val navigationCommands = mapOf(
        // French
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
        "navigation" to "NavigationScreen",

        // English
        "main menu" to "MainScreen",
        "home" to "MainScreen",
        "profile" to "Profil",
        "device" to "Appareil",
        "settings" to "Parametre",
        "preferences" to "Preferences",
        "information" to "Information"
    )

    fun processWebSocketMessage(message: String) {
        Log.d(TAG, "Processing message: '$message'")

        // Handle direct command format from Python server
        if (message.startsWith("COMMAND:")) {
            val commandFull = message.substringAfter("COMMAND:")

            if (commandFull.startsWith("NAVIGATE_TO:")) {
                val screenName = commandFull.substringAfter("NAVIGATE_TO:")
                handleNavigation(screenName)
                return
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

    private fun processRecognizedText(text: String) {
        Log.d(TAG, "Processing recognized text: '$text'")

        // Priority 1: Check emergency/call commands first with exact phrase matching
        val exactEmergencyMatch = emergencyCallCommands.keys.find { trigger ->
            text == trigger || text.startsWith("$trigger ") || text.endsWith(" $trigger") ||
                    text.contains(" $trigger ")
        }

        if (exactEmergencyMatch != null) {
            val action = emergencyCallCommands[exactEmergencyMatch]!!
            Log.d(TAG, "Emergency command matched: $exactEmergencyMatch -> $action")
            handleEmergencyCommand(action)
            return
        }

        // Special case: handle compound emergency phrases more strictly
        when {
            text.matches(Regex(".*\\bfaire appel\\b.*")) -> {
                Log.d(TAG, "Matched 'faire appel' phrase")
                handleEmergencyCommand("CALL_ASSISTANT")
                return
            }
            text.matches(Regex(".*\\bcall assistant\\b.*")) -> {
                Log.d(TAG, "Matched 'call assistant' phrase")
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

        // Priority 2: Check navigation commands with word boundary matching
        for ((trigger, screen) in navigationCommands) {
            if (text.matches(Regex(".*\\b$trigger\\b.*"))) {
                Log.d(TAG, "Navigation command matched: $trigger -> $screen")
                handleNavigation(screen)
                return
            }
        }

        Log.d(TAG, "No command matched for: '$text'")
    }

    private fun handleEmergencyCommand(command: String) {
        Log.d(TAG, "Handling emergency command: $command")

        when (command) {
            "CALL_ASSISTANT" -> {
                showToast("Calling assistant...")
                callEmergencyContact("Assistant")
            }
            "CALL_EMERGENCY" -> {
                showToast("Emergency call - calling aidant...")
                callEmergencyContact("Aidant")
            }
            "CALL_POLICE" -> {
                showToast("Calling police...")
                callEmergencyContact("Police")
            }
            "CALL_FIRE" -> {
                showToast("Calling fire department...")
                callEmergencyContact("Pompiers")
            }
            "CALL_AMBULANCE" -> {
                showToast("Calling ambulance...")
                callEmergencyContact("Ambulance")
            }
            "CALL_RELATIVE" -> {
                showToast("Calling relative...")
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
                            showToast("No phone number available for ${contact.label}")
                        }
                    } else {
                        Log.e(TAG, "Failed to get emergency contacts: ${response.code()}")
                        showToast("Failed to retrieve emergency contacts")
                    }
                }

                override fun onFailure(call: Call<List<ListItems>>, t: Throwable) {
                    Log.e(TAG, "Network error when fetching emergency contacts", t)
                    showToast("Network error: ${t.message}")
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
                showToast("Navigation failed: Unknown screen '$screenName'")
                return
            }
        }

        showToast("Navigating to: $screenName")
        navigateToScreen(route)
    }

    private fun navigateToScreen(route: String) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                navController?.navigate(route)
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error: ${e.message}", e)
                showToast("Navigation failed: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        coroutineScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}