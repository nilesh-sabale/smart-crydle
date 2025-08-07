# smart-crydle for Baby
📱 Android Application – Description
The Android application is a critical component of the IoT-Driven Infant Care System, designed to provide real-time monitoring and alerts to parents or caregivers based on the data collected by sensors connected to the ESP32 microcontroller.

# 🔑 Key Features:
#Real-Time Alerts
The app sends push notifications when:
The baby cries (with reason: hunger, discomfort, etc.)
Temperature or humidity exceeds safe thresholds
Unusual motion is detected near or in the cradle

#Live Environmental Monitoring
Displays temperature and humidity values continuously.
Ensures the baby's environment is safe and comfortable.

#Cry Reason Classification Display
Shows specific reasons behind a baby’s cry using ML model results (like hunger, pain).
Data is classified via CNN model and delivered to the app through Firebase.

#Firebase Integration
Uses Firebase Realtime Database for:
Storing sensor readings (cry detection, movement, environment)

#Syncing data between the cradle system and app
Triggering cloud-based alerts and real-time data streaming

#User Interface
Simple UI built using Android Studio

Presents recent activity logs, such as:
“Baby Cry Detected: Hungry”
“Object Detected!”
"Temperature too high!"

Parents can visually track to baby's needs remotely

