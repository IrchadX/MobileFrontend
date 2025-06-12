package com.example.mobileuser_frontend.pages

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobileuser_frontend.module.CustomDropdown
import com.example.mobileuser_frontend.module.ListItems
import com.example.mobileuser_frontend.VoiceCommandHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Preferences(
    navController: NavController,
    voiceCommandHandler: VoiceCommandHandler? = null
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Language options - Convert to ListItems
    val languageOptions = listOf(
        ListItems("Français", "fr"),
        ListItems("English", "en")
    )

    // Voice options - Convert to ListItems
    val voiceOptions = listOf(
        ListItems("Voix Féminine", "female"),
        ListItems("Voix Masculine", "male")
    )

    // Sensitivity options - Convert to ListItems
    val sensitivityOptions = listOf(
        ListItems("Faible", "low"),
        ListItems("Moyen", "medium"),
        ListItems("Élevé", "high")
    )

    // Get current saved values
    var selectedLanguage by remember {
        mutableStateOf(
            when(sharedPref.getString("selected_language", "fr")) {
                "fr" -> "Français"
                "en" -> "English"
                else -> "Français"
            }
        )
    }

    var selectedVoice by remember {
        mutableStateOf(
            when(sharedPref.getString("selected_voice", "female")) {
                "female" -> "Voix Féminine"
                "male" -> "Voix Masculine"
                else -> "Voix Féminine"
            }
        )
    }

    var selectedSensitivity by remember {
        mutableStateOf(
            when(sharedPref.getString("selected_sensitivity", "medium")) {
                "low" -> "Faible"
                "medium" -> "Moyen"
                "high" -> "Élevé"
                else -> "Moyen"
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight-10.dp)
            .background(color = Color(0xfffcfffe))
    ) {
        Text(
            text = "Préférences de Navigation",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.Start)
                .padding(10.dp),
        )

        Text(
            text = "Guidage Vocal",
            color = Color(0xff17252a),
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            ),
            modifier = Modifier.align(Alignment.Start)
                .padding(start = 10.dp),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = "Langue",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )

            CustomDropdown(
                label = "",
                items = languageOptions,
                initialValue = selectedLanguage,
                onItemSelected = { selectedItem ->
                    selectedLanguage = selectedItem.label
                },
            )

            Text(
                text = "Genre de Voix",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )

            CustomDropdown(
                label = "",
                items = voiceOptions,
                initialValue = selectedVoice,
                onItemSelected = { selectedItem ->
                    selectedVoice = selectedItem.label
                },
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )

            // Vibration Section
            Text(
                text = "Vibration",
                color = Color(0xff17252a),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.align(Alignment.Start),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Sensibilité",
                    color = Color(0xff17252a),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )

                CustomDropdown(
                    label = "",
                    items = sensitivityOptions,
                    initialValue = selectedSensitivity,
                    onItemSelected = { selectedItem ->
                        selectedSensitivity = selectedItem.label
                    },
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }

            // Update the Save button onClick in your Preferences composable

            Button(
                onClick = {
                    Log.d("Preferences", "Save button clicked")

                    // Save preferences
                    val editor = sharedPref.edit()

                    // Convert display language to language code
                    val languageCode = when(selectedLanguage) {
                        "Français" -> "fr"
                        "English" -> "en"
                        else -> "fr"
                    }

                    // Convert display voice to voice code
                    val voiceCode = when(selectedVoice) {
                        "Voix Féminine" -> "female"
                        "Voix Masculine" -> "male"
                        else -> "female"
                    }

                    // Convert display sensitivity to sensitivity code
                    val sensitivityCode = when(selectedSensitivity) {
                        "Faible" -> "low"
                        "Moyen" -> "medium"
                        "Élevé" -> "high"
                        else -> "medium"
                    }

                    // Check if language or voice changed BEFORE saving
                    val previousLanguage = sharedPref.getString("selected_language", "fr")
                    val previousVoice = sharedPref.getString("selected_voice", "female")
                    val languageChanged = previousLanguage != languageCode
                    val voiceChanged = previousVoice != voiceCode

                    Log.d("Preferences", "Previous language: $previousLanguage, New language: $languageCode, Changed: $languageChanged")
                    Log.d("Preferences", "Previous voice: $previousVoice, New voice: $voiceCode, Changed: $voiceChanged")

                    // Save to SharedPreferences
                    editor.putString("selected_language", languageCode)
                    editor.putString("selected_voice", voiceCode)
                    editor.putString("selected_sensitivity", sensitivityCode)
                    val saved = editor.commit() // Use commit() instead of apply() for immediate save

                    Log.d("Preferences", "Preferences saved: $saved")

                    // Send updates to VoiceCommandHandler if settings changed
                    if (voiceCommandHandler != null) {
                        if (languageChanged) {
                            Log.d("Preferences", "Language changed, notifying server immediately")
                            voiceCommandHandler.onLanguageChanged()
                        } else if (voiceChanged) {
                            Log.d("Preferences", "Voice changed, updating TTS")
                            voiceCommandHandler.onVoiceChanged()
                        }

                        // Test the voice with new settings
                        if (languageChanged || voiceChanged) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500) // Give time for TTS to update
                                voiceCommandHandler.testVoice() // Test the new voice
                                delay(1000) // Let the test complete before navigation
                                navController.popBackStack()
                            }
                        } else {
                            navController.popBackStack()
                        }
                    } else {
                        Log.w("Preferences", "VoiceCommandHandler is null - cannot update TTS settings")
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3AAFA9)),
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp)
            ) {
                Text(
                    text = "Sauvegarder",
                    color = Color(0xfffcfffe),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
        }
    }
}