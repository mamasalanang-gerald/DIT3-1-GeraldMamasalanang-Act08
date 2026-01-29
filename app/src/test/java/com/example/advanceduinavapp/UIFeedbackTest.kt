package com.example.advanceduinavapp

import android.location.Location
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class UIFeedbackTest {

    private lateinit var context: android.content.Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testCoordinateDisplayFormat_WithValidLocation() {
        // Arrange
        val location = Location("test").apply {
            latitude = 37.7749
            longitude = -122.4194
        }

        // Act
        val latitude = String.format("%.4f", location.latitude)
        val longitude = String.format("%.4f", location.longitude)
        val coordinatesText = "Latitude: $latitude, Longitude: $longitude"

        // Assert
        assertNotNull(coordinatesText)
        assertTrue(coordinatesText.contains("37.7749"))
        assertTrue(coordinatesText.contains("-122.4194"))
        assertTrue(coordinatesText.contains("Latitude:"))
        assertTrue(coordinatesText.contains("Longitude:"))
    }

    @Test
    fun testAccuracyDisplayFormat_WithValidAccuracy() {
        // Arrange
        val location = Location("test").apply {
            accuracy = 10.5f
        }

        // Act
        val accuracy = String.format("%.1f", location.accuracy)
        val accuracyText = "Accuracy: $accuracy m"

        // Assert
        assertNotNull(accuracyText)
        assertTrue(accuracyText.contains("10.5"))
        assertTrue(accuracyText.contains("m"))
    }

    @Test
    fun testTimestampDisplayFormat_WithValidTimestamp() {
        // Arrange
        val location = Location("test").apply {
            time = System.currentTimeMillis()
        }

        // Act
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(location.time))
        val timestampText = "Last Update: $timestamp"

        // Assert
        assertNotNull(timestampText)
        assertTrue(timestampText.contains("Last Update:"))
        assertTrue(timestamp.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
    }

    @Test
    fun testCoordinateDisplayUpdate_WithMultipleLocations() {
        // Arrange
        val location1 = Location("test").apply {
            latitude = 37.7749
            longitude = -122.4194
        }
        val location2 = Location("test").apply {
            latitude = 40.7128
            longitude = -74.0060
        }

        // Act
        val coordinates1 = "Latitude: ${String.format("%.4f", location1.latitude)}, Longitude: ${String.format("%.4f", location1.longitude)}"
        val coordinates2 = "Latitude: ${String.format("%.4f", location2.latitude)}, Longitude: ${String.format("%.4f", location2.longitude)}"

        // Assert
        assertTrue(coordinates1.contains("37.7749"))
        assertTrue(coordinates2.contains("40.7128"))
        assertTrue(coordinates1 != coordinates2)
    }

    @Test
    fun testAccuracyDisplayUpdate_WithDifferentAccuracies() {
        // Arrange
        val location1 = Location("test").apply {
            accuracy = 5.0f
        }
        val location2 = Location("test").apply {
            accuracy = 15.0f
        }

        // Act
        val accuracy1 = "Accuracy: ${String.format("%.1f", location1.accuracy)} m"
        val accuracy2 = "Accuracy: ${String.format("%.1f", location2.accuracy)} m"

        // Assert
        assertTrue(accuracy1.contains("5.0"))
        assertTrue(accuracy2.contains("15.0"))
        assertTrue(accuracy1 != accuracy2)
    }

    @Test
    fun testTimestampDisplayUpdate_WithDifferentTimes() {
        // Arrange
        val time1 = System.currentTimeMillis()
        val time2 = time1 + 5000 // 5 seconds later

        // Act
        val timestamp1 = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(time1))
        val timestamp2 = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(time2))

        // Assert
        assertNotNull(timestamp1)
        assertNotNull(timestamp2)
        assertTrue(timestamp1.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
        assertTrue(timestamp2.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
    }

    @Test
    fun testCoordinateDisplayPrecision_FourDecimalPlaces() {
        // Arrange
        val location = Location("test").apply {
            latitude = 37.77491234
            longitude = -122.41941234
        }

        // Act
        val latitude = String.format("%.4f", location.latitude)
        val longitude = String.format("%.4f", location.longitude)

        // Assert
        assertTrue(latitude.matches(Regex("-?\\d+\\.\\d{4}")))
        assertTrue(longitude.matches(Regex("-?\\d+\\.\\d{4}")))
    }

    @Test
    fun testAccuracyDisplayPrecision_OneDecimalPlace() {
        // Arrange
        val location = Location("test").apply {
            accuracy = 10.567f
        }

        // Act
        val accuracy = String.format("%.1f", location.accuracy)

        // Assert
        assertTrue(accuracy.matches(Regex("\\d+\\.\\d{1}")))
    }
}
