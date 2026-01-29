package com.example.advanceduinavapp

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Handles runtime permission requests and status checking for location permissions.
 */
class PermissionHandler(private val context: Context) {

    /**
     * Checks if a specific permission is granted.
     *
     * @param permission The permission to check (e.g., Manifest.permission.ACCESS_FINE_LOCATION)
     * @return true if permission is granted, false otherwise
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests a specific permission using the provided launcher.
     *
     * @param launcher The ActivityResultLauncher for requesting permissions
     * @param permission The permission to request
     */
    fun requestPermission(launcher: ActivityResultLauncher<String>, permission: String) {
        launcher.launch(permission)
    }

    /**
     * Requests multiple permissions using the provided launcher.
     *
     * @param launcher The ActivityResultLauncher for requesting permissions
     * @param permissions Array of permissions to request
     */
    fun requestPermissions(launcher: ActivityResultLauncher<Array<String>>, permissions: Array<String>) {
        launcher.launch(permissions)
    }

    /**
     * Checks if the app should show a rationale for requesting a permission.
     * This is used to determine if we should explain why we need the permission.
     *
     * @param activity The activity context
     * @param permission The permission to check
     * @return true if rationale should be shown, false otherwise
     */
    fun shouldShowPermissionRationale(activity: androidx.activity.ComponentActivity, permission: String): Boolean {
        return androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Checks if location permission (ACCESS_FINE_LOCATION) is granted.
     *
     * @return true if location permission is granted, false otherwise
     */
    fun isLocationPermissionGranted(): Boolean {
        return isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Requests location permission (ACCESS_FINE_LOCATION).
     *
     * @param launcher The ActivityResultLauncher for requesting permissions
     */
    fun requestLocationPermission(launcher: ActivityResultLauncher<String>) {
        requestPermission(launcher, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
