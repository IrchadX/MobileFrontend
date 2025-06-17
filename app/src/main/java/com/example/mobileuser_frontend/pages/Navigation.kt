package com.example.mobileuser_frontend.pages

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.module.CustomDropdown
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.mqtt.MqttNavigationManager
import com.example.mobileuser_frontend.mqtt.POI
import java.util.*

// ViewModel for managing MQTT navigation state
class NavigationViewModel : androidx.lifecycle.ViewModel() {
    private lateinit var mqttManager: MqttNavigationManager

    val navigationInstructions get() = mqttManager.navigationInstructions
    val obstacleAlert get() = mqttManager.obstacleAlert
    val connectionStatus get() = mqttManager.connectionStatus
    val availablePOIs get() = mqttManager.availablePOIs

    fun initialize(context: android.content.Context, geoJsonString: String) {
        mqttManager = MqttNavigationManager(context)
        mqttManager.connect()
        mqttManager.extractPOIsFromGeoJSON(geoJsonString)
    }

    fun sendNavigationRequest(destination: String, coordinates: List<Double>) {
        mqttManager.sendNavigationRequest(destination, coordinates)
    }

    fun clearObstacleAlert() {
        mqttManager.clearObstacleAlert()
    }

    override fun onCleared() {
        super.onCleared()
        if (::mqttManager.isInitialized) {
            mqttManager.disconnect()
        }
    }
}

