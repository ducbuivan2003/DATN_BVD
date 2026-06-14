#ifndef FAN_H
#define FAN_H
#include "device.h"
#include "wifi_and_firebase.h"
#include "button.h"

class Fan : public Device {
public:
  Fan(int pin, String id, String name, String roomId, int buttonPin =-1, bool needFbUpdate =true) : Device(pin, id, name, roomId, buttonPin, needFbUpdate) {
  }
  void begin() { // Khởi tạo quạt và cấu hình chân output
    pinMode(getPin(), OUTPUT); // Cấu hình chân điều khiển quạt là OUTPUT
    digitalWrite(getPin(), LOW); // Đưa chân về LOW để tắt quạt ban đầu
    setMainState(false); // Cập nhật trạng thái quạt là OFF
}
void turnOnFan() { // Hàm bật quạt
    // Chỉ bật khi quạt đang tắt
    if(!getMainState()) {
      setMainState(true); // Cập nhật trạng thái quạt là ON
        digitalWrite(getPin(), HIGH); // Xuất mức HIGH để bật relay/quạt
        Serial.println("🌀 " + getId() + " ON"); // In trạng thái lên Serial Monitor
    }
}
void turnOffFan() { // Hàm tắt quạt
    // Chỉ tắt khi quạt đang bật
    if(getMainState()) {
        setMainState(false); // Cập nhật trạng thái quạt là OFF
        digitalWrite(getPin(), LOW); // Xuất mức LOW để tắt relay/quạt
        Serial.println("🌀 " + getId() + " OFF"); // In trạng thái lên Serial Monitor
    }
}
void toggleFan() { // Đảo trạng thái quạt
    // Nếu quạt đang bật thì tắt
    if(getMainState()) {
        turnOffFan();
    } else { // Nếu quạt đang tắt thì bật
        turnOnFan();
    }
}
void sendStateToFirebase() { // Gửi trạng thái quạt lên Firebase
    // Chuyển trạng thái bool sang chuỗi
    String state = getMainState() ? "on" : "off";
    // Upload trạng thái lên Firebase
    uploadData(getPath() + "/status", state);
}
void buttonPress(){ // Xử lý khi nhấn nút vật lý
    toggleFan(); // Đổi trạng thái quạt
    sendStateToFirebase(); // Đồng bộ trạng thái lên Firebase
}
void FbUpdate(String st){ // Cập nhật trạng thái từ Firebase
    // Nếu quạt đang bật nhưng Firebase yêu cầu OFF
    if(getMainState() && st == "off"){
        turnOffFan();
    }
    // Nếu quạt đang tắt nhưng Firebase yêu cầu ON
    else if(!getMainState() && st == "on"){
        turnOnFan();
    }
}
};

#endif