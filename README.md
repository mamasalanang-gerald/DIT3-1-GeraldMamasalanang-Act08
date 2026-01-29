# Location Tracker App

**Name:** Gerald Mamasalanang  
**Section:** DIT 3-1  
**Activity Title:** Device Features & Sensors in Mobile Applications  
**Lab Focus:** Simple Location Tracker App using GPS and Google Maps

## Project Description

This project is an Android app called **Location Tracker** that demonstrates GPS location services, runtime permissions, and Google Maps integration. The app displays the user's real-time location on a map with continuous updates as the device moves.

The main features include:

- **Runtime Location Permissions** - Requests ACCESS_FINE_LOCATION with proper handling
- **GPS Location Tracking** - Real-time location updates using FusedLocationProviderClient
- **Google Maps Integration** - Displays map with location marker
- **Location Data Validation** - Validates latitude, longitude, and accuracy values
- **Error Handling** - Graceful handling of permission denial and location service failures
- **UI Feedback** - Displays coordinates, accuracy, and timestamp of last update
- **Continuous Operation** - Maintains responsiveness during rapid location updates

## Project Architecture

```
app/src/main/java/com/example/advanceduinavapp/
├── MainActivity.kt                    # Main activity with map and location tracking
├── PermissionHandler.kt               # Runtime permission management
├── LocationManager.kt                 # Location services and validation
├── MapManager.kt                      # Google Maps operations
└── ErrorHandler.kt                    # Error messages and recovery

app/src/test/java/com/example/advanceduinavapp/
├── PermissionHandlerTest.kt           # Unit tests for permissions
├── LocationManagerPropertyTest.kt     # Property tests for location validation
├── MapManagerTest.kt                  # Unit tests for map operations
├── MainActivityTest.kt                # Unit tests for activity lifecycle
├── ErrorHandlerTest.kt                # Unit tests for error handling
├── UIFeedbackTest.kt                  # Unit tests for UI display
├── PermissionPrerequisitePropertyTest.kt      # Property 1: Permission prerequisite
├── LocationMarkerConsistencyPropertyTest.kt   # Property 2: Marker consistency
├── MapCenteringPropertyTest.kt                # Property 3: Map centering
├── ContinuousOperationPropertyTest.kt         # Property 6: Continuous operation
├── FirstLaunchPermissionPropertyTest.kt       # Property 5: First launch permission
└── ErrorRecoveryPropertyTest.kt               # Property 7: Error recovery

app/src/main/res/
├── layout/
│   └── activity_main.xml              # Main activity layout with map and info panel
└── AndroidManifest.xml                # Permissions and Google Maps API key
```

## Technologies Used

- **Kotlin** - Primary programming language
- **Google Play Services** - FusedLocationProviderClient for location updates
- **Google Maps SDK** - Map display and marker management
- **Android Location Services** - GPS and network-based location providers
- **ActivityResultContracts** - Modern runtime permission handling
- **Robolectric** - Unit testing framework for Android
- **Mockito** - Mocking framework for testing
- **Kotest** - Property-based testing library
- **ConstraintLayout** - Flexible layout positioning
- **Material Design** - Modern UI components

## Setup Instructions

1. Clone this repository:
   ```bash
   git clone git@github.com:mamasalanang-gerald/DIT3-1-GeraldMamasalanang-Act08.git
   ```

2. Open the project in Android Studio

3. Add Google Maps API Key:
   - Get an API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Replace `YOUR_GOOGLE_MAPS_API_KEY` in `AndroidManifest.xml` with your actual key

4. Build the project (Build → Make Project)

5. Run on device or emulator (requires location services enabled)

## How to Use

1. **Launch App** - App requests location permission on first launch
2. **Grant Permission** - Allow ACCESS_FINE_LOCATION permission when prompted
3. **View Map** - Map displays with your current location marked
4. **Real-Time Updates** - Marker updates as you move (5-second intervals)
5. **View Details** - See coordinates, accuracy, and last update timestamp
6. **Handle Errors** - If location services are disabled, tap "Retry" to enable them

## Permissions Used

- `android.permission.ACCESS_FINE_LOCATION` - Precise GPS location
- `android.permission.ACCESS_COARSE_LOCATION` - Approximate location (fallback)
- `android.permission.INTERNET` - Map data and location services
- `android.permission.ACCESS_NETWORK_STATE` - Network availability check

## Implementation Details

### Core Components

**PermissionHandler**
- Checks if location permission is granted
- Requests runtime permissions using ActivityResultContracts
- Handles permission rationale display

**LocationManager**
- Manages FusedLocationProviderClient for location updates
- Validates location data (latitude -90 to 90, longitude -180 to 180, positive accuracy)
- Provides callbacks for location updates
- Handles permission checks before accessing location

**MapManager**
- Initializes Google Maps
- Adds and updates location markers
- Centers map on current location
- Manages marker lifecycle

**ErrorHandler**
- Provides user-friendly error messages
- Classifies errors as recoverable or non-recoverable
- Handles location services disabled, permission denied, and retrieval failures

**MainActivity**
- Coordinates between location services and map display
- Manages activity lifecycle (onCreate, onResume, onPause)
- Handles permission results
- Updates UI with location information
- Implements retry mechanism for failed location requests

### Location Data Validation

