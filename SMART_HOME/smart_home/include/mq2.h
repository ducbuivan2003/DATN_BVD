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
    int sensor_value;                // Giá trị cảm biến
    int sensorThres = 400;          // Ngưỡng phát hiện gas
    bool need_turn_off_fan = false;    // Cờ tắt quạt
    bool dectected_gas = false;      // Trạng thái phát hiện gas
    unsigned long gas_detected_time = 0; // Thời điểm phát hiện gas
    unsigned long last_time = 0;     // Thời điểm phát hiện gần nhất
    bool is_sent_noti = false;        // Cờ gửi thông báo

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
    void sent_noti_to_FB() { // Gửi thông báo gas lên Firebase
        push_notification("Có khí gas", DEVICE_ID);
    }
      void detect_gas() {
    unsigned long now = millis(); // Lấy thời gian hiện tại
    int gas_value = (APin != -1) ? analogRead(APin) : 0; // Đọc giá trị Analog
    int gas_state = (Dpin != -1) ? digitalRead(Dpin) : HIGH; // Đọc trạng thái Digital
    // Kiểm tra phát hiện khí gas
    bool gas_detected = (APin != -1 && gas_value > sensorThres) || (Dpin != -1 && gas_state == LOW);
    if (gas_detected) {
        // Lưu thời điểm bắt đầu phát hiện gas
        if (gas_detected_time == 0) {
                    gas_detected_time = now;  // Bắt đầu đếm thời gian phát hiện gas
                }
                if (!dectected_gas && now - gas_detected_time >= 20) {  // Đã phát hiện gas trên 1 giây
                    dectected_gas = true;
                    need_turn_off_fan = false;
                    last_time = now;  // Cập nhật lần cuối phát hiện gas
                    if (!is_sent_noti) {
                        sent_noti_to_FB();
                        is_sent_noti = true;
                    }
                }
            } else {  
                gas_detected_time = 0;  // Reset bộ đếm thời gian phát hiện gas
                if (dectected_gas && now - last_time >= 5000) {  // Không có gas trong 4 giây
                    dectected_gas = false;
                    need_turn_off_fan = true;
                    is_sent_noti = false;
                }
            }
    
        }
    bool get_detected_gas() { // Lấy trạng thái phát hiện gas
    return dectected_gas;
}
bool isNeedTurnFan() { // Kiểm tra cần tắt quạt
   return need_turn_off_fan;
}
void justTurnOff(){ // Đặt lại trạng thái tắt quạt
    need_turn_off_fan = false;
}
};
#endif