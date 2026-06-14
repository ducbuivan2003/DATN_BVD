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
  SCK    18 YELLOW
  MOSI   23 WHITE
  MISO   19 GREEN
  SDA/SS 5 ORANGE
  RST    15 BLUE
  */
private:
    int SERVO;
    bool state = false; // Mặc định là đóng cửa
    MFRC522 rfid; // Đối tượng RFID
    String pw ="1234"; // Mật khẩu mặc định
    Servo myServo;
    const byte validUID[2][4] = {
        {0xC3, 0x74, 0xCD, 0x28}, // Thẻ 1
        {0x33, 0xA2, 0x3F, 0x14}  // Thẻ 2
    };
    int validUIDCount = 2; // Số lượng thẻ hợp lệ

public:
    RFID_Door(int SS,int RS, int SERVO) : rfid(SS, RS) { // Khởi tạo RFID với chân SS
        this->SERVO = SERVO;
    }

    void init() {
        SPI.begin(); // Khởi tạo SPI (ESP32 tự động xử lý các chân)
        rfid.PCD_Init(); // Khởi tạo RFID
        myServo.attach(SERVO); // Gán chân điều khiển servo
        myServo.write(0); // Đặt servo về vị trí đóng cửa
    }
    
    bool getState() {
        return state;
    }

    void setState(bool newState) {
        state = newState;
    }

    void turnServo(int angle) {
        myServo.write(angle);
    }

    void openDoor() {
        Serial.println("✅ Mở cửa...");
        turnServo(0); // Mở cửa
        state = true;
    }

    void closeDoor() {
        Serial.println("🔒 Đóng cửa...");
        turnServo(112); // Đóng cửa
        state = false;
    }

    bool isValidUID() {
        // Kiểm tra nếu UID hợp lệ
        for (int i = 0; i < validUIDCount; i++) {
            if (memcmp(rfid.uid.uidByte, validUID[i], 4) == 0) {
                return true;
            }
        }
        return false;
    }

    void printUID() {
        Serial.print("UID: ");
        for (byte i = 0; i < rfid.uid.size; i++) {
            Serial.print(rfid.uid.uidByte[i], HEX);
            Serial.print(" ");
        }
        Serial.println();
    }

    bool checkCard() {
        bool rt = false;  // Declare rt variable
        //Serial.println("⏳ Đang chờ thẻ...");
        if (!rfid.PICC_IsNewCardPresent()) return rt;  // Kiểm tra thẻ mới
        if (!rfid.PICC_ReadCardSerial()) return rt;   // Đọc serial của thẻ

        //printUID(); // In UID ra Serial Monitor
        Serial.print("checking...");
        if (isValidUID()) {
            Serial.println("✅ Thẻ hợp lệ");
            rt = true;
        } else {
            Serial.println("⛔ Thẻ không hợp lệ!");
        }

        rfid.PICC_HaltA();  // Dừng giao tiếp với thẻ
        rfid.PCD_StopCrypto1(); // Dừng mã hóa dữ liệu
        return rt;
    }
    bool checkPassword(String input) {
        if (input == pw) {
            Serial.println("✅ Mật khẩu hợp lệ");
            return true;
        } else {
            Serial.println("⛔ Mật khẩu không hợp lệ!");
            return false;
        }
    }
    void updatePassword(String newPassword) {
        if(newPassword != pw) {
            pw = newPassword; // Cập nhật mật khẩu mới
            Serial.println("✅ Mật khẩu đã được cập nhật: " + pw);
        }
    }
    void updateToFB(){
        String path = did + "/status"; // Đường dẫn đến trạng thái cửa
        if (state) {
            uploadData(path, "on"); // Cập nhật trạng thái là "on"
        } else {
            uploadData(path, "off"); // Cập nhật trạng thái là "off"
        }
    }

};

#endif