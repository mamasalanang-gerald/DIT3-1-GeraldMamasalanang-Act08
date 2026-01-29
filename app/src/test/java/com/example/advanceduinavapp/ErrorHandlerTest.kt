package com.example.advanceduinavapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ErrorHandlerTest {

    private lateinit var errorHandler: ErrorHandler
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        errorHandler = ErrorHandler(context)
    }

    @Test
    fun testGetLocationServicesDisabledMessage_ReturnsNonEmptyString() {
        // Act
        val message = errorHandler.getLocationServicesDisabledMessage()

        // Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("Location services"))
    }

    @Test
    fun testGetPermissionDeniedMessage_ReturnsNonEmptyString() {
        // Act
        val message = errorHandler.getPermissionDeniedMessage()

        // Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("permission"))
    }

    @Test
    fun testGetLocationRetrievalFailedMessage_ReturnsNonEmptyString() {
        // Act
        val message = errorHandler.getLocationRetrievalFailedMessage()

        // Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("location"))
    }

    @Test
    fun testGetMaxRetriesExceededMessage_IncludesRetryCount() {
        // Arrange
        val maxRetries = 3

        // Act
        val message = errorHandler.getMaxRetriesExceededMessage(maxRetries)

        // Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("$maxRetries"))
    }

    @Test
    fun testGetInvalidLocationDataMessage_ReturnsNonEmptyString() {
        // Act
        val message = errorHandler.getInvalidLocationDataMessage()

        // Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("invalid"))
    }

    @Test
    fun testIsRecoverableError_WithSecurityException_ReturnsFalse() {
        // Arrange
        val exception = SecurityException("Permission denied")

        // Act
        val result = errorHandler.isRecoverableError(exception)

        // Assert
        assertFalse(result)
    }

    @Test
    fun testIsRecoverableError_WithIllegalStateException_ReturnsFalse() {
        // Arrange
        val exception = IllegalStateException("Invalid state")

        // Act
        val result = errorHandler.isRecoverableError(exception)

        // Assert
        assertFalse(result)
    }

    @Test
    fun testIsRecoverableError_WithGenericException_ReturnsTrue() {
        // Arrange
        val exception = Exception("Generic error")

        // Act
        val result = errorHandler.isRecoverableError(exception)

        // Assert
        assertTrue(result)
    }

    @Test
    fun testIsRecoverableError_WithRuntimeException_ReturnsTrue() {
        // Arrange
        val exception = RuntimeException("Runtime error")

        // Act
        val result = errorHandler.isRecoverableError(exception)

        // Assert
        assertTrue(result)
    }
}
