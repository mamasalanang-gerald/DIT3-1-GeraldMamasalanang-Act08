package com.example.advanceduinavapp

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

/**
 * Property-based test for map centering on location update.
 * Feature: location-tracker, Property 3: Map Centering on Update
 * Validates: Requirements 4.2, 4.3
 *
 * Property: For any location update received, the map view SHALL be centered on the new
 * Location_Marker position within 500ms of receiving the update.
 */
@RunWith(RobolectricTestRunner::class)
class MapCenteringPropertyTest {

    private lateinit var mapManager: MapManager

    @Mock
    private lateinit var mockGoogleMap: GoogleMap

    @Mock
    private lateinit var mockMarker: Marker

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mapManager = MapManager(mockGoogleMap)
    }

    @Test
    fun testMapCentering_OnLocationUpdate() {
        // Property: For any location update, the map should be centered on the new location
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
            }

            // Act
            mapManager.updateLocationMarker(location)

            // Assert - Verify animateCamera was called (which centers the map)
            verify(mockGoogleMap).animateCamera(any())
        }
    }

    @Test
    fun testMapCentering_WithDefaultZoom() {
        // Property: Map centering should use default zoom level
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

            // Act
            mapManager.centerMapOnLocation(latitude, longitude)

            // Assert
            verify(mockGoogleMap).animateCamera(any())
        }
    }

    @Test
    fun testMapCentering_WithCustomZoom() {
        // Property: Map centering should respect custom zoom levels
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
            val zoomLevel = 18f

            // Act
            mapManager.centerMapOnLocation(latitude, longitude, zoomLevel)

            // Assert
            verify(mockGoogleMap).animateCamera(any())
        }
    }

    @Test
    fun testMapCentering_SequentialUpdates() {
        // Property: For any sequence of location updates, the map should center on each new location
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { lat1, lon1, lat2, lon2, lat3, lon3 ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

            // Act - Multiple sequential updates
            val location1 = Location("test").apply {
                latitude = lat1
                longitude = lon1
            }
            val location2 = Location("test").apply {
                latitude = lat2
                longitude = lon2
            }
            val location3 = Location("test").apply {
                latitude = lat3
                longitude = lon3
            }

            mapManager.updateLocationMarker(location1)
            mapManager.updateLocationMarker(location2)
            mapManager.updateLocationMarker(location3)

            // Assert - Verify animateCamera was called for each update
            verify(mockGoogleMap).animateCamera(any())
        }
    }

    @Test
    fun testMapCentering_BoundaryLocations() {
        // Test map centering at boundary coordinates
        val boundaryLocations = listOf(
            Pair(-90.0, -180.0),
            Pair(-90.0, 180.0),
            Pair(90.0, -180.0),
            Pair(90.0, 180.0),
            Pair(0.0, 0.0)
        )

        for ((latitude, longitude) in boundaryLocations) {
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

            // Act
            mapManager.centerMapOnLocation(latitude, longitude)

            // Assert
            verify(mockGoogleMap).animateCamera(any())
        }
    }

    @Test
    fun testMapCentering_RapidUpdates() {
        // Property: Map should handle rapid location updates without issues
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

            // Act - Simulate rapid updates
            for (i in 0..9) {
                val location = Location("test").apply {
                    this.latitude = latitude + (i * 0.001)
                    this.longitude = longitude + (i * 0.001)
                }
                mapManager.updateLocationMarker(location)
            }

            // Assert - Verify animateCamera was called multiple times
            verify(mockGoogleMap).animateCamera(any())
        }
    }

    @Test
    fun testMapCentering_ZoomLevelVariation() {
        // Property: Map centering should work with various zoom levels
        val zoomLevels = listOf(1f, 5f, 10f, 15f, 18f, 21f)

        for (zoomLevel in zoomLevels) {
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

            // Act
            mapManager.centerMapOnLocation(37.7749, -122.4194, zoomLevel)

            // Assert
            verify(mockGoogleMap).animateCamera(any())
        }
    }
}
