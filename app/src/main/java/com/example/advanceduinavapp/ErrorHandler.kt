package com.example.advanceduinavapp

import android.content.Context
import android.location.LocationManager as AndroidLocationManager

/**
 * Handles error scenarios and provides user-friendly error messages.
 */
class ErrorHandler(private val context: Context) {

    /**
     * Checks if location services are enabled on the device.
     *
     * @return true if location services are enabled, false otherwise
     */
    fun isLocationServicesEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
        return locationManager.isLocationEnabled
    }

    /**
     * Gets an error message for location services disabled.
     *
     * @return User-friendly error message
     */
    fun getLocationServicesDisabledMessage(): String {
        return "Location services are disabled. Please enable them in your device settings."
    }

    /**
     * Gets an error message for permission denied.
     *
     * @return User-friendly error message
     */
    fun getPermissionDeniedMessage(): String {
        return "Location permission is required to use this app. Please grant permission in settings."
    }

    /**
     * Gets an error message for location retrieval failure.
     *
     * @return User-friendly error message
     */
    fun getLocationRetrievalFailedMessage(): String {
        return "Unable to retrieve your location. Please ensure location services are enabled and try again."
    }

    /**
     * Gets an error message for max retries exceeded.
     *
     * @param maxRetries The maximum number of retries attempted
     * @return User-friendly error message
     */
    fun getMaxRetriesExceededMessage(maxRetries: Int): String {
        return "Failed to get location after $maxRetries attempts. Please check your location settings and try again."
    }

    /**
     * Gets an error message for invalid location data.
     *
     * @return User-friendly error message
     */
    fun getInvalidLocationDataMessage(): String {
        return "Received invalid location data. Retrying..."
    }

    /**
     * Validates if an error is recoverable.
     *
     * @param exception The exception to check
     * @return true if the error is recoverable, false otherwise
     */
    fun isRecoverableError(exception: Exception): Boolean {
        return when (exception) {
            is SecurityException -> false // Permission error is not recoverable without user action
            is IllegalStateException -> false // State error is not recoverable
            else -> true // Other errors might be recoverable
        }
    }
}
