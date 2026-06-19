#ifndef FAN_H
#define FAN_H
#include "device.h"
#include "wifi_and_firebase.h"
#include "button.h"

class Fan : public Device {
public:
  Fan(int pin, String id, String name, String roomId, int button_pin =-1, bool need_FB_update =true) : Device(pin, id, name, roomId, button_pin, need_FB_update) {
  }
  void begin() { // Khởi tạo quạt và cấu hình chân output
    pinMode(get_pin(), OUTPUT); // Cấu hình chân điều khiển quạt là OUTPUT
    digitalWrite(get_pin(), LOW); // Đưa chân về LOW để tắt quạt ban đầu
    set_main_state(false); // Cập nhật trạng thái quạt là OFF
}
void turn_on_fan() { // Hàm bật quạt
    // Chỉ bật khi quạt đang tắt
    if(!get_main_state()) {
      set_main_state(true); // Cập nhật trạng thái quạt là ON
        digitalWrite(get_pin(), HIGH); // Xuất mức HIGH để bật relay/quạt
        Serial.println("🌀 " + get_Id() + " ON"); // In trạng thái lên Serial Monitor
    }
}
void turn_off_fan() { // Hàm tắt quạt
    // Chỉ tắt khi quạt đang bật
    if(get_main_state()) {
        set_main_state(false); // Cập nhật trạng thái quạt là OFF
        digitalWrite(get_pin(), LOW); // Xuất mức LOW để tắt relay/quạt
        Serial.println("🌀 " + get_Id() + " OFF"); // In trạng thái lên Serial Monitor
    }
}
void toggle_fan() { // Đảo trạng thái quạt
    // Nếu quạt đang bật thì tắt
    if(get_main_state()) {
        turn_off_fan();
    } else { // Nếu quạt đang tắt thì bật
        turn_on_fan();
    }
}
void send_state_to_FB() { // Gửi trạng thái quạt lên Firebase
    // Chuyển trạng thái bool sang chuỗi
    String state = get_main_state() ? "on" : "off";
    // Upload trạng thái lên Firebase
    upload_data(get_path() + "/status", state);
}
void button_press(){ // Xử lý khi nhấn nút vật lý
    toggle_fan(); // Đổi trạng thái quạt
    send_state_to_FB(); // Đồng bộ trạng thái lên Firebase
}
void Fb_update(String st){ // Cập nhật trạng thái từ Firebase
    // Nếu quạt đang bật nhưng Firebase yêu cầu OFF
    if(get_main_state() && st == "off"){
        turn_off_fan();
    }
    // Nếu quạt đang tắt nhưng Firebase yêu cầu ON
    else if(!get_main_state() && st == "on"){
        turn_on_fan();
    }
}
};

#endif