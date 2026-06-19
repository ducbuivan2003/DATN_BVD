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
      bool main_state;  // Trạng thái bật/tắt của thiết bị
      String other_state; //nhiệt độ hoặc độ ẩm
      String control_state; // Chỉ dành cho điều hòa
      String roomId; // ID của phòng chứa thiết bị
      int button_pin;
      bool need_FB_update = true;// mặc định là có nhé
  public:
    static std::vector<Device*> dvList;
      Device(int pin, String id = "unknown", String name = "unknown", String roomId = "unknown", int button_pin = -1, bool need_FB_update = true) {
        this->pin = pin;
          this->id = id;
          this->name = name;
          main_state = false;
          control_state = "unknown";
          other_state = "unknown";
          this->button_pin = button_pin;
          this->roomId = roomId;
          path ="/"+roomId+ "/" + id;
          this->need_FB_update = need_FB_update;
          if(button_pin != -1) {
            buttonPins.push_back(button_pin);
          }
          if(need_FB_update){
            dvList.push_back(this);
          }
      }
      ~Device() {}
      bool get_main_state() { return main_state; }
      void set_main_state(bool state) { main_state = state; }
      void set_other_state(String state) { other_state = state; }
      String get_Id() { return id; }
      String get_other_state() { return other_state; }
      String get_control_state() { return control_state; }
      String get_name() { return name; }
      void set_control_state(String state) { control_state = state; }
      int get_pin() { return pin; }
      void set_pin(int pin) { this->pin = pin; }
      void set_path(String path) { this->path = path; }
      String get_path() { return path; }
      int get_button_pin() { return button_pin; }
      void set_button_pin(int button_pin) { this->button_pin = button_pin; }
      void set_roomId(String roomId) { this->roomId = roomId; }
      virtual void Fb_update(String st) = 0; 
      String get_roomId() { return roomId; }        

  };

#endif
