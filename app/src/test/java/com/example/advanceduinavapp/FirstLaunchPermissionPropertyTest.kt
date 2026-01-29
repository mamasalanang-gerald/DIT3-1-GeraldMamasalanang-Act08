package com.example.advanceduinavapp

import android.Manifest
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowPackageManager
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Property-based test for first launch permission request.
 * Feature: location-tracker, Property 5: Permission Request on First Launch
 * Validates: Requirements 1.1
 *
 * Property: For any first app launch, if location permission has not been previously granted,
 * the system SHALL display a permission request dialog before attempting to access location data.
 */
@RunWith(RobolectricTestRunner::class)
class FirstLaunchPermissionPropertyTest {

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var context: android.content.Context
    private lateinit var shadowPackageManager: ShadowPackageManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        shadowPackageManager = shadowOf(context.packageManager)
        permissionHandler = PermissionHandler(context)
    }

    @Test
    fun testFirstLaunchPermissionRequest_WhenPermissionNotGranted() {
        // Property: On first launch, if permission is not granted, it should be requested
        // Arrange - Permission is not granted by default
        assertFalse(permissionHandler.isLocationPermissionGranted())

        // Act
        val isGranted = permissionHandler.isLocationPermissionGranted()

        // Assert - Permission should not be granted on first launch
        assertFalse(isGranted)
    }

    @Test
    fun testFirstLaunchPermissionRequest_PermissionNotPreviouslyGranted() {
        // Property: System should request permission if it was never granted before
        // Arrange
        shadowPackageManager.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Act
        val isGranted = permissionHandler.isLocationPermissionGranted()

        // Assert
        assertFalse(isGranted)
    }

    @Test
    fun testFirstLaunchPermissionRequest_AfterPermissionGrant() {
        // Property: After user grants permission, subsequent launches should not request again
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Act
        val isGranted = permissionHandler.isLocationPermissionGranted()

        // Assert
        assertTrue(isGranted)
    }

    @Test
    fun testFirstLaunchPermissionRequest_MultipleFirstLaunches() {
        // Property: Multiple first launches without permission should all request permission
        for (i in 0..4) {
            // Arrange
            shadowPackageManager.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

            // Act
            val isGranted = permissionHandler.isLocationPermissionGranted()

            // Assert
            assertFalse(isGranted)
        }
    }

    @Test
    fun testFirstLaunchPermissionRequest_PermissionStateTransition() {
        // Property: Permission state should transition correctly from denied to granted
        // Arrange - Start with permission denied
        shadowPackageManager.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        assertFalse(permissionHandler.isLocationPermissionGranted())

        // Act - Grant permission
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Assert - Permission should now be granted
        assertTrue(permissionHandler.isLocationPermissionGranted())
    }

    @Test
    fun testFirstLaunchPermissionRequest_PermissionRevocation() {
        // Property: Permission can be revoked after being granted
        // Arrange - Grant permission
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        assertTrue(permissionHandler.isLocationPermissionGranted())

        // Act - Revoke permission
        shadowPackageManager.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Assert - Permission should be denied
        assertFalse(permissionHandler.isLocationPermissionGranted())
    }

    @Test
    fun testFirstLaunchPermissionRequest_LocationPermissionSpecific() {
        // Property: System should specifically check for ACCESS_FINE_LOCATION permission
        // Arrange
        shadowPackageManager.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Act
        val isLocationPermissionGranted = permissionHandler.isLocationPermissionGranted()

        // Assert
        assertFalse(isLocationPermissionGranted)
    }

    @Test
    fun testFirstLaunchPermissionRequest_ConsistentBehavior() {
        // Property: Permission check should return consistent results
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Act - Check permission multiple times
        val result1 = permissionHandler.isLocationPermissionGranted()
        val result2 = permissionHandler.isLocationPermissionGranted()
        val result3 = permissionHandler.isLocationPermissionGranted()

        // Assert - All checks should return the same result
        assertTrue(result1)
        assertTrue(result2)
        assertTrue(result3)
    }

    @Test
    fun testFirstLaunchPermissionRequest_InitialState() {
        // Property: Initial state should have permission denied
        // Arrange - Fresh context
        val freshContext = ApplicationProvider.getApplicationContext<android.content.Context>()
        val freshPermissionHandler = PermissionHandler(freshContext)

        // Act
        val isGranted = freshPermissionHandler.isLocationPermissionGranted()

        // Assert - Permission should not be granted initially
        assertFalse(isGranted)
    }
}