@Composable
fun Navigation(
    navController: NavController,
    geoJsonString: String,
    viewModel: NavigationViewModel = viewModel()
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Voice recognition states
    var isListening by remember { mutableStateOf(false) }
    var voiceInput by remember { mutableStateOf("") }
    var showVoiceResult by remember { mutableStateOf(false) }

    // Initialize MQTT manager
    LaunchedEffect(Unit) {
        viewModel.initialize(context, geoJsonString)
    }

    // Collect states
    val availablePOIs by viewModel.availablePOIs.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val navigationInstructions by viewModel.navigationInstructions.collectAsState()
    val obstacleAlert by viewModel.obstacleAlert.collectAsState()

    // Convert POIs to dropdown items
    val poiOptions = remember(availablePOIs) {
        availablePOIs.map { poi ->
            ListItems(
                label = poi.name,
                number = poi.category
            )
        }
    }

    // Selected destinations
    var selectedDestination by remember { mutableStateOf<POI?>(null) }
    var showInstructions by remember { mutableStateOf(false) }

    // Speech recognition launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            spokenText?.let { results ->
                if (results.isNotEmpty()) {
                    voiceInput = results[0]
                    showVoiceResult = true

                    // Try to match the spoken text with available POIs
                    val matchedPOI = findBestMatch(voiceInput, availablePOIs)
                    if (matchedPOI != null) {
                        selectedDestination = matchedPOI
                    }
                }
            }
        }
    }

    // Function to start voice recognition
    fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dites le nom de votre destination")
        }

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            isListening = true
            speechRecognizerLauncher.launch(intent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xfffcfffe))
            .padding(top = 10.dp, start = 20.dp, end = 20.dp),
    ) {
        // Connection Status Indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (connectionStatus) Color.Green else Color.Red
                    )
            )
            Text(
                text = if (connectionStatus) "Connected to Navigation Service" else "Disconnected",
                style = TextStyle(fontSize = 12.sp),
                color = if (connectionStatus) Color.Green else Color.Red
            )
        }

        // Current Position Section
        Row(
            horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .requiredSize(size = 116.dp)
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color = Color(0xff3aafa9))
                    .padding(all = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.directright),
                    contentDescription = "vuesax/bold/direct-right",
                    modifier = Modifier.requiredSize(size = 70.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Position actuelle",
                    color = Color(0xff17252a),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Cyberespace",
                    color = Color(0xff17252a),
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Destination Selection Header
        Text(
            text = "Point d'arrivée",
            color = Color.Black,
            lineHeight = 5.43.em,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
        )

        // Voice Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Utiliser la reconnaissance vocale",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = Color(0xff17252a),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { startVoiceRecognition() },
                    enabled = !isListening && connectionStatus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) Color.Gray else Color(0xff3aafa9)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (isListening) "Écoute en cours..." else "Dire la destination",
                        color = Color.White,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                // Show voice input result
                if (showVoiceResult && voiceInput.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Vous avez dit:",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xff17252a)
                            )
                            Text(
                                text = "\"$voiceInput\"",
                                style = TextStyle(fontSize = 16.sp),
                                color = Color(0xff3aafa9),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            if (selectedDestination != null) {
                                Text(
                                    text = "✓ Destination trouvée: ${selectedDestination!!.name}",
                                    style = TextStyle(fontSize = 14.sp),
                                    color = Color.Green,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                Text(
                                    text = "❌ Destination non trouvée. Utilisez la liste ci-dessous.",
                                    style = TextStyle(fontSize = 14.sp),
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Traditional Dropdown Selection
        Text(
            text = "Ou choisir dans la liste:",
            color = Color.Gray,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CustomDropdown(
            label = "",
            items = poiOptions,
            initialValue = selectedDestination?.name ?: "Choisir Point d'arrivée",
            onItemSelected = { selectedItem ->
                selectedDestination = availablePOIs.find { it.name == selectedItem.label }
                showVoiceResult = false
            },
        )

        // Start Navigation Button
        if (selectedDestination != null && connectionStatus) {
            Button(
                onClick = {
                    selectedDestination?.let { poi ->
                        viewModel.sendNavigationRequest(poi.name, poi.coordinates)
                        showInstructions = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3aafa9))
            ) {
                Text(
                    text = "Démarrer la navigation vers ${selectedDestination!!.name}",
                    color = Color.White,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        // Obstacle Alert
        obstacleAlert?.let { alert ->
            if (alert.detected) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "⚠️ Obstacle Détecté",
                            color = Color(0xFFD32F2F),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = alert.description,
                            color = Color(0xFF5D4037),
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.clearObstacleAlert() },
                            modifier = Modifier.padding(top = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Compris", color = Color.White)
                        }
                    }
                }
            }
        }

        // Navigation Instructions
        if (showInstructions && navigationInstructions.isNotEmpty()) {
            Text(
                text = "Instructions de Navigation",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
            )

            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(navigationInstructions.takeLast(5)) { instruction ->
                    InstructionCard(instruction = instruction)
                }
            }
        }

        // Available POIs count
        Text(
            text = "Points d'intérêt disponibles: ${availablePOIs.size}",
            color = Color.Gray,
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

// Helper function to find the best match between spoken text and available POIs
fun findBestMatch(spokenText: String, availablePOIs: List<POI>): POI? {
    val normalizedSpoken = spokenText.lowercase().trim()

    // First, try exact match
    availablePOIs.forEach { poi ->
        if (poi.name.lowercase() == normalizedSpoken) {
            return poi
        }
    }

    // Then try partial match
    availablePOIs.forEach { poi ->
        if (poi.name.lowercase().contains(normalizedSpoken) ||
            normalizedSpoken.contains(poi.name.lowercase())) {
            return poi
        }
    }

    // Finally, try word-by-word matching
    val spokenWords = normalizedSpoken.split(" ")
    availablePOIs.forEach { poi ->
        val poiWords = poi.name.lowercase().split(" ")
        val matchingWords = spokenWords.intersect(poiWords.toSet())
        if (matchingWords.isNotEmpty() && matchingWords.size >= minOf(spokenWords.size, poiWords.size) / 2) {
            return poi
        }
    }

    return null
}

@Composable
fun InstructionCard(instruction: com.example.mobileuser_frontend.mqtt.NavigationInstruction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xff17252a))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = instruction.instruction,
                color = Color.White,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (instruction.distance.isNotEmpty()) {
                Text(
                    text = instruction.distance,
                    color = Color.White,
                    style = TextStyle(fontSize = 14.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (instruction.direction.isNotEmpty()) {
                Text(
                    text = instruction.direction,
                    color = Color.White,
                    style = TextStyle(fontSize = 14.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}