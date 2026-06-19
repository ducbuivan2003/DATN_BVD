#include  "wifi_and_firebase.h"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

String did ="/" + String(RoomID) + "/" + String(doorID); // Đường dẫn đến thiết bị

// Cấu hình NTP
const char* ntp_server = "pool.ntp.org";  
const long  gmt_offset_sec = 7 * 3600; // GMT+7 (Việt Nam)
const int   daylight_offset_sec = 0;   

void setup_wifi_firebase(){
    // Kết nối WiFi
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Đang kết nối WiFi...");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }
    Serial.println("\nKết nối wiFi thành công! Địa chỉ IP: " + WiFi.localIP().toString());

    // Đồng bộ thời gian qua NTP
    configTime(gmt_offset_sec, daylight_offset_sec, ntp_server);
    Serial.println("Đang đồng bộ thời gian...");
    delay(2000); // Chờ lấy dữ liệu thời gian từ NTP

    // Cấu hình Firebase
    config.host = FIREBASE_HOST;
    config.signer.tokens.legacy_token = FIREBASE_AUTH;
    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);
}

String generate_key() {
    unsigned long timestamp = millis();  // Thời gian tính từ lúc ESP32 khởi động
    int random_num = random(1000, 9999);  // Số ngẫu nhiên để tránh trùng key
    return String(timestamp) + "_" + String(random_num);
}


String get_current_time() {
    struct tm timeinfo;
    if (!getLocalTime(&timeinfo)) {
        Serial.println("❌ Lỗi lấy thời gian!");
        return "00.00.00.01.01.1970"; 
    }

    char time_str[20];
    sprintf(time_str, "%02d.%02d.%02d.%02d.%02d.%04d",
            timeinfo.tm_hour, timeinfo.tm_min, timeinfo.tm_sec, 
            timeinfo.tm_mday, timeinfo.tm_mon + 1, timeinfo.tm_year + 1900);

    return String(time_str);
}
    
// ✅ Đọc trạng thái thiết bị từ Firebase
String download_data(String path) {
    path = String(USER_ID) + path;  // Gán USER_ID vào đường dẫn
    if (Firebase.RTDB.getString(&fbdo, path)) {
        String status = fbdo.stringData();
        // Serial.println("Trạng thái thiết bị: " + status);
        return status;
    }
    return "error";
}

// ✅ Upload dữ liệu lên Firebase
bool upload_data(String path, String data) {// path : /a/b
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
void push_notification(String text, String deviceId) {
    String current_time = get_current_time();  // Lấy thời gian thực
    String idtime = deviceId + "." + current_time;

    // Tạo push key thủ công
    String push_key = generate_key(); 

    // Đường dẫn Firebase với USER_ID
    String path_text =  "/main/noti/" + push_key + "/text";
    String path_time =  "/main/noti/" + push_key + "/time";
    String path_newestId = "/main/newestNoti";

    // Upload dữ liệu
    bool text_uploaded = upload_data(path_text, text);
    bool time_uploaded = upload_data(path_time, idtime);
    bool newestId = upload_data(path_newestId, idtime);

    if (text_uploaded && time_uploaded && newestId) {
        Serial.println("push thong bao thanh cong: " + push_key);
    } else {
        Serial.println("Loi push thong bao!");
    }
}