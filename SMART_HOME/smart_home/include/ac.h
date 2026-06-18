#ifndef AC_H
#define AC_H
#include "device.h"
#include "wifi_and_firebase.h"
#include "button.h"

class AirConditioner : public Device {
  public:
    AirConditioner(int pin, String id, String name, String roomId, int buttonPin=-1, bool needFBUpdate=true) : Device(pin, id, name, roomId, buttonPin, needFBUpdate) {}
    void begin() { // Khởi tạo máy lạnh
    pinMode(getPin(), OUTPUT); // Cấu hình chân điều khiển là OUTPUT
    digitalWrite(getPin(), LOW); // Tắt máy lạnh ban đầu
    setMainState(false); // Cập nhật trạng thái OFF
 }
void turnOnAC() { // Bật máy lạnh
    if(!getMainState()) { // Chỉ bật khi đang tắt
        setMainState(true); // Cập nhật trạng thái ON
        digitalWrite(getPin(), HIGH); // Xuất mức HIGH để bật relay
        Serial.println("❄️ " + getId() + " ON"); // In trạng thái lên Serial
    }
}
void turnOffAC() { // Tắt máy lạnh
    if(getMainState()) { // Chỉ tắt khi đang bật
        setMainState(false); // Cập nhật trạng thái OFF
        digitalWrite(getPin(), LOW); // Xuất mức LOW để tắt relay
        Serial.println("❄️ " + getId() + " OFF"); // In trạng thái lên Serial
    }
}
void toggleAC() { // Đảo trạng thái máy lạnh
    if(getMainState()) {
        turnOffAC();
    } else {
        turnOnAC();
    }
}
void setTemperature(String temp) { // Cập nhật nhiệt độ máy lạnh
    setOtherState(temp); // Lưu nhiệt độ mới
    sendOtherStateToFirebase(); // Đồng bộ nhiệt độ lên Firebase
    Serial.println("❄️ " + getId() + " set temperature: " + temp + "°C");
}
void send_state_to_fb() { // Gửi trạng thái và nhiệt độ lên Firebase
    String state = getMainState() ? "on" : "off"; // Chuyển trạng thái sang String
    uploadData(getPath() + "/status", state); // Upload trạng thái ON/OFF
    uploadData(getPath() + "/temperature", getOtherState()); // Upload nhiệt độ
}
void sendOtherStateToFirebase() { // Gửi riêng nhiệt độ lên Firebase
    uploadData(getPath() + "/temperature", getOtherState());
}
void FbUpdate(String st){ // Cập nhật trạng thái từ Firebase
    if(getMainState() && st == "off"){ // Nếu đang ON nhưng Firebase yêu cầu OFF
        turnOffAC();
    }
    else if(!getMainState() && st == "on"){ // Nếu đang OFF nhưng Firebase yêu cầu ON
        turnOnAC();
    }
}
};

#endif