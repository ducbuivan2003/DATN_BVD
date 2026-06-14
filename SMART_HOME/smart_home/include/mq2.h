#ifndef MQ2_H
#define MQ2_H
#include <Arduino.h>
#include "wifi_and_firebase.h"
#include "device.h"
#include "button.h"


 class MQ2 {

private:
    int APin;                       // Chân Analog
    int Dpin;                       // Chân Digital
    int sensorValue;                // Giá trị cảm biến
    int sensorThres = 400;          // Ngưỡng phát hiện gas
    bool needTurnOffFan = false;    // Cờ tắt quạt
    bool dectectedGas = false;      // Trạng thái phát hiện gas
    unsigned long gasDetectedTime = 0; // Thời điểm phát hiện gas
    unsigned long lastTime = 0;     // Thời điểm phát hiện gần nhất
    bool isSentNoti = false;        // Cờ gửi thông báo

public:

    MQ2(int Apin, int Dpin, int sensorThres = 400) { // Constructor khởi tạo MQ2

        this->APin = Apin;
        this->Dpin = Dpin;
        this->sensorThres = sensorThres;
    }
    void begin() { // Khởi tạo cảm biến MQ2
        if (Dpin != -1) { // Kiểm tra chân Digital
            pinMode(Dpin, INPUT);
        } else {
            Serial.println("Dpin = -1");
        }
    }
    void sentNotiToFb() { // Gửi thông báo gas lên Firebase
        pushNotification("Có khí gas", DEVICE_ID);
    }
      void detectGas() {
    unsigned long now = millis(); // Lấy thời gian hiện tại
    int gasValue = (APin != -1) ? analogRead(APin) : 0; // Đọc giá trị Analog
    int gasState = (Dpin != -1) ? digitalRead(Dpin) : HIGH; // Đọc trạng thái Digital
    // Kiểm tra phát hiện khí gas
    bool gasDetected = (APin != -1 && gasValue > sensorThres) || (Dpin != -1 && gasState == LOW);
    if (gasDetected) {
        // Lưu thời điểm bắt đầu phát hiện gas
        if (gasDetectedTime == 0) {
                    gasDetectedTime = now;  // Bắt đầu đếm thời gian phát hiện gas
                }
                if (!dectectedGas && now - gasDetectedTime >= 20) {  // Đã phát hiện gas trên 1 giây
                    dectectedGas = true;
                    needTurnOffFan = false;
                    lastTime = now;  // Cập nhật lần cuối phát hiện gas
                    if (!isSentNoti) {
                        sentNotiToFb();
                        isSentNoti = true;
                    }
                }
            } else {  
                gasDetectedTime = 0;  // Reset bộ đếm thời gian phát hiện gas
                if (dectectedGas && now - lastTime >= 5000) {  // Không có gas trong 4 giây
                    dectectedGas = false;
                    needTurnOffFan = true;
                    isSentNoti = false;
                }
            }
    
        }
    bool getDectectedGas() { // Lấy trạng thái phát hiện gas
    return dectectedGas;
}
bool isNeedTurnFan() { // Kiểm tra cần tắt quạt
   return needTurnOffFan;
}
void justTurnOff(){ // Đặt lại trạng thái tắt quạt
    needTurnOffFan = false;
}
};
#endif