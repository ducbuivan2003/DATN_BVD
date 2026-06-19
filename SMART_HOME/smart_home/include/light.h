#ifndef LIGHT_H
#define LIGHT_H
#include"device.h"
#include "wifi_and_firebase.h"
#include "button.h"
class Light : public Device {// 1 chân nối vcc, chân điều khiển kéo xuống low thì sáng
public:
  Light(int pin, String id, String name, String roomId, int button_pin=-1, bool need_FB_update=true) : Device(pin, id, name, roomId, button_pin, need_FB_update) {}
  void begin(){ // Khởi tạo LED và cấu hình chân output
    pinMode(get_pin(), OUTPUT); // Cấu hình chân LED là OUTPUT
    digitalWrite(get_pin(), LOW); // Đưa chân xuống LOW để bật LED
    set_main_state(true); // Cập nhật trạng thái hiện tại là ON
}

void turn_on_led() { // Hàm bật LED
    // Chỉ bật khi LED đang tắt
    if(!get_main_state()) {
        set_main_state(true); // Cập nhật trạng thái ON
        digitalWrite(get_pin(), LOW); // Xuất mức LOW để bật LED
        Serial.println("💡 " + get_Id() + " ON"); // In trạng thái ra Serial
    }
}
void turn_off_led() { // Hàm tắt LED
    // Chỉ tắt khi LED đang bật
    if(get_main_state()) {
        set_main_state(false); // Cập nhật trạng thái OFF
        digitalWrite(get_pin(), HIGH); // Xuất mức HIGH để tắt LED
        Serial.println("💡 " + get_Id() + " OFF"); // In trạng thái ra Serial
    }
}
void toggle_led() { // Đảo trạng thái LED
    // Nếu LED đang bật thì tắt
    if(get_main_state()) {
        turn_off_led();
    } else { // Nếu LED đang tắt thì bật
        turn_on_led();
    }
}

void send_state_to_FB() { // Gửi trạng thái LED lên Firebase
    // Chuyển trạng thái bool sang String
    String state = get_main_state() ? "on" : "off";
    // Upload trạng thái lên Firebase
    upload_data(get_path() + "/status", state);
}
void button_press(){ // Xử lý khi nhấn nút vật lý
    toggle_led(); // Đổi trạng thái LED
    send_state_to_FB(); // Đồng bộ trạng thái lên Firebase
}
void Fb_update(String st){ // Cập nhật trạng thái từ Firebase
    // Nếu LED đang bật nhưng Firebase yêu cầu OFF
    if(get_main_state() && st == "off"){
        turn_off_led();
    }
    // Nếu LED đang tắt nhưng Firebase yêu cầu ON
    else if(!get_main_state() && st == "on"){
        turn_on_led();
    }
}
};
#endif