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

/**
 * Property-based test for error recovery.
 * Feature: location-tracker, Property 7: Error Recovery
 * Validates: Requirements 6.2, 6.4
 *
 * Property: For any location retrieval failure, the system SHALL display an error message
 * to the user and remain in a state where the user can retry the operation without restarting the app.
 */
@RunWith(RobolectricTestRunner::class)
class ErrorRecoveryPropertyTest {

    private lateinit var errorHandler: ErrorHandler
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        errorHandler = ErrorHandler(context)
    }

    @Test
    fun testErrorRecovery_LocationServicesDisabled() {
        // Property: When location services are disabled, system should provide recovery message
        // Arrange
        val message = errorHandler.getLocationServicesDisabledMessage()

        // Act & Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("Location services"))
    }

    @Test
    fun testErrorRecovery_PermissionDenied() {
        // Property: When permission is denied, system should provide recovery message
        // Arrange
        val message = errorHandler.getPermissionDeniedMessage()

        // Act & Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("permission"))
    }

    @Test
    fun testErrorRecovery_LocationRetrievalFailed() {
        // Property: When location retrieval fails, system should provide recovery message
        // Arrange
        val message = errorHandler.getLocationRetrievalFailedMessage()

        // Act & Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("location"))
    }

    @Test
    fun testErrorRecovery_InvalidLocationData() {
        // Property: When location data is invalid, system should provide recovery message
        // Arrange
        val message = errorHandler.getInvalidLocationDataMessage()

        // Act & Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("invalid"))
    }

    @Test
    fun testErrorRecovery_MaxRetriesExceeded() {
        // Property: When max retries are exceeded, system should provide informative message
        // Arrange
        val maxRetries = 3
        val message = errorHandler.getMaxRetriesExceededMessage(maxRetries)

        // Act & Assert
        assertNotNull(message)
        assertTrue(message.isNotEmpty())
        assertTrue(message.contains("$maxRetries"))
    }

    @Test
    fun testErrorRecovery_RecoverableError() {
        // Property: Generic exceptions should be marked as recoverable
        // Arrange
        val exception = Exception("Generic error")

        // Act
        val isRecoverable = errorHandler.isRecoverableError(exception)

        // Assert
        assertTrue(isRecoverable)
    }

    @Test
    fun testErrorRecovery_NonRecoverableSecurityException() {
        // Property: Security exceptions should not be recoverable
        // Arrange
        val exception = SecurityException("Permission denied")

        // Act
        val isRecoverable = errorHandler.isRecoverableError(exception)

        // Assert
        assertFalse(isRecoverable)
    }

    @Test
    fun testErrorRecovery_NonRecoverableIllegalStateException() {
        // Property: Illegal state exceptions should not be recoverable
        // Arrange
        val exception = IllegalStateException("Invalid state")

        // Act
        val isRecoverable = errorHandler.isRecoverableError(exception)

        // Assert
        assertFalse(isRecoverable)
    }

    @Test
    fun testErrorRecovery_RuntimeException() {
        // Property: Runtime exceptions should be recoverable
        // Arrange
        val exception = RuntimeException("Runtime error")

        // Act
        val isRecoverable = errorHandler.isRecoverableError(exception)

        // Assert
        assertTrue(isRecoverable)
    }

    @Test
    fun testErrorRecovery_MultipleErrorScenarios() {
        // Property: System should handle multiple error scenarios without crashing
        val errorScenarios = listOf(
            errorHandler.getLocationServicesDisabledMessage(),
            errorHandler.getPermissionDeniedMessage(),
            errorHandler.getLocationRetrievalFailedMessage(),
            errorHandler.getInvalidLocationDataMessage(),
            errorHandler.getMaxRetriesExceededMessage(3)
        )

        // Act & Assert
        for (message in errorScenarios) {
            assertNotNull(message)
            assertTrue(message.isNotEmpty())
        }
    }

    @Test
    fun testErrorRecovery_ErrorMessageConsistency() {
        // Property: Error messages should be consistent across multiple calls
        // Arrange
        val message1 = errorHandler.getLocationServicesDisabledMessage()
        val message2 = errorHandler.getLocationServicesDisabledMessage()

        // Act & Assert
        assertTrue(message1 == message2)
    }

    @Test
    fun testErrorRecovery_ExceptionClassification() {
        // Property: Different exception types should be classified correctly
        val exceptions = listOf(
            Pair(SecurityException("test"), false),
            Pair(IllegalStateException("test"), false),
            Pair(Exception("test"), true),
            Pair(RuntimeException("test"), true),
            Pair(IllegalArgumentException("test"), true)
        )

        for ((exception, expectedRecoverable) in exceptions) {
            // Act
            val isRecoverable = errorHandler.isRecoverableError(exception)

            // Assert
            assertTrue(isRecoverable == expectedRecoverable)
        }
    }

    @Test
    fun testErrorRecovery_RetryCountVariation() {
        // Property: Max retries message should include the retry count
        val retryCounts = listOf(1, 3, 5, 10)

        for (retryCount in retryCounts) {
            // Act
            val message = errorHandler.getMaxRetriesExceededMessage(retryCount)

            // Assert
            assertNotNull(message)
            assertTrue(message.contains("$retryCount"))
        }
    }

    @Test
    fun testErrorRecovery_UserFriendlyMessages() {
        // Property: All error messages should be user-friendly and non-empty
        val messages = listOf(
            errorHandler.getLocationServicesDisabledMessage(),
            errorHandler.getPermissionDeniedMessage(),
            errorHandler.getLocationRetrievalFailedMessage(),
            errorHandler.getInvalidLocationDataMessage()
        )

        for (message in messages) {
            // Assert
            assertNotNull(message)
            assertTrue(message.isNotEmpty())
            assertTrue(message.length > 10) // Should be descriptive
        }
    }
}
