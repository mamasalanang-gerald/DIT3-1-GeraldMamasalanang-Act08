package com.example.advanceduinavapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowPackageManager
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class PermissionHandlerTest {

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var context: Context
    private lateinit var shadowPackageManager: ShadowPackageManager

    @Mock
    private lateinit var mockLauncher: ActivityResultLauncher<String>

    @Mock
    private lateinit var mockMultiLauncher: ActivityResultLauncher<Array<String>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()
        permissionHandler = PermissionHandler(context)
        shadowPackageManager = shadowOf(context.packageManager)
    }

    @Test
    fun testIsPermissionGranted_WhenPermissionGranted_ReturnsTrue() {
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Act
        val result = permissionHandler.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        // Assert
        assertTrue(result)
    }

    @Test
    fun testIsPermissionGranted_WhenPermissionDenied_ReturnsFalse() {
        // Arrange
        // Permission is not granted by default

        // Act
        val result = permissionHandler.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        // Assert
        assertFalse(result)
    }

    @Test
    fun testIsLocationPermissionGranted_WhenLocationPermissionGranted_ReturnsTrue() {
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        // Act
        val result = permissionHandler.isLocationPermissionGranted()

        // Assert
        assertTrue(result)
    }

    @Test
    fun testIsLocationPermissionGranted_WhenLocationPermissionDenied_ReturnsFalse() {
        // Arrange
        // Permission is not granted by default

        // Act
        val result = permissionHandler.isLocationPermissionGranted()

        // Assert
        assertFalse(result)
    }

    @Test
    fun testRequestPermission_CallsLauncherWithCorrectPermission() {
        // Arrange
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        // Act
        permissionHandler.requestPermission(mockLauncher, permission)

        // Assert
        verify(mockLauncher).launch(permission)
    }

    @Test
    fun testRequestLocationPermission_CallsLauncherWithLocationPermission() {
        // Arrange
        // Act
        permissionHandler.requestLocationPermission(mockLauncher)

        // Assert
        verify(mockLauncher).launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @Test
    fun testRequestPermissions_CallsLauncherWithCorrectPermissions() {
        // Arrange
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Act
        permissionHandler.requestPermissions(mockMultiLauncher, permissions)

        // Assert
        verify(mockMultiLauncher).launch(permissions)
    }
}
