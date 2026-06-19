#ifndef RFID_H
#define RFID_H

#include <SPI.h>
#include <MFRC522.h>
#include <ESP32Servo.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include "wifi_and_firebase.h"


class RFID_Door {
      /*
  VCC    3.3V
  GND    GND
  SCK    18 
  MOSI   23 
  MISO   19 
  SDA/SS 5 
  RST    15 
  */
private:
    int SERVO;
    bool state = false; // Mặc định là đóng cửa
    MFRC522 rfid; // Đối tượng RFID
    String pw ="1234"; // Mật khẩu mặc định
    Servo my_servo;
    const byte valid_UID[2][4] = {
        {0xC3, 0x74, 0xCD, 0x28}, // Thẻ 1
        {0x33, 0xA2, 0x3F, 0x14}  // Thẻ 2
    };
    int valid_UID_count = 2; // Số lượng thẻ hợp lệ

public:
    RFID_Door(int SS,int RS, int SERVO) : rfid(SS, RS) { // Khởi tạo RFID với chân SS
        this->SERVO = SERVO;
    }

    void init() {
        SPI.begin(); // Khởi tạo SPI (ESP32 tự động xử lý các chân)
        rfid.PCD_Init(); // Khởi tạo RFID
        my_servo.attach(SERVO); // Gán chân điều khiển servo
        my_servo.write(0); // Đặt servo về vị trí đóng cửa
    }
    
    bool get_state() {
        return state;
    }

    void set_state(bool new_state) {
        state = new_state;
    }

    void turn_servo(int angle) {
        my_servo.write(angle);
    }

    void open_door() {
        Serial.println("✅ Mở cửa...");
        turn_servo(0); // Mở cửa
        state = true;
    }

    void close_door() {
        Serial.println("🔒 Đóng cửa...");
        turn_servo(112); // Đóng cửa
        state = false;
    }

    bool is_valid_UID() {
        // Kiểm tra nếu UID hợp lệ
        for (int i = 0; i < valid_UID_count; i++) {
            if (memcmp(rfid.uid.uidByte, valid_UID[i], 4) == 0) {
                return true;
            }
        }
        return false;
    }

    void print_UID() {
        Serial.print("UID: ");
        for (byte i = 0; i < rfid.uid.size; i++) {
            Serial.print(rfid.uid.uidByte[i], HEX);
            Serial.print(" ");
        }
        Serial.println();
    }

    bool check_card() {
        bool rt = false;  // Declare rt variable
        //Serial.println("⏳ Đang chờ thẻ...");
        if (!rfid.PICC_IsNewCardPresent()) return rt;  // Kiểm tra thẻ mới
        if (!rfid.PICC_ReadCardSerial()) return rt;   // Đọc serial của thẻ

        //print_UID(); // In UID ra Serial Monitor
        Serial.print("checking...");
        if (is_valid_UID()) {
            Serial.println("✅ Thẻ hợp lệ");
            rt = true;
        } else {
            Serial.println("⛔ Thẻ không hợp lệ!");
        }

        rfid.PICC_HaltA();  // Dừng giao tiếp với thẻ
        rfid.PCD_StopCrypto1(); // Dừng mã hóa dữ liệu
        return rt;
    }
    bool check_password(String input) {
        if (input == pw) {
            Serial.println("✅ Mật khẩu hợp lệ");
            return true;
        } else {
            Serial.println("⛔ Mật khẩu không hợp lệ!");
            return false;
        }
    }
    void update_password(String new_password) {
        if(new_password != pw) {
            pw = new_password; // Cập nhật mật khẩu mới
            Serial.println("✅ Mật khẩu đã được cập nhật: " + pw);
        }
    }
    void update_to_FB(){
        String path = did + "/status"; // Đường dẫn đến trạng thái cửa
        if (state) {
            upload_data(path, "on"); // Cập nhật trạng thái là "on"
        } else {
            upload_data(path, "off"); // Cập nhật trạng thái là "off"
        }
    }

};

#endif
