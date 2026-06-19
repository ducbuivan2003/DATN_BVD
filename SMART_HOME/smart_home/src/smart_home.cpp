#include "string.h"
#include <vector>
#include <wifi_and_firebase.h>
#include "button.h"
#include "device.h"
#include "light.h"
#include "fan.h"
#include "mq2.h"
#include "ac.h"
#include "fm52.h"
#include "dht11.h"
//std::vector<int> buttonPins ={};  // Chân các nút bấm
// ko upload state trong class


#define PHONG_BEP "23714458"
#define PHONG_NGU "323238991"
#define PHONG_KHACH "374566167"
#define PHONG_GARA "589955086"


//-------------------------------------------------STREAM------------------------------------------------------
#if 1
void stream_data_from_FB(std::vector<Device*> &dvList){
  static unsigned long last_time = 0;

  unsigned long current_time = millis(); 

  if (current_time - last_time > 1000) 
  {  // nhận dữ liệu mỗi 1 giây
    last_time = current_time;
    for(int i=0;i<dvList.size();i++)
    {
      String st = download_data(dvList[i]->get_path() + "/status");
      dvList[i]->Fb_update(st);
    }
  }

return;
}
#endif
//-------------------------------------------------END STREAM------------------------------------------------------
std::vector<int> buttonPins ={};  // Chân các nút bấm
std::vector<Device*> Device::dvList;
volatile int last_pressed_button = -1;

Fan fan_bep(27, "82322702", "Quạt 1", PHONG_BEP, 26);// 1 chan noi 5v 1 chan noi BJT cuc C 
Light light_bep(25, "903872522", "Đèn 1", PHONG_BEP, 33);// 1 chân nối vcc , chân điều khiển kéo xuống low thì sáng (25)
Light light_hall(12, "null", "Đèn 2", PHONG_KHACH, -1,false);
Light light_khach(14, "254010416", "Đèn 3", PHONG_KHACH, 34);
Light light_ngu(22, "274878463", "Đèn 5", PHONG_NGU, 4); // chân 4 là chân điều khiển, chân 22 là chân nối dây âm
Light light_garage(19, "356325987", "Đèn 4", PHONG_GARA, -1);
AirConditioner airC(18, "716502344", "Điều hòa", PHONG_NGU, -1);

MQ2 mq2(-1,32, 200);// A, D, Threshold, dùng Analog cho đẹp  (cảm biến gas) 2 chan nguon 5v, 1 chan tin hieu
FM52 fm52(35);// chân digital (cảm biến tiệm cận) 2 chan nguon 3.3v, 1 chasn tin hieu
DHT11 dht11(23); // chân 23 làm tín hiệu input

void setup() {
  setup_wifi_firebase();
  Serial.begin(115200);
  fan_bep.begin();

  light_hall.begin();

  light_bep.begin();

  light_khach.begin();

  light_garage.begin();

  light_ngu.begin();

  mq2.begin();

  dht11.begin();

  airC.begin();

  fm52.begin();
  
  button_begin();
  // in ra chân button
  Serial.print("Duc log Button pins: ");
  for(int i = 0; i < buttonPins.size(); i++) 
  {
    Serial.print("Button pin: ");
    Serial.println(buttonPins[i]);
  }
  Serial.println("Start Streaming data from Fb");
  stream_data_from_FB(Device::dvList);
  Serial.println("Setup done successfully");

}



// while

void loop() {

  stream_data_from_FB(Device::dvList);

  if(fm52.is_detected_close())
  {
    light_hall.turn_on_led();
  }
  else
  {
    light_hall.turn_off_led();
  }

  mq2.detect_gas();
  if(mq2.get_detected_gas())
  {
    if(!fan_bep.get_main_state())
    {
      fan_bep.turn_on_fan();
      fan_bep.send_state_to_FB();
    }
  }

  if(mq2.isNeedTurnFan())
  {
    fan_bep.turn_off_fan();
    fan_bep.send_state_to_FB();
    mq2.justTurnOff();
  }

  unsigned long current_time = millis();
  static unsigned long last_time = 0;
  if (current_time - last_time > 2345)
   { 
    last_time = current_time;
    dht11.read_sensor();
   // String st = String(dht11.getTemperature());
    airC.set_temperature(String(dht11.getTemperature()));
   // airC.send_other_state_to_FB();
  }

if (last_pressed_button != -1) 
{
        switch (last_pressed_button) {
            case 33:
            {
                light_bep.button_press();
                break;
            }

            case 26:
            {
              
              fan_bep.button_press();
              break;
            }
            case 4:
            {
                light_ngu.button_press();
                break;
            }
            case 34:
            {
                light_khach.button_press();
                break;
            }
            default:
                break;
        }
        last_pressed_button = -1; // Xóa trạng thái sau khi xử lý
    }
}
