# Mobile User Frontend - Voice Control
A Kotlin Android application with integrated voice recognition and text-to-speech capabilities for hands-free interaction, deployed as a Docker image for IoT device integration.

## Deployment Architecture
The application is containerized using Docker and deployed directly on IoT devices, providing a lightweight and portable solution for voice-controlled device management. The Docker deployment ensures consistent performance across different IoT hardware platforms.

## Voice Features

### Speech Recognition (STT)
The app integrates voice recognition capabilities for converting voice commands into text. Due to the significant size and hosting costs of advanced speech recognition models like Vosk, the application currently utilizes device-native speech recognition services as a cost-effective alternative for IoT deployment.

*Note: Initial attempts to deploy Vosk server were unsuccessful due to model size constraints and hosting limitations in IoT environments.*

### Text-to-Speech (TTS)  
The app can speak back to you in both **French** and **English**, providing audio feedback for all interactions and confirmations.

## Bilingual Support
* **French** (default)
* **English**  
* Switch between languages using voice commands or app settings
* Voice recognition and speech output automatically adapt to your selected language

## Voice Commands

| Category | English Command | French Command | Description |
|----------|----------------|----------------|-------------|
| **Device Control** | Battery | Batterie | Check your device's battery level |
| | Device Status | État du dispositif | Get current device state |
| | Connection | Connexion | Check network connectivity |
| | Device Type | Type d'appareil | Identify your device model |
| **Navigation** | Main Menu / Home | Menu principal / Accueil | Return to main screen |
| | Profile | Profil | Open user profile |
| | Device | Dispositif | Access device settings |
| | Settings | Paramètres | Open app settings |
| | Preferences | Préférences | View user preferences |
| **Emergency Calls** | Call Assistant | Appeler l'assistant | Contact your assigned assistant |
| | Emergency | Urgence | Call emergency services |
| | Police | Police | Contact police |
| | Fire Department | Pompiers | Call fire services |
| | Ambulance | Ambulance | Call ambulance |
| | Relative | Proche / Famille | Call family member |
| **Language Control** | Switch to French | Passer au français | Change to French language |
| | Switch to English | Passer à l'anglais | Change to English language |
| **System** | Help | Aide | Hear all available commands |
| | Stop | Arrêt | Exit voice mode |

## How It Works
1. **Speak**: Say your command in French or English
2. **Recognition**: The app processes your voice using device-native speech recognition
3. **Processing**: Your command is understood and executed within the IoT device
4. **Feedback**: The app speaks back to confirm the action

## Usage 
- create a folder on your computer and clone the repository with : git clone https://github.com/IrchadX/MobileFrontend.git
- change the server url in the WebSocketManager.kt file:  val serverUri = URI("ws://192.168.43.121:8765") to the vosk server url (IrchadTTS)
- both the mobile app and the server should be on the same network / use ngrok 
-  The model could not be deployed after many trials due to the size of the models that is not free to host. It's only deployed on a Docker image and used the day of the presentation with the IoT device and the mobile app.

- Go to com.example.mobileuser_frontend.module folder and change The Base_URL (on line 14 ) to https://apigateway-production-b99d.up.railway.app/api/v1/mobile
- Connect your phone to the computer or use android emulator , and click on the run app button on the center of the top bar.

