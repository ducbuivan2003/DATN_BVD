#ifndef FM52_H
#define FM52_H
#include <Arduino.h>
#include "wifi_and_firebase.h"
#include "device.h"
#include "button.h"

class FM52{
  private:
    int pin;
   // bool state;// false là k có, true là có
  public:
    void begin(){
      pinMode(pin, INPUT);
    }
    FM52(int pin){
      this->pin = pin;
    //  this->state = false;
    }
/*     int isDetectedClose(){ 
      // -1 là chuyển sang k có, 
      //1 là chuyển sang có, 
     // 0 là ko thay đổi
      
      if(digitalRead(pin) == LOW && state == false){ 
        state = true;
        return 1;
      }else if(digitalRead(pin) == HIGH && state == true){// HIGH là tắt
        state = false;
        return -1;
      }else{
       // state = !digitalRead(pin);
        return 0;
      }
    } */
   bool isDetectedClose(){
     //state = !digitalRead(pin);
     return !digitalRead(pin);
   }

};
#endif