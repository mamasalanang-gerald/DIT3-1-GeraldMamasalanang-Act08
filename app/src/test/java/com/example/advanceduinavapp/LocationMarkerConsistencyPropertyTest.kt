package com.example.advanceduinavapp

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
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
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Property-based test for location marker consistency.
 * Feature: location-tracker, Property 2: Location Marker Consistency
 * Validates: Requirements 3.1, 3.2
 *
 * Property: For any valid location data received, the Location_Marker on the map
 * SHALL be positioned at the exact coordinates (latitude, longitude) provided by the Location_Provider.
 */
@RunWith(RobolectricTestRunner::class)
class LocationMarkerConsistencyPropertyTest {

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
    fun testLocationMarkerConsistency_WithValidCoordinates() {
        // Property: For any valid location coordinates, the marker should be positioned exactly at those coordinates
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
            whenever(mockMarker.position).thenReturn(LatLng(latitude, longitude))

            // Act
            mapManager.addLocationMarker(latitude, longitude)

            // Assert
            val markerPosition = mapManager.getCurrentMarkerPosition()
            assertNotNull(markerPosition)
            assertEquals(latitude, markerPosition.latitude, 0.0001)
            assertEquals(longitude, markerPosition.longitude, 0.0001)
        }
    }

    @Test
    fun testLocationMarkerConsistency_WithLocationObject() {
        // Property: For any valid Location object, the marker should be positioned at its coordinates
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
            }
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
            whenever(mockMarker.position).thenReturn(LatLng(latitude, longitude))

            // Act
            mapManager.updateLocationMarker(location)

            // Assert
            val markerPosition = mapManager.getCurrentMarkerPosition()
            assertNotNull(markerPosition)
            assertEquals(latitude, markerPosition.latitude, 0.0001)
            assertEquals(longitude, markerPosition.longitude, 0.0001)
        }
    }

    @Test
    fun testLocationMarkerConsistency_BoundaryValues() {
        // Test boundary values for latitude and longitude
        val boundaryCoordinates = listOf(
            Pair(-90.0, -180.0),
            Pair(-90.0, 180.0),
            Pair(90.0, -180.0),
            Pair(90.0, 180.0),
            Pair(0.0, 0.0)
        )

        for ((latitude, longitude) in boundaryCoordinates) {
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
            whenever(mockMarker.position).thenReturn(LatLng(latitude, longitude))

            // Act
            mapManager.addLocationMarker(latitude, longitude)

            // Assert
            val markerPosition = mapManager.getCurrentMarkerPosition()
            assertNotNull(markerPosition)
            assertEquals(latitude, markerPosition.latitude, 0.0001)
            assertEquals(longitude, markerPosition.longitude, 0.0001)
        }
    }

    @Test
    fun testLocationMarkerConsistency_SequentialUpdates() {
        // Property: For any sequence of location updates, each marker should be positioned at its respective coordinates
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { lat1, lon1, lat2, lon2 ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

            // Act - First update
            whenever(mockMarker.position).thenReturn(LatLng(lat1, lon1))
            mapManager.addLocationMarker(lat1, lon1)
            val position1 = mapManager.getCurrentMarkerPosition()

            // Act - Second update
            whenever(mockMarker.position).thenReturn(LatLng(lat2, lon2))
            mapManager.addLocationMarker(lat2, lon2)
            val position2 = mapManager.getCurrentMarkerPosition()

            // Assert
            assertNotNull(position1)
            assertNotNull(position2)
            assertEquals(lat1, position1.latitude, 0.0001)
            assertEquals(lon1, position1.longitude, 0.0001)
            assertEquals(lat2, position2.latitude, 0.0001)
            assertEquals(lon2, position2.longitude, 0.0001)
        }
    }

    @Test
    fun testLocationMarkerConsistency_PrecisionPreservation() {
        // Property: Marker position should preserve coordinate precision
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0)
        ) { latitude, longitude ->
            // Arrange
            whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
            whenever(mockMarker.position).thenReturn(LatLng(latitude, longitude))

            // Act
            mapManager.addLocationMarker(latitude, longitude)

            // Assert
            val markerPosition = mapManager.getCurrentMarkerPosition()
            assertNotNull(markerPosition)
            // Verify precision is maintained to at least 4 decimal places
            assertEquals(latitude, markerPosition.latitude, 0.00001)
            assertEquals(longitude, markerPosition.longitude, 0.00001)
        }
    }
}
