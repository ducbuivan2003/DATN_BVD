#ifndef AC_H
#define AC_H
#include "device.h"
#include "wifi_and_firebase.h"
#include "button.h"

class AirConditioner : public Device {
  public:
    AirConditioner(int pin, String id, String name, String roomId, int button_pin=-1, bool needFBUpdate=true) : Device(pin, id, name, roomId, button_pin, needFBUpdate) {}
    void begin() { // Khởi tạo máy lạnh
    pinMode(get_pin(), OUTPUT); // Cấu hình chân điều khiển là OUTPUT
    digitalWrite(get_pin(), LOW); // Tắt máy lạnh ban đầu
    set_main_state(false); // Cập nhật trạng thái OFF
 }
void turn_onAC() { // Bật máy lạnh
    if(!get_main_state()) { // Chỉ bật khi đang tắt
        set_main_state(true); // Cập nhật trạng thái ON
        digitalWrite(get_pin(), HIGH); // Xuất mức HIGH để bật relay
        Serial.println("❄️ " + get_Id() + " ON"); // In trạng thái lên Serial
    }
}
void turn_offAC() { // Tắt máy lạnh
    if(get_main_state()) { // Chỉ tắt khi đang bật
        set_main_state(false); // Cập nhật trạng thái OFF
        digitalWrite(get_pin(), LOW); // Xuất mức LOW để tắt relay
        Serial.println("❄️ " + get_Id() + " OFF"); // In trạng thái lên Serial
    }
}
void toggleAC() { // Đảo trạng thái máy lạnh
    if(get_main_state()) {
        turn_offAC();
    } else {
        turn_onAC();
    }
}
void set_temperature(String temp) { // Cập nhật nhiệt độ máy lạnh
    set_other_state(temp); // Lưu nhiệt độ mới
    send_other_state_to_FB(); // Đồng bộ nhiệt độ lên Firebase
    Serial.println("❄️ " + get_Id() + " set temperature: " + temp + "°C");
}
void send_state_to_FB() { // Gửi trạng thái và nhiệt độ lên Firebase
    String state = get_main_state() ? "on" : "off"; // Chuyển trạng thái sang String
    upload_data(get_path() + "/status", state); // Upload trạng thái ON/OFF
    upload_data(get_path() + "/temperature", get_other_state()); // Upload nhiệt độ
}
void send_other_state_to_FB() { // Gửi riêng nhiệt độ lên Firebase
    upload_data(get_path() + "/temperature", get_other_state());
}
void Fb_update(String st){ // Cập nhật trạng thái từ Firebase
    if(get_main_state() && st == "off"){ // Nếu đang ON nhưng Firebase yêu cầu OFF
        turn_offAC();
    }
    else if(!get_main_state() && st == "on"){ // Nếu đang OFF nhưng Firebase yêu cầu ON
        turn_onAC();
    }
}
};

#endif