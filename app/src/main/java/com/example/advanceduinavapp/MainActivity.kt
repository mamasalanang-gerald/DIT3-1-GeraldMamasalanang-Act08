package com.example.advanceduinavapp

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main activity for the Location Tracker app.
 * Manages map display, location permissions, and location updates.
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var locationManager: LocationManager
    private lateinit var mapManager: MapManager
    private lateinit var errorHandler: ErrorHandler
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var googleMap: GoogleMap? = null
    private var isLocationUpdating = false
    private var retryCount = 0
    private val maxRetries = 3

    // UI Elements
    private lateinit var coordinatesText: TextView
    private lateinit var accuracyText: TextView
    private lateinit var timestampText: TextView
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: Button

    companion object {
        private const val TAG = "LocationTracker"
    }

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (errorHandler.isLocationServicesEnabled()) {
                startLocationUpdates()
            } else {
                showErrorMessage(errorHandler.getLocationServicesDisabledMessage())
            }
        } else {
            showErrorMessage(errorHandler.getPermissionDeniedMessage())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity created")

        // Initialize managers
        permissionHandler = PermissionHandler(this)
        errorHandler = ErrorHandler(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = LocationManager(this, fusedLocationClient)
        mapManager = MapManager()

        // Initialize UI elements
        coordinatesText = findViewById(R.id.coordinatesText)
        accuracyText = findViewById(R.id.accuracyText)
        timestampText = findViewById(R.id.timestampText)
        errorMessage = findViewById(R.id.errorMessage)
        retryButton = findViewById(R.id.retryButton)

        retryButton.setOnClickListener {
            retryCount = 0
            startLocationUpdates()
        }

        // Initialize map
        try {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            if (mapFragment != null) {
                Log.d(TAG, "Map fragment found, requesting map async")
                mapFragment.getMapAsync(this)
            } else {
                Log.e(TAG, "Map fragment not found in layout")
                showErrorMessage("Map fragment not found. Please check your layout configuration.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize map fragment", e)
            showErrorMessage("Failed to initialize map: ${e.message}")
        }

        // Request location permission if not granted
        if (!permissionHandler.isLocationPermissionGranted()) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            if (errorHandler.isLocationServicesEnabled()) {
                startLocationUpdates()
            } else {
                showErrorMessage(errorHandler.getLocationServicesDisabledMessage())
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "Map ready callback received")
        googleMap = map
        mapManager.initializeMap(map)
        
        // Set map type to normal
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        // If permission is already granted, get last known location
        if (permissionHandler.isLocationPermissionGranted()) {
            locationManager.getLastKnownLocation(
                onSuccess = { location ->
                    Log.d(TAG, "Last known location: ${location.latitude}, ${location.longitude}")
                    updateUI(location)
                    mapManager.updateLocationMarker(location)
                },
                onFailure = { exception ->
                    Log.d(TAG, "Last known location not available: ${exception.message}")
                    // Last known location not available, will get it from updates
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionHandler.isLocationPermissionGranted() && !isLocationUpdating) {
            if (errorHandler.isLocationServicesEnabled()) {
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (!permissionHandler.isLocationPermissionGranted()) {
            showErrorMessage(errorHandler.getPermissionDeniedMessage())
            return
        }

        if (!errorHandler.isLocationServicesEnabled()) {
            showErrorMessage(errorHandler.getLocationServicesDisabledMessage())
            return
        }

        isLocationUpdating = true
        hideErrorMessage()
        retryButton.visibility = Button.GONE
        Log.d(TAG, "Starting location updates")

        locationManager.requestLocationUpdates { location ->
            Log.d(TAG, "Location update received: ${location.latitude}, ${location.longitude}")
            updateUI(location)
            mapManager.updateLocationMarker(location)
            retryCount = 0 // Reset retry count on successful update
        }
    }

    private fun stopLocationUpdates() {
        isLocationUpdating = false
        locationManager.stopLocationUpdates()
        Log.d(TAG, "Stopped location updates")
    }

    private fun updateUI(location: Location) {
        val latitude = String.format("%.4f", location.latitude)
        val longitude = String.format("%.4f", location.longitude)
        val accuracy = String.format("%.1f", location.accuracy)
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(location.time))

        coordinatesText.text = "Latitude: $latitude, Longitude: $longitude"
        accuracyText.text = "Accuracy: $accuracy m"
        timestampText.text = "Last Update: $timestamp"
    }

    private fun showErrorMessage(message: String) {
        Log.e(TAG, "Error: $message")
        errorMessage.text = message
        errorMessage.visibility = TextView.VISIBLE
        retryButton.visibility = Button.VISIBLE
    }

    private fun hideErrorMessage() {
        errorMessage.visibility = TextView.GONE
    }

    private fun retryLocationUpdates() {
        retryCount++
        if (retryCount < maxRetries) {
            lifecycleScope.launch {
                delay(5000) // Wait 5 seconds before retrying
                startLocationUpdates()
            }
        } else {
            showErrorMessage(errorHandler.getMaxRetriesExceededMessage(maxRetries))
        }
    }
}
