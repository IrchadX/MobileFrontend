package com.example.mobileuser_frontend

import android.content.Context
import android.util.Log
import android.widget.Toast
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
            val command = message.substringAfter("COMMAND:")
            Log.d(TAG, "Direct command received: $command")

            when (command.uppercase()) {
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
        }
    }

    /**
     * Handle the call command by fetching emergency contacts and making a call
     */
    private fun handleCallCommand() {
        Log.d(TAG, "Handling call command")
        showDebugToast("Voice command detected: Initiating emergency call")

        // Follow the same pattern as in the Appel composable
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