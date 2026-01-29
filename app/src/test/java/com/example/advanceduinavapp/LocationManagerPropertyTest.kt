package com.example.advanceduinavapp

import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.checkAll
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Property-based tests for LocationManager location data validation.
 * Feature: location-tracker, Property 4: Location Data Validation
 * Validates: Requirements 6.3
 */
@RunWith(RobolectricTestRunner::class)
class LocationManagerPropertyTest {

    private lateinit var locationManager: LocationManager

    @Mock
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext()
        locationManager = LocationManager(context, mockFusedLocationClient)
    }

    @Test
    fun testValidLocationDataProperty() {
        // Property: For any valid location coordinates (latitude -90 to 90, longitude -180 to 180, positive accuracy),
        // the validation should return true
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.float(min = 0f, max = 1000f)
        ) { latitude, longitude, accuracy ->
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy
            }

            assertTrue(locationManager.validateLocation(location))
        }
    }

    @Test
    fun testInvalidLatitudeProperty() {
        // Property: For any latitude outside the range -90 to 90,
        // the validation should return false
        io.kotest.property.checkAll(
            Arb.double(min = -360.0, max = -91.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.float(min = 0f, max = 1000f)
        ) { latitude, longitude, accuracy ->
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy
            }

            assertFalse(locationManager.validateLocation(location))
        }

        io.kotest.property.checkAll(
            Arb.double(min = 91.0, max = 360.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.float(min = 0f, max = 1000f)
        ) { latitude, longitude, accuracy ->
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy
            }

            assertFalse(locationManager.validateLocation(location))
        }
    }

    @Test
    fun testInvalidLongitudeProperty() {
        // Property: For any longitude outside the range -180 to 180,
        // the validation should return false
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -360.0, max = -181.0),
            Arb.float(min = 0f, max = 1000f)
        ) { latitude, longitude, accuracy ->
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy
            }

            assertFalse(locationManager.validateLocation(location))
        }

        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = 181.0, max = 360.0),
            Arb.float(min = 0f, max = 1000f)
        ) { latitude, longitude, accuracy ->
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy
            }

            assertFalse(locationManager.validateLocation(location))
        }
    }

    @Test
    fun testNegativeAccuracyProperty() {
        // Property: For any negative accuracy value,
        // the validation should return false
        io.kotest.property.checkAll(
            Arb.double(min = -90.0, max = 90.0),
            Arb.double(min = -180.0, max = 180.0),
            Arb.float(min = -1000f, max = -0.1f)
        ) { latitude, longitude, accuracy ->
            val location = Location("test").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.accuracy = accuracy
            }

            assertFalse(locationManager.validateLocation(location))
        }
    }

    @Test
    fun testBoundaryLatitudeProperty() {
        // Property: Boundary values for latitude (-90 and 90) should be valid
        val location1 = Location("test").apply {
            latitude = -90.0
            longitude = 0.0
            accuracy = 10f
        }
        assertTrue(locationManager.validateLocation(location1))

        val location2 = Location("test").apply {
            latitude = 90.0
            longitude = 0.0
            accuracy = 10f
        }
        assertTrue(locationManager.validateLocation(location2))
    }

    @Test
    fun testBoundaryLongitudeProperty() {
        // Property: Boundary values for longitude (-180 and 180) should be valid
        val location1 = Location("test").apply {
            latitude = 0.0
            longitude = -180.0
            accuracy = 10f
        }
        assertTrue(locationManager.validateLocation(location1))

        val location2 = Location("test").apply {
            latitude = 0.0
            longitude = 180.0
            accuracy = 10f
        }
        assertTrue(locationManager.validateLocation(location2))
    }

    @Test
    fun testZeroAccuracyProperty() {
        // Property: Zero accuracy should be valid (edge case)
        val location = Location("test").apply {
            latitude = 0.0
            longitude = 0.0
            accuracy = 0f
        }
        assertTrue(locationManager.validateLocation(location))
    }
}
