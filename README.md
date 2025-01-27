Purpose and Functionality of the Project
Sound Limitations is an Android application that helps users control media volume by setting volume limits for specific apps.

This application can be useful in scenarios such as:

-Preventing children from listening to loud sounds in certain apps.
-Protecting hearing by creating volume limits for specific apps.
-Allowing users to better control the volume of their devices based on their needs.

The main features of the application are:
1. App Listing and Search:
Lists all user-installed apps on the device in alphabetical order.
Users can search for any app using a search bar.

2.Volume Limiting:
Users can set a maximum media volume level for each app.
This limit can be configured through a simple user interface.

3.Real-Time Volume Monitoring:
Monitors volume changes on the device in real-time using an accessibility service and ContentObserver.
If the set limit is exceeded, the volume is automatically reduced.

4.Accessibility Integration:
Uses system accessibility features to monitor and control volume effectively.



Project Architecture
This project is designed based on the MVC (Model-View-Controller) architecture, which is common in Android development.

1. Manifest and Permissions
The Android Manifest file includes necessary permissions and configurations:
The QUERY_ALL_PACKAGES permission is removed, limiting package visibility.
An AccessibilityService is defined to enable system-level volume control.

2. Activity and UI Layer
-MainActivity:
The main entry point of the app. It contains buttons to navigate to different features like listing apps, viewing apps, and accessing accessibility settings.

-ListeActivity:
Displays a list of all user-installed apps.
Allows the user to select an app to set a volume limit.

-VolumeLimitActivity:
Allows the user to define the maximum volume limit for a selected app.

-GoruntuleActivity:
Shows all user-installed apps in a paginated grid view using a ViewPager.


3. Adapter Layer
-AppAdapter:
A custom adapter for displaying app icons and names in a list.

-AppsPagerAdapter:
Used to display apps in a grid format across multiple pages.


4. Service and Monitoring Layer
-VolumeAccessibilityService:
Monitors app transitions and volume changes.
Ensures the defined volume limits are not exceeded.

-ContentObserver:
Detects system-level volume changes and triggers necessary actions.


5. Data Storage
-SharedPreferences:
Stores volume limit values for each app locally on the device.
Ensures fast and easy access to saved data.


6. Application Logic
-Volume Control:
Uses AudioManager to read and update the device’s media volume levels.
Automatically adjusts the volume if the user-defined limit is exceeded.

-App-Based Control:
Allows volume limits to be set based on the package name of each app.