The app validates all location data before displaying:
- Latitude must be between -90 and 90 degrees
- Longitude must be between -180 and 180 degrees
- Accuracy must be a positive value

Invalid locations are silently skipped and the app waits for the next valid update.

### Error Handling Strategy

1. **Permission Denied** - Shows message, allows user to retry
2. **Location Services Disabled** - Shows message, allows user to enable in settings
3. **Location Retrieval Failed** - Automatically retries after 5 seconds
4. **Invalid Location Data** - Skips invalid data, continues waiting for valid updates
5. **Max Retries Exceeded** - Shows message with option to retry manually

## Testing Strategy

### Unit Tests
- **PermissionHandlerTest** - Tests permission checking and requesting
- **LocationManagerPropertyTest** - Tests location validation with boundary values
- **MapManagerTest** - Tests marker positioning and map operations
- **MainActivityTest** - Tests activity lifecycle and initialization
- **ErrorHandlerTest** - Tests error message generation and exception classification
- **UIFeedbackTest** - Tests coordinate, accuracy, and timestamp display formatting

### Property-Based Tests (Correctness Properties)

**Property 1: Permission Prerequisite for Location Access**
- For any location update request, if permission is not granted, location access is blocked
- Validates: Requirements 1.1, 1.2

**Property 2: Location Marker Consistency**
- For any valid location data, the marker is positioned at exact coordinates
- Validates: Requirements 3.1, 3.2

**Property 3: Map Centering on Update**
- For any location update, the map centers on the new marker position
- Validates: Requirements 4.2, 4.3

**Property 4: Location Data Validation**
- For any location data, validation correctly identifies valid/invalid coordinates
- Validates: Requirements 6.3

**Property 5: Permission Request on First Launch**
- For any first launch, permission request is shown if not previously granted
- Validates: Requirements 1.1

**Property 6: Continuous Operation After Location Update**
- For any sequence of location updates, system remains responsive without crashing
- Validates: Requirements 4.4

**Property 7: Error Recovery**
- For any location retrieval failure, system displays error and allows retry
- Validates: Requirements 6.2, 6.4

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests PermissionHandlerTest

# Run with coverage report
./gradlew test --coverage
```
## Key Learnings

### 1. GPS and Location Services
I learned how GPS provides location data through satellite signals and how Android abstracts this through LocationManager and FusedLocationProviderClient. The FusedLocationProviderClient is preferred because it intelligently combines GPS, Wi-Fi, and cellular data for better accuracy and battery efficiency.

### 2. Runtime Permissions
Modern Android requires explicit runtime permission requests for sensitive data like location. I implemented this using ActivityResultContracts, which provides a cleaner API than the older requestPermissions() callback approach. Proper permission handling is critical for user privacy and app stability.

### 3. Location Data Validation
Not all location data from the system is valid. I implemented comprehensive validation to ensure latitude is within -90 to 90, longitude is within -180 to 180, and accuracy is positive. This prevents displaying incorrect markers on the map.

### 4. Property-Based Testing
Property-based testing with Kotest allowed me to verify correctness properties across hundreds of randomly generated inputs. This is more powerful than traditional unit tests because it catches edge cases and boundary conditions automatically.

### 5. Error Handling and Recovery
Graceful error handling is essential for location-based apps. I implemented strategies for permission denial, disabled location services, and retrieval failures. The app remains responsive and allows users to retry without restarting.

## Screenshots

Screenshots should be placed in the `/screenshots/` folder:
- `permission_request.png` - Permission request dialog
- `map_location.png` - Map with current location marker
- `location_update.png` - Location updates with coordinates and accuracy

## How to Run

```bash
# Clone the repository
git clone git@github.com:mamasalanang-gerald/DIT3-1-GeraldMamasalanang-Act08.git

# Open in Android Studio
# Add Google Maps API key to AndroidManifest.xml
# Build → Make Project
# Run on emulator or device with location services enabled
```

## Minimum Requirements

- Android API Level 24 (Android 7.0)
- Google Play Services
- Location services enabled on device
- Google Maps API key

## Implementation Completion

All 12 implementation tasks have been completed:

1. ✅ Set up project dependencies and manifest configuration
2. ✅ Create PermissionHandler class for runtime permission management
3. ✅ Write unit tests for PermissionHandler
4. ✅ Create LocationManager class for location services
5. ✅ Write property test for location data validation
6. ✅ Create MapManager class for map operations
7. ✅ Write unit tests for MapManager
8. ✅ Create MainActivity with map fragment and lifecycle management
9. ✅ Write unit tests for MainActivity lifecycle
10. ✅ Implement error handling and user feedback
11. ✅ Write unit tests for error handling
12. ✅ Implement location update UI feedback
13. ✅ Write unit tests for UI feedback
14. ✅ Checkpoint - Verify core functionality
15. ✅ Write property test for permission prerequisite
16. ✅ Write property test for location marker consistency
17. ✅ Write property test for map centering on update
18. ✅ Implement continuous location tracking
19. ✅ Write property test for continuous operation
20. ✅ Implement permission request on first launch
21. ✅ Write property test for first launch permission request
22. ✅ Implement error recovery and retry mechanism
23. ✅ Write property test for error recovery
24. ✅ Final checkpoint - Ensure all tests pass and app is stable

**Build Status:** ✅ BUILD SUCCESSFUL
