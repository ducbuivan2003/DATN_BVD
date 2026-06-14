#ifndef KEYBOARD_H
#define KEYBOARD_H
#include <Arduino.h>


class keyBoard {
        private:
          const int rows = 3;
          String str ="";
          const int cols = 4;
          //const int rowPins[3] = {34, 35, 32};  // Chân hàng (INPUT, PULLDOWN ngoài)
          const int rowPins[3] = {14,12,13};  // Chân hàng (INPUT, PULLDOWN ngoài)
          const int colPins[4] = {27, 26, 25, 33};  // Chân cột (OUTPUT)
          
          unsigned long lastDebounceTime[3][4] = {0};  // Lưu thời gian debounce
          bool lastButtonState[3][4] = {false};  // Trạng thái trước đó của phím
          bool buttonState[3][4] = {false};  // Trạng thái hiện tại
          bool keyPressed[3][4] = {false};  // Lưu trạng thái phím đã nhấn để tránh lặp lại nhấn liên tục
          unsigned long debounceDelay = 80;  // Thời gian debounce (20ms)
      
          String arr2[3][4] = {
              {"1", "4", "7", "L"},
              {"2", "5", "8", "0"},
              {"3", "6", "9", "R"}
          };
      
        public:
          keyBoard() {}
      
          void init() {
              // Cài đặt các chân hàng và cột
              for (int i = 0; i < rows; i++) {
                  pinMode(rowPins[i], INPUT_PULLDOWN);  // Dùng điện trở Pull-Down ngoài
              }
              for (int j = 0; j < cols; j++) {
                  pinMode(colPins[j], OUTPUT);
                  digitalWrite(colPins[j], LOW);  // Đặt ban đầu là LOW
              }
          }
      
          void reset() {
              // Đặt lại trạng thái phím đã nhấn
              for (int i = 0; i < rows; i++) {
                  for (int j = 0; j < cols; j++) {
                      keyPressed[i][j] = false;
                      buttonState[i][j] = false;  // Đặt lại trạng thái phím
                      lastButtonState[i][j] = false;  // Đặt lại trạng thái trước đó
                      lastDebounceTime[i][j] = 0;  // Đặt lại thời gian debounce
                  }
              }
          }
          String getPressKey() {
              String rt = "null";
              unsigned long currentTime = millis();
      
              // Quét qua các cột
              for (int i = 0; i < cols; i++) {
                  digitalWrite(colPins[i], HIGH);  // Kích hoạt cột
      
                  // Quét qua các hàng
                  for (int j = 0; j < rows; j++) {
                      bool reading = digitalRead(rowPins[j]);  // Đọc trạng thái phím
      
                      // Kiểm tra debounce
                      if (reading != lastButtonState[j][i]) {
                          lastDebounceTime[j][i] = currentTime;  // Lưu lại thời gian khi có thay đổi
                      }
      
                      if ((currentTime - lastDebounceTime[j][i]) > debounceDelay) {
                          if (reading == HIGH && !keyPressed[j][i]) {  // Phím được nhấn lần đầu
                              rt = arr2[j][i];  // Lưu phím nhấn
                              keyPressed[j][i] = true;  // Đánh dấu là đã nhấn
                          } 
                          else if (reading == LOW && keyPressed[j][i]) {  // Phím được thả ra
                              keyPressed[j][i] = false;  // Đánh dấu là phím đã được thả
                          }
                          buttonState[j][i] = reading;  // Cập nhật trạng thái hiện tại
                      }
      
                      lastButtonState[j][i] = reading;  // Cập nhật trạng thái trước đó
                  }
      
                  digitalWrite(colPins[i], LOW);  // Tắt cột
              }
      
              return rt;  // Trả về phím đã nhấn hoặc "null" nếu không có phím nhấn
          }
      
          void updateStr(String a){
              str += a;
      
          }
          String getStr(){
              return str;  // Trả về chuỗi str
          }   
          void deleteStr(){
              str = "";  // Xóa chuỗi str
          }
      };

#endif