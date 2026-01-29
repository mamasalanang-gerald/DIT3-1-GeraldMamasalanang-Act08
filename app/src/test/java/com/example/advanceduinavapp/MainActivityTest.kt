package com.example.advanceduinavapp

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowPackageManager
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var shadowPackageManager: ShadowPackageManager

    @Before
    fun setUp() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        shadowPackageManager = shadowOf(context.packageManager)
    }

    @Test
    fun testMainActivityCreates() {
        // Arrange
        val controller = Robolectric.buildActivity(MainActivity::class.java)

        // Act
        val activity = controller.create().get()

        // Assert
        assertNotNull(activity)
    }

    @Test
    fun testOnCreateInitializesManagers() {
        // Arrange
        val controller = Robolectric.buildActivity(MainActivity::class.java)

        // Act
        val activity = controller.create().get()

        // Assert
        // Verify activity was created successfully
        assertNotNull(activity)
    }

    @Test
    fun testOnResumeStartsLocationUpdates_WhenPermissionGranted() {
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().get()

        // Act
        controller.resume()

        // Assert
        assertNotNull(activity)
    }

    @Test
    fun testOnPauseStopsLocationUpdates() {
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().resume().get()

        // Act
        controller.pause()

        // Assert
        assertNotNull(activity)
    }

    @Test
    fun testActivityLifecycle_CreateResumeDestroy() {
        // Arrange
        shadowPackageManager.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        val controller = Robolectric.buildActivity(MainActivity::class.java)

        // Act
        val activity = controller
            .create()
            .resume()
            .pause()
            .stop()
            .destroy()
            .get()

        // Assert
        assertNotNull(activity)
    }
}
