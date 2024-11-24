# Guardian Angel - Personal Safety App  

Guardian Angel is an Android app designed to prioritize user safety by integrating essential emergency features. With live location sharing, SOS alerts, AI assistance, and more, it ensures help is always within reach.  

---

## Features  

### Core Features  
- **Emergency SOS Alerts**: Instantly call emergency services and notify emergency contacts.  
- **Live Location Sharing**: Send real-time GPS location via SMS to pre-defined contacts.  
- **Activity Timer**: Set a timer for activities and receive safety notifications upon completion.  
- **AI-Powered Assistance**: Chat with the Gemini AI chatbot for safety tips and emergency guidance.  
- **Sound Alerts**: Activate loud or ultrasonic sounds for emergencies.  
- **Crisis Notifications**: Stay updated with alerts for natural disasters or public emergencies.  
- **Emergency Contact Management**: Securely store and manage emergency contacts.  
- **User Profile Management**: Add medical details and preferences for faster emergency responses.  

---

## Technologies Used  

### Frontend  
- **Jetpack Compose**: For modern and responsive UI design.  

### Backend  
- **Room Database**: For storing emergency contacts, medical details, and location history.  
- **Kotlin Coroutines**: To handle background operations efficiently without blocking the main thread.  

### APIs and Services  
- **Google Gemini AI API**: For intelligent chatbot functionality.  
- **Android LocationManager API**: For GPS-based location tracking.  
- **SmsManager**: For sending SMS messages to emergency contacts.  
- **NotificationCompat**: For generating safety and crisis notifications.  

---

## Setup Instructions  

1. Clone the repository:  
   ```bash
   git clone https://github.com/yourusername/guardian-angel.git
   cd guardian-angel
   ```

2. Open the project in **Android Studio**.  

3. Configure your API keys:  
   - Add your Gemini AI API key in `GeminiViewModel.kt`:  
     ```kotlin
     private val generativeModel = GenerativeModel(
         modelName = "gemini-pro",
         apiKey = "YOUR_API_KEY_HERE"
     )
     ```  

4. Build and run the app on a physical device or an emulator.  

5. Grant necessary permissions such as `ACCESS_FINE_LOCATION`, `CALL_PHONE`, and `SEND_SMS` for full functionality.  

---

## Permissions Required  

- **ACCESS_FINE_LOCATION**: For real-time GPS location tracking.  
- **SEND_SMS**: To share location via SMS with emergency contacts.  
- **CALL_PHONE**: To make SOS calls directly from the app.  
- **POST_NOTIFICATIONS**: For activity and crisis alerts.  

---

## Troubleshooting  

- If the app cannot access GPS, ensure location permissions are enabled and the GPS provider is active.  
- For SMS functionality, verify that SMS permissions are granted and your device supports SMS.  
- Use a properly configured emulator or physical device for accurate testing of features like location tracking.  

---

## Contributing  

Contributions are welcome! To contribute:  
1. Fork the repository.  
2. Create a new branch:  
   ```bash
   git checkout -b feature/your-feature
   ```  
3. Commit your changes:  
   ```bash
   git commit -m "Add your feature"
   ```  
4. Push to the branch:  
   ```bash
   git push origin feature/your-feature
   ```  
5. Submit a pull request.  

---

## License  

This project is licensed under the [MIT License](LICENSE).  
