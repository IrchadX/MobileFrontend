package com.example.mobileuser_frontend.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobileuser_frontend.R
import com.example.mobileuser_frontend.mqtt.MqttNavigationManager
import com.example.mobileuser_frontend.mqtt.NavigationInstruction
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import java.util.*

// ViewModel for NavigationGuide
class NavigationGuideViewModel : androidx.lifecycle.ViewModel() {
    private lateinit var mqttManager: MqttNavigationManager
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var vibrator: Vibrator
    private var isTtsInitialized = false

    // Settings states
    private val _isVoiceEnabled = mutableStateOf(true)
    val isVoiceEnabled: State<Boolean> = _isVoiceEnabled

    private val _isHapticEnabled = mutableStateOf(true)
    val isHapticEnabled: State<Boolean> = _isHapticEnabled

    private val _lastSpokenInstruction = mutableStateOf("")
    val lastSpokenInstruction: State<String> = _lastSpokenInstruction

    val navigationInstructions get() = mqttManager.navigationInstructions
    val obstacleAlert get() = mqttManager.obstacleAlert
    val connectionStatus get() = mqttManager.connectionStatus

    fun initialize(context: Context) {
        mqttManager = MqttNavigationManager(context)
        mqttManager.connect()

        // Initialize Text-to-Speech
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.FRENCH)
                isTtsInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }

        // Initialize Vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun clearObstacleAlert() {
        mqttManager.clearObstacleAlert()
    }

    fun toggleVoice() {
        _isVoiceEnabled.value = !_isVoiceEnabled.value
    }

    fun toggleHaptic() {
        _isHapticEnabled.value = !_isHapticEnabled.value
    }

    fun speakInstruction(instruction: String) {
        if (_isVoiceEnabled.value && isTtsInitialized && instruction != _lastSpokenInstruction.value) {
            textToSpeech.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, null)
            _lastSpokenInstruction.value = instruction
        }
    }

    fun repeatLastInstruction() {
        if (_isVoiceEnabled.value && isTtsInitialized && _lastSpokenInstruction.value.isNotEmpty()) {
            textToSpeech.speak(_lastSpokenInstruction.value, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun triggerHapticFeedback(type: HapticType = HapticType.INSTRUCTION) {
        if (_isHapticEnabled.value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticType.INSTRUCTION -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticType.OBSTACLE -> VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200, 100, 200), -1)
                    HapticType.DIRECTION_CHANGE -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.INSTRUCTION -> vibrator.vibrate(200)
                    HapticType.OBSTACLE -> vibrator.vibrate(longArrayOf(0, 200, 100, 200, 100, 200), -1)
                    HapticType.DIRECTION_CHANGE -> vibrator.vibrate(100)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::mqttManager.isInitialized) {
            mqttManager.disconnect()
        }
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}

enum class HapticType {
    INSTRUCTION,
    OBSTACLE,
    DIRECTION_CHANGE
}

@Composable
fun NavigationGuide(
    viewModel: NavigationGuideViewModel = viewModel()
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    // Initialize MQTT manager
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    // Collect states
    val navigationInstructions by viewModel.navigationInstructions.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val obstacleAlert by viewModel.obstacleAlert.collectAsState()
    val isVoiceEnabled by viewModel.isVoiceEnabled
    val isHapticEnabled by viewModel.isHapticEnabled

    // Get the latest instructions
    val currentInstruction = navigationInstructions.lastOrNull()
    val nextInstruction = if (navigationInstructions.size >= 2) {
        navigationInstructions[navigationInstructions.size - 2]
    } else null

    // Handle new instructions - trigger voice and haptic feedback
    LaunchedEffect(currentInstruction) {
        currentInstruction?.let { instruction ->
            viewModel.speakInstruction(instruction.instruction + ". " + instruction.distance)
            viewModel.triggerHapticFeedback(HapticType.INSTRUCTION)
        }
    }

    // Handle obstacle alerts
    LaunchedEffect(obstacleAlert) {
        obstacleAlert?.let { alert ->
            if (alert.detected) {
                viewModel.speakInstruction("Attention! Obstacle détecté. ${alert.description}")
                viewModel.triggerHapticFeedback(HapticType.OBSTACLE)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color(0xfffcfffe))
            .padding(top = 106.dp, start = 20.dp, end = 20.dp),
    ) {
        // Control Panel for Voice and Haptic Settings
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Voice Toggle
            OutlinedButton(
                onClick = {
                    viewModel.toggleVoice()
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isVoiceEnabled) Color(0xff3aafa9) else Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Voice Toggle",
                    tint = if (isVoiceEnabled) Color.White else Color(0xff3aafa9)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isVoiceEnabled) "Voice ON" else "Voice OFF",
                    color = if (isVoiceEnabled) Color.White else Color(0xff3aafa9)
                )
            }

            // Haptic Toggle
            OutlinedButton(
                onClick = {
                    viewModel.toggleHaptic()
                    if (!isHapticEnabled) {
                        viewModel.triggerHapticFeedback(HapticType.DIRECTION_CHANGE)
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isHapticEnabled) Color(0xff3aafa9) else Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.directright), // Replace with vibration icon
                    contentDescription = "Haptic Toggle",
                    tint = if (isHapticEnabled) Color.White else Color(0xff3aafa9),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHapticEnabled) "Haptic ON" else "Haptic OFF",
                    color = if (isHapticEnabled) Color.White else Color(0xff3aafa9)
                )
            }

            // Repeat Last Instruction Button
            IconButton(
                onClick = {
                    viewModel.repeatLastInstruction()
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Repeat Instruction",
                    tint = Color(0xff3aafa9)
                )
            }
        }

        // Connection Status
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
                text = if (connectionStatus) "Navigation Active" else "Navigation Inactive",
                style = TextStyle(fontSize = 14.sp),
                color = if (connectionStatus) Color.Green else Color.Red
            )
        }

        // Current Position Header
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
                    modifier = Modifier.requiredSize(size = 67.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Position actuelle",
                    color = Color(0xff17252a),
                    lineHeight = 8.em,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Navigation en cours...",
                    color = Color(0xff17252a),
                    lineHeight = 6.51.em,
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Obstacle Alert (Priority Display)
        obstacleAlert?.let { alert ->
            if (alert.detected) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color(0xFFE57373))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "⚠️ OBSTACLE DÉTECTÉ",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = alert.description,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.clearObstacleAlert()
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Compris", color = Color(0xFFE57373))
                    }
                }
            }
        }

        // Navigation Instructions
        if (obstacleAlert?.detected != true) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Current Instruction
                if (currentInstruction != null) {
                    NavigationInstructionCard(
                        instruction = currentInstruction,
                        isCurrentInstruction = true,
                        onCardClick = {
                            viewModel.speakInstruction(currentInstruction.instruction + ". " + currentInstruction.distance)
                            viewModel.triggerHapticFeedback(HapticType.DIRECTION_CHANGE)
                        }
                    )
                } else {
                    // Default instruction when no MQTT instructions available
                    NavigationInstructionCard(
                        instruction = NavigationInstruction(
                            instruction = "En attente d'instructions",
                            distance = "Connexion au système de navigation...",
                            direction = "",
                            timestamp = System.currentTimeMillis()
                        ),
                        isCurrentInstruction = true,
                        onCardClick = {
                            viewModel.speakInstruction("En attente d'instructions de navigation")
                        }
                    )
                }

                // Next Instruction
                if (nextInstruction != null) {
                    NavigationInstructionCard(
                        instruction = nextInstruction,
                        isCurrentInstruction = false,
                        onCardClick = {
                            viewModel.speakInstruction("Prochaine instruction: " + nextInstruction.instruction + ". " + nextInstruction.distance)
                            viewModel.triggerHapticFeedback(HapticType.DIRECTION_CHANGE)
                        }
                    )
                } else if (currentInstruction != null) {
                    // Show a preview of next step
                    NavigationInstructionCard(
                        instruction = NavigationInstruction(
                            instruction = "Prochaine instruction",
                            distance = "À venir...",
                            direction = "",
                            timestamp = System.currentTimeMillis()
                        ),
                        isCurrentInstruction = false,
                        onCardClick = {
                            viewModel.speakInstruction("Prochaine instruction à venir")
                        }
                    )
                }

                // Instructions count
                if (navigationInstructions.isNotEmpty()) {
                    Text(
                        text = "Instructions reçues: ${navigationInstructions.size}",
                        color = Color.Gray,
                        style = TextStyle(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationInstructionCard(
    instruction: NavigationInstruction,
    isCurrentInstruction: Boolean,
    onCardClick: () -> Unit = {}
) {
    val backgroundColor = if (isCurrentInstruction) Color(0xff3aafa9) else Color(0xff17252a)
    val textColor = Color.White

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = backgroundColor)
            .clickable { onCardClick() }
            .padding(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isCurrentInstruction) "INSTRUCTION ACTUELLE" else "PROCHAINE INSTRUCTION",
                color = textColor,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Tap to hear",
                tint = textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = instruction.instruction,
            color = textColor,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = if (isCurrentInstruction) 24.sp else 20.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        if (instruction.distance.isNotEmpty()) {
            Text(
                text = instruction.distance,
                color = textColor,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = if (isCurrentInstruction) 18.sp else 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (instruction.direction.isNotEmpty()) {
            Text(
                text = "Direction: ${instruction.direction}",
                color = textColor,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 16.sp,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isCurrentInstruction) {
            Text(
                text = "Appuyez pour répéter",
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}