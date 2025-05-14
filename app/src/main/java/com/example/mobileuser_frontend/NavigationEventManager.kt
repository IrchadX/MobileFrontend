package com.example.mobileuser_frontend

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Singleton class that manages navigation events triggered by voice commands
 */
object NavigationEventManager {
    // Define navigation events
    enum class NavigationEvent {
        NAVIGATE_TO_HOME,
        NAVIGATE_TO_PROFILE,
        NAVIGATE_TO_SETTINGS,
        NAVIGATE_TO_NAVIGATION,
        NAVIGATE_TO_CALL
    }

    // Create a MutableSharedFlow that can be observed for navigation events
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    val navigationEvents = _navigationEvents.asSharedFlow()

    // Function to emit navigation events
    suspend fun navigateTo(event: NavigationEvent) {
        _navigationEvents.emit(event)
    }
}