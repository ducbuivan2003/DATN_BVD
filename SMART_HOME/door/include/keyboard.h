#ifndef KEYBOARD_H
#define KEYBOARD_H
#include <Arduino.h>


class key_board {
        private:
          const int rows = 3;
          String str ="";
          const int cols = 4;
          //const int row_pins[3] = {34, 35, 32};  // Chân hàng (INPUT, PULLDOWN ngoài)
          const int row_pins[3] = {14,12,13};  // Chân hàng (INPUT, PULLDOWN ngoài)
          const int col_pins[4] = {27, 26, 25, 33};  // Chân cột (OUTPUT)
          
          unsigned long last_debounce_time[3][4] = {0};  // Lưu thời gian debounce
          bool last_button_state[3][4] = {false};  // Trạng thái trước đó của phím
          bool button_state[3][4] = {false};  // Trạng thái hiện tại
          bool key_pressed[3][4] = {false};  // Lưu trạng thái phím đã nhấn để tránh lặp lại nhấn liên tục
          unsigned long debounce_delay = 80;  // Thời gian debounce (20ms)
      
          String arr2[3][4] = {
              {"1", "4", "7", "L"},
              {"2", "5", "8", "0"},
              {"3", "6", "9", "R"}
          };
      
        public:
          key_board() {}
      
          void init() {
              // Cài đặt các chân hàng và cột
              for (int i = 0; i < rows; i++) {
                  pinMode(row_pins[i], INPUT_PULLDOWN);  // Dùng điện trở Pull-Down ngoài
              }
              for (int j = 0; j < cols; j++) {
                  pinMode(col_pins[j], OUTPUT);
                  digitalWrite(col_pins[j], LOW);  // Đặt ban đầu là LOW
              }
          }
      
          void reset() {
              // Đặt lại trạng thái phím đã nhấn
              for (int i = 0; i < rows; i++) {
                  for (int j = 0; j < cols; j++) {
                      key_pressed[i][j] = false;
                      button_state[i][j] = false;  // Đặt lại trạng thái phím
                      last_button_state[i][j] = false;  // Đặt lại trạng thái trước đó
                      last_debounce_time[i][j] = 0;  // Đặt lại thời gian debounce
                  }
              }
          }
          String get_press_key() {
              String rt = "null";
              unsigned long current_time = millis();
      
              // Quét qua các cột
              for (int i = 0; i < cols; i++) {
                  digitalWrite(col_pins[i], HIGH);  // Kích hoạt cột
      
                  // Quét qua các hàng
                  for (int j = 0; j < rows; j++) {
                      bool reading = digitalRead(row_pins[j]);  // Đọc trạng thái phím
      
                      // Kiểm tra debounce
                      if (reading != last_button_state[j][i]) {
                          last_debounce_time[j][i] = current_time;  // Lưu lại thời gian khi có thay đổi
                      }
      
                      if ((current_time - last_debounce_time[j][i]) > debounce_delay) {
                          if (reading == HIGH && !key_pressed[j][i]) {  // Phím được nhấn lần đầu
                              rt = arr2[j][i];  // Lưu phím nhấn
                              key_pressed[j][i] = true;  // Đánh dấu là đã nhấn
                          } 
                          else if (reading == LOW && key_pressed[j][i]) {  // Phím được thả ra
                              key_pressed[j][i] = false;  // Đánh dấu là phím đã được thả
                          }
                          button_state[j][i] = reading;  // Cập nhật trạng thái hiện tại
                      }
      
                      last_button_state[j][i] = reading;  // Cập nhật trạng thái trước đó
                  }
      
                  digitalWrite(col_pins[i], LOW);  // Tắt cột
              }
      
              return rt;  // Trả về phím đã nhấn hoặc "null" nếu không có phím nhấn
          }
      
          void update_str(String a){
              str += a;
      
          }
          String get_str(){
              return str;  // Trả về chuỗi str
          }   
          void delete_str(){
              str = "";  // Xóa chuỗi str
          }
      };

#endif