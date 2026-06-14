#ifndef DEVICE_H
#define DEVICE_H

#include <Arduino.h>
#include <vector>
#include "button.h"
#define PHONG_BEP "23714458"
#define PHONG_NGU "323238991"
#define PHONG_KHACH "374566167"

extern std::vector<int> buttonPins; // Chân các nút bấm

class Device {
private:
    int pin;        // Chân kết nối thiết bị
    String id;      // ID thiết bị
    String name;    // Tên thiết bị
      String path; // Đường dẫn Firebase, ko có userid
      bool mainState;  // Trạng thái bật/tắt của thiết bị
      String otherState; //nhiệt độ hoặc độ ẩm
      String controlState; // Chỉ dành cho điều hòa
      String roomId; // ID của phòng chứa thiết bị
      int buttonPin;
      bool needFbUpdate = true;// mặc định là có nhé
  public:
    static std::vector<Device*> dvList;
      Device(int pin, String id = "unknown", String name = "unknown", String roomId = "unknown", int buttonPin = -1, bool needFbUpdate = true) {
        this->pin = pin;
          this->id = id;
          this->name = name;
          mainState = false;
          controlState = "unknown";
          otherState = "unknown";
          this->buttonPin = buttonPin;
          this->roomId = roomId;
          path ="/"+roomId+ "/" + id;
          this->needFbUpdate = needFbUpdate;
          if(buttonPin != -1) {
            buttonPins.push_back(buttonPin);
          }
          if(needFbUpdate){
            dvList.push_back(this);
          }
      }
      ~Device() {}
      bool getMainState() { return mainState; }
      void setMainState(bool state) { mainState = state; }
      void setOtherState(String state) { otherState = state; }
      String getId() { return id; }
      String getOtherState() { return otherState; }
      String getControlState() { return controlState; }
      String getName() { return name; }
      void setControlState(String state) { controlState = state; }
      int getPin() { return pin; }
      void setPin(int pin) { this->pin = pin; }
      void setPath(String path) { this->path = path; }
      String getPath() { return path; }
      int getButtonPin() { return buttonPin; }
      void setButtonPin(int buttonPin) { this->buttonPin = buttonPin; }
      void setRoomId(String roomId) { this->roomId = roomId; }
      virtual void FbUpdate(String st) = 0; 
      String getRoomId() { return roomId; }        

  };

#endif
