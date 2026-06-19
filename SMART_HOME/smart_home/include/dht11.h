#ifndef DHT11_H
#define DHT11_H

#include <Arduino.h>
#include "DHTesp.h" 

class DHT11{
private:
    int pin; // Chân kết nối DHT11
    DHTesp dht; // Đối tượng điều khiển cảm biến DHT
    float temperature = 0; // Giá trị nhiệt độ
    float humidity = 0; // Giá trị độ ẩm
public:
    DHT11(int pin) { // Constructor khởi tạo DHT11
        this->pin = pin;
    }
    void begin() { // Khởi tạo cảm biến DHT11
        dht.setup(pin, DHTesp::DHT11);
    }
    void read_sensor() { // Đọc dữ liệu từ cảm biến
        temperature = dht.getTemperature();
        humidity = dht.getHumidity();
    }
    float getTemperature() { // Lấy giá trị nhiệt độ
        return temperature;
    }
    float getHumidity() { // Lấy giá trị độ ẩm
        return humidity;
    }
};
#endif
