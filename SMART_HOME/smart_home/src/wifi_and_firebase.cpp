#include"wifi_and_firebase.h"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
const char* ntpServer = "pool.ntp.org";

void setup_wifi_firebase(){
    // Kết nối WiFi
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Đang kết nối WiFi...");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }
    Serial.println("\n✅ Đã kết nối WiFi!");

    // Đồng bộ thời gian qua NTP
    configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
    Serial.println("⏳ Đang đồng bộ thời gian...");
    delay(2000); // Chờ lấy dữ liệu thời gian từ NTP

    // Cấu hình Firebase
    config.host = FIREBASE_HOST;
    config.signer.tokens.legacy_token = FIREBASE_AUTH;
    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);
}

String generateKey() {
    unsigned long timestamp = millis();  // Thời gian tính từ lúc ESP32 khởi động
    int randomNum = random(1000, 9999);  // Số ngẫu nhiên để tránh trùng key
    return String(timestamp) + "_" + String(randomNum);
}

// ✅ Lấy thời gian từ NTP
String getCurrentTime() {
    struct tm timeinfo;
    if (!getLocalTime(&timeinfo)) {
        Serial.println("❌ Lỗi lấy thời gian!");
        return "00.00.00.01.01.1970"; 
    }

    char timeStr[20];
    sprintf(timeStr, "%02d.%02d.%02d.%02d.%02d.%04d",
            timeinfo.tm_hour, timeinfo.tm_min, timeinfo.tm_sec, 
            timeinfo.tm_mday, timeinfo.tm_mon + 1, timeinfo.tm_year + 1900);

    return String(timeStr);
}
    
// ✅ Đọc trạng thái thiết bị từ Firebase
String download_data(String path) {
    path = String(USER_ID) + path;  // Gán USER_ID vào đường dẫn
    if (Firebase.RTDB.getString(&fbdo, path)) {
        String status = fbdo.stringData();
        Serial.println("Trạng thái thiết bị: " + status);
        return status;
    }
    return "error";
}

// ✅ Upload dữ liệu lên Firebase
bool uploadData(String path, String data) {// path : /a/b
    path = String(USER_ID) + path;  // Gán USER_ID vào đường dẫn
    if (Firebase.RTDB.setString(&fbdo, path, data)) {
        Serial.println("Cập nhật thành công!");
        return true;
    } else {
        Serial.println("Lỗi cập nhật: " + fbdo.errorReason());
    }
    return false;
}

// ✅ Gửi thông báo lên Firebase
void pushNotification(String text, String deviceId) {
    String currentTime = getCurrentTime();  // Lấy thời gian thực
    String idtime = deviceId + "." + currentTime;

    // Tạo push key thủ công
    String pushKey = generateKey(); 

    // Đường dẫn Firebase với USER_ID
    String pathText =  "/main/noti/" + pushKey + "/text";
    String pathTime =  "/main/noti/" + pushKey + "/time";
    String pathNewestId = "/main/newestNoti";

    // Upload dữ liệu
    bool textUploaded = uploadData(pathText, text);
    bool timeUploaded = uploadData(pathTime, idtime);
    bool newestId = uploadData(pathNewestId, idtime);

    if (textUploaded && timeUploaded && newestId) {
        Serial.println("✅ Push Notification Sent: " + pushKey);
    } else {
        Serial.println("❌ Error sending push notification");
    }
}


