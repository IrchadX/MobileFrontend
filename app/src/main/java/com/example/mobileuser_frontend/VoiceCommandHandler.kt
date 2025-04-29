package com.example.mobileuser_frontend

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.mobileuser_frontend.functions.fetchPhoneNumber
import com.example.mobileuser_frontend.functions.makePhoneCall
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles voice command processing and executes appropriate actions
 */
class VoiceCommandHandler(private val context: Context) {
    private val TAG = "VoiceCommandHandler"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Process incoming messages from the WebSocket server
     */
    fun processWebSocketMessage(message: String) {
        Log.d(TAG, "Processing message: $message")

        // Check for special trigger keyword in recognized messages
        if (message.contains("TRIGGER_CALL")) {
            Log.d(TAG, "Call trigger detected in message, initiating call")
            handleCallCommand()
            return
        }

        // First check for direct command messages
        if (message.startsWith("COMMAND:")) {
            val command = message.substringAfter("COMMAND:")
            Log.d(TAG, "Direct command received: $command")

            when (command) {
                "CALL_ASSISTANT" -> {
                    Log.d(TAG, "Call assistant command received, initiating call")
                    handleCallCommand()
                }
                // Add other direct commands as needed
            }
            return
        }

        // Then check for recognition messages
        if (message.startsWith("📢 Recognized:")) {
            val recognizedText = message.substringAfter("'").substringBefore("'").trim().lowercase()
            Log.d(TAG, "Extracted recognized text: $recognizedText")

            when {
                // Command patterns for making a call - expand as needed
                "faire appel" in recognizedText ||
                        "appeler assistance" in recognizedText ||
                        "demande aide" in recognizedText ||
                        "demande d'aide" in recognizedText -> {
                    Log.d(TAG, "Call command detected in recognition, initiating call")
                    handleCallCommand()
                }
                // Add more command patterns here as needed
            }
        }
    }

    /**
     * Handle the call command by fetching phone number and making a call
     * This uses the same code path as the UI button click
     */
    private fun handleCallCommand() {
        coroutineScope.launch(Dispatchers.Main) {
            // Use the same fixed ID that's used in the Appel composable
            // This ensures we're using exactly the same code path
            fetchPhoneNumber("66") { phone ->
                if (phone != null) {
                    Log.d(TAG, "Making call to: $phone")
                    makePhoneCall(context, phone)
                } else {
                    Log.e(TAG, "Failed to fetch phone number")
                    Toast.makeText(context, "Error: Try again Please", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}