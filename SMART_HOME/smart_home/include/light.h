#ifndef LIGHT_H
#define LIGHT_H
#include"device.h"
#include "wifi_and_firebase.h"
#include "button.h"
class Light : public Device {// 1 chân nối vcc, chân điều khiển kéo xuống low thì sáng
public:
  Light(int pin, String id, String name, String roomId, int buttonPin=-1, bool needFbUpdate=true) : Device(pin, id, name, roomId, buttonPin, needFbUpdate) {}
  void begin(){ // Khởi tạo LED và cấu hình chân output
    pinMode(getPin(), OUTPUT); // Cấu hình chân LED là OUTPUT
    digitalWrite(getPin(), LOW); // Đưa chân xuống LOW để bật LED
    setMainState(true); // Cập nhật trạng thái hiện tại là ON
}

void turnOnLed() { // Hàm bật LED
    // Chỉ bật khi LED đang tắt
    if(!getMainState()) {
        setMainState(true); // Cập nhật trạng thái ON
        digitalWrite(getPin(), LOW); // Xuất mức LOW để bật LED
        Serial.println("💡 " + getId() + " ON"); // In trạng thái ra Serial
    }
}
void turnOffLed() { // Hàm tắt LED
    // Chỉ tắt khi LED đang bật
    if(getMainState()) {
        setMainState(false); // Cập nhật trạng thái OFF
        digitalWrite(getPin(), HIGH); // Xuất mức HIGH để tắt LED
        Serial.println("💡 " + getId() + " OFF"); // In trạng thái ra Serial
    }
}
void toggleLed() { // Đảo trạng thái LED
    // Nếu LED đang bật thì tắt
    if(getMainState()) {
        turnOffLed();
    } else { // Nếu LED đang tắt thì bật
        turnOnLed();
    }
}

void sendStateToFirebase() { // Gửi trạng thái LED lên Firebase
    // Chuyển trạng thái bool sang String
    String state = getMainState() ? "on" : "off";
    // Upload trạng thái lên Firebase
    uploadData(getPath() + "/status", state);
}
void buttonPress(){ // Xử lý khi nhấn nút vật lý
    toggleLed(); // Đổi trạng thái LED
    sendStateToFirebase(); // Đồng bộ trạng thái lên Firebase
}
void FbUpdate(String st){ // Cập nhật trạng thái từ Firebase
    // Nếu LED đang bật nhưng Firebase yêu cầu OFF
    if(getMainState() && st == "off"){
        turnOffLed();
    }
    // Nếu LED đang tắt nhưng Firebase yêu cầu ON
    else if(!getMainState() && st == "on"){
        turnOnLed();
    }
}
};
#endif