#ifndef __WIFI_AND_FIREBASE_H__
#define __WIFI_AND_FIREBASE_H__
#include <Arduino.h>
#include <WiFi.h>
#include "time.h"  // Thư viện để lấy thời gian từ Internet
#include <Firebase_ESP_Client.h>
#define USER_ID "j8gAdLAk0kcH0zaQyLII3eE53yJ2"

#define DEVICE_ID "esp32w2"
// Thông tin WiFi của bạn
#define WIFI_SSID "Tung Ngan 1"
#define WIFI_PASSWORD "0974795395"


#define FIREBASE_HOST "https://datn-8f7b7-default-rtdb.asia-southeast1.firebasedatabase.app/" //cap nhat
#define FIREBASE_AUTH "ottXcSDG77KXC4KShsKUtwYt77iGdpldBvOAc2FR"

extern FirebaseData fbdo;
extern FirebaseAuth auth;
extern FirebaseConfig config;

#define DEVICE_ID "esp32w2"

// Cấu hình NTP
extern const char* ntp_server;
const long  gmt_offset_sec = 7 * 3600; // GMT+7 (Việt Nam)
const int   daylight_offset_sec = 0;   

void setup_wifi_firebase();
String generate_key();   
String get_current_time();
String download_data(String path);
bool upload_data(String path, String data);
void push_notification(String text, String deviceId);

#endif