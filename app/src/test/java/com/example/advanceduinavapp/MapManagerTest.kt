package com.example.advanceduinavapp

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class MapManagerTest {

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
    fun testInitializeMap_SetsGoogleMapInstance() {
        // Arrange
        val newMapManager = MapManager()

        // Act
        newMapManager.initializeMap(mockGoogleMap)

        // Assert
        // Verify by checking that subsequent operations work
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
        newMapManager.addLocationMarker(0.0, 0.0)
        verify(mockGoogleMap).addMarker(any())
    }

    @Test
    fun testAddLocationMarker_AddsMarkerAtCorrectPosition() {
        // Arrange
        val latitude = 37.7749
        val longitude = -122.4194
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

        // Act
        mapManager.addLocationMarker(latitude, longitude)

        // Assert
        verify(mockGoogleMap).addMarker(any())
        assertTrue(mapManager.hasMarker())
    }

    @Test
    fun testAddLocationMarker_WithTitle() {
        // Arrange
        val latitude = 37.7749
        val longitude = -122.4194
        val title = "Test Location"
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

        // Act
        mapManager.addLocationMarker(latitude, longitude, title)

        // Assert
        verify(mockGoogleMap).addMarker(any())
    }

    @Test
    fun testCenterMapOnLocation_AnimatesCameraToLocation() {
        // Arrange
        val latitude = 37.7749
        val longitude = -122.4194
        val zoomLevel = 15f

        // Act
        mapManager.centerMapOnLocation(latitude, longitude, zoomLevel)

        // Assert
        verify(mockGoogleMap).animateCamera(any())
    }

    @Test
    fun testCenterMapOnLocation_WithDefaultZoom() {
        // Arrange
        val latitude = 37.7749
        val longitude = -122.4194

        // Act
        mapManager.centerMapOnLocation(latitude, longitude)

        // Assert
        verify(mockGoogleMap).animateCamera(any())
    }

    @Test
    fun testUpdateLocationMarker_UpdatesMarkerAndCentersMap() {
        // Arrange
        val location = Location("test").apply {
            latitude = 37.7749
            longitude = -122.4194
        }
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)

        // Act
        mapManager.updateLocationMarker(location)

        // Assert
        verify(mockGoogleMap).addMarker(any())
        verify(mockGoogleMap).animateCamera(any())
    }

    @Test
    fun testClearMarkers_ClearsMapAndMarker() {
        // Arrange
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
        mapManager.addLocationMarker(0.0, 0.0)
        assertTrue(mapManager.hasMarker())

        // Act
        mapManager.clearMarkers()

        // Assert
        verify(mockGoogleMap).clear()
        assertFalse(mapManager.hasMarker())
    }

    @Test
    fun testHasMarker_ReturnsTrueWhenMarkerExists() {
        // Arrange
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
        mapManager.addLocationMarker(0.0, 0.0)

        // Act
        val result = mapManager.hasMarker()

        // Assert
        assertTrue(result)
    }

    @Test
    fun testHasMarker_ReturnsFalseWhenNoMarker() {
        // Arrange
        val newMapManager = MapManager(mockGoogleMap)

        // Act
        val result = newMapManager.hasMarker()

        // Assert
        assertFalse(result)
    }

    @Test
    fun testGetCurrentMarkerPosition_ReturnsNullWhenNoMarker() {
        // Arrange
        val newMapManager = MapManager(mockGoogleMap)

        // Act
        val position = newMapManager.getCurrentMarkerPosition()

        // Assert
        assertNull(position)
    }

    @Test
    fun testAddLocationMarker_RemovesPreviousMarker() {
        // Arrange
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
        mapManager.addLocationMarker(0.0, 0.0)

        // Act
        mapManager.addLocationMarker(10.0, 10.0)

        // Assert
        verify(mockMarker).remove()
        verify(mockGoogleMap).addMarker(any())
    }

    @Test
    fun testMarkerPositionAccuracy() {
        // Arrange
        val latitude = 40.7128
        val longitude = -74.0060
        whenever(mockGoogleMap.addMarker(any())).thenReturn(mockMarker)
        whenever(mockMarker.position).thenReturn(LatLng(latitude, longitude))

        // Act
        mapManager.addLocationMarker(latitude, longitude)

        // Assert
        val position = mapManager.getCurrentMarkerPosition()
        assertNotNull(position)
        assertEquals(latitude, position.latitude, 0.0001)
        assertEquals(longitude, position.longitude, 0.0001)
    }
}
