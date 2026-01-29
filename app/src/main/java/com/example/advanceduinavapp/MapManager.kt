package com.example.advanceduinavapp

import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Manages Google Maps operations including marker placement and map centering.
 */
class MapManager(private var googleMap: GoogleMap? = null) {

    private var currentMarker: Marker? = null

    /**
     * Initializes the map with the provided GoogleMap instance.
     *
     * @param map The GoogleMap instance to manage
     */
    fun initializeMap(map: GoogleMap) {
        googleMap = map
    }

    /**
     * Adds or updates a location marker on the map.
     *
     * @param latitude The latitude of the marker
     * @param longitude The longitude of the marker
     * @param title Optional title for the marker
     */
    fun addLocationMarker(latitude: Double, longitude: Double, title: String = "Current Location") {
        googleMap?.let { map ->
            val location = LatLng(latitude, longitude)

            // Remove existing marker if present
            currentMarker?.remove()

            // Add new marker
            currentMarker = map.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(title)
            )
        }
    }

    /**
     * Centers the map view on the specified location.
     *
     * @param latitude The latitude to center on
     * @param longitude The longitude to center on
     * @param zoomLevel The zoom level (default 15f for street level)
     */
    fun centerMapOnLocation(latitude: Double, longitude: Double, zoomLevel: Float = 15f) {
        googleMap?.let { map ->
            val location = LatLng(latitude, longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
        }
    }

    /**
     * Updates the location marker position and centers the map on it.
     *
     * @param location The Location object containing latitude and longitude
     */
    fun updateLocationMarker(location: Location) {
        addLocationMarker(location.latitude, location.longitude)
        centerMapOnLocation(location.latitude, location.longitude)
    }

    /**
     * Clears all markers from the map.
     */
    fun clearMarkers() {
        googleMap?.clear()
        currentMarker = null
    }

    /**
     * Gets the current marker position.
     *
     * @return LatLng of the current marker, or null if no marker exists
     */
    fun getCurrentMarkerPosition(): LatLng? {
        return currentMarker?.position
    }

    /**
     * Checks if a marker is currently displayed on the map.
     *
     * @return true if a marker exists, false otherwise
     */
    fun hasMarker(): Boolean {
        return currentMarker != null
    }
}
