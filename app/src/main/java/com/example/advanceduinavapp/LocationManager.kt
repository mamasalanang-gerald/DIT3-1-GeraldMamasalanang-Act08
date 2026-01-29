package com.example.advanceduinavapp

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

/**
 * Manages location services and provides location updates.
 * Handles permission checking, location requests, and location data validation.
 */
class LocationManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    private var locationCallback: LocationCallback? = null
    private var onLocationUpdate: ((Location) -> Unit)? = null

    /**
     * Checks if location permission is granted.
     *
     * @return true if ACCESS_FINE_LOCATION permission is granted, false otherwise
     */
    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    /**
     * Validates location data to ensure it contains valid coordinates and accuracy.
     *
     * @param location The location to validate
     * @return true if location is valid, false otherwise
     */
    fun validateLocation(location: Location): Boolean {
        val latitude = location.latitude
        val longitude = location.longitude
        val accuracy = location.accuracy

        // Validate latitude range: -90 to 90
        if (latitude < -90 || latitude > 90) {
            return false
        }

        // Validate longitude range: -180 to 180
        if (longitude < -180 || longitude > 180) {
            return false
        }

        // Validate accuracy is positive
        if (accuracy < 0) {
            return false
        }

        return true
    }

    /**
     * Requests location updates with the specified callback.
     * Requires ACCESS_FINE_LOCATION permission to be granted.
     *
     * @param onUpdate Callback function to be called when location is updated
     */
    fun requestLocationUpdates(onUpdate: (Location) -> Unit) {
        if (!isLocationPermissionGranted()) {
            return
        }

        onLocationUpdate = onUpdate

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (validateLocation(location)) {
                        onLocationUpdate?.invoke(location)
                    }
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission was revoked
        }
    }

    /**
     * Stops receiving location updates.
     */
    fun stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
            locationCallback = null
        }
        onLocationUpdate = null
    }

    /**
     * Gets the last known location.
     * Requires ACCESS_FINE_LOCATION permission to be granted.
     *
     * @param onSuccess Callback with the last known location
     * @param onFailure Callback if location retrieval fails
     */
    fun getLastKnownLocation(
        onSuccess: (Location) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (!isLocationPermissionGranted()) {
            onFailure(SecurityException("Location permission not granted"))
            return
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && validateLocation(location)) {
                        onSuccess(location)
                    } else {
                        onFailure(Exception("Invalid or null location"))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } catch (e: SecurityException) {
            onFailure(e)
        }
    }
}
