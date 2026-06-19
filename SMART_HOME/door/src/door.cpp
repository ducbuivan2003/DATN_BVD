#include "wifi_and_firebase.h"
#include "rfid.h"
#include "oled.h"
#include "keyboard.h"

// pin ko thể dùng khi dùng wifi 34 35 36 39  
RFID_Door door(5, 15, 2); 
OLED oled; 
key_board kb; 
bool is_type_PW = false;

QueueHandle_t key_queue; // Hàng đợi cho phím nhấn

void stream_door_data() {
    static unsigned long last_time = 0;
    unsigned long current_time = millis(); 
    if (current_time - last_time > 1000) {
        last_time = current_time;
        String st = download_data(did + "/status");
        if (st == "on") {
            if (!door.get_state()) {
                door.open_door();
                oled.show_open_door();
                oled.update_previous();
            }
        } else if (st == "off") {
            if (door.get_state()) {
                door.close_door();
                oled.show_close_door();
                oled.update_previous();
            }
        } else {
            Serial.println("❌ Trạng thái không hợp lệ!");
            Serial.println("Trạng thái: " + st);
        }

        String pw = download_data(did + "/password");
        door.update_password(pw);
    }
}

void all_door() {
    stream_door_data();

    if (door.check_card()) {
        if (door.get_state()) {
            //door.close_door();
            door.update_to_FB();
            oled.show_close_door();
            oled.update_previous();
            
            is_type_PW = false;
        } else {
            door.open_door();
            oled.show_access_granted();
            oled.update_previous();
            door.update_to_FB();
            is_type_PW = false;
        }
    }

    String key = "";
    if (xQueueReceive(key_queue, &key, 0) == pdTRUE) {
        is_type_PW = true;
        Serial.println(key);
        if (key == "L") {
            kb. delete_str();
            oled.show_password(kb.get_str());
        } else if (key == "R") {
            oled.show_password(kb.get_str());
            if (door.get_state() && kb.get_str() == "") {
                door.close_door();
                door.update_to_FB();
                oled.show_close_door();
                oled.update_previous();
                is_type_PW = false;
            } else {
                if (door.check_password(kb.get_str())) {
                    door.open_door();
                    door.update_to_FB();
                    oled.display_ok();
                    oled.update_previous();
                    is_type_PW = false;
                } else {
                    oled.show_wrong_password();
                    oled.update_previous();
                    is_type_PW = false;
                }
            }
            kb. delete_str();
        } else {
            kb.update_str(key);
            oled.show_password(kb.get_str());
            is_type_PW = true;
        }
        kb.reset();
    }

    if (!is_type_PW) {
        oled.return_home_screen();
    }
}

// Task chạy riêng để đọc bàn phím
void keyboardTask(void *parameter) {
    while (true) {
        String key = kb.get_press_key();
        if (key != "null") {
            xQueueSend(key_queue, &key, portMAX_DELAY);
        }
        vTaskDelay(50 / portTICK_PERIOD_MS); // giảm tần suất quét
    }
}

void setup() {
    setup_wifi_firebase();
    Serial.begin(115200);
    door.init();
    oled.init();
    kb.init();

    // Tạo hàng đợi cho phím
    key_queue = xQueueCreate(10, sizeof(String));

    // Tạo task cho bàn phím
    xTaskCreate(
        keyboardTask,      // Hàm task
        "Keyboard Task",   // Tên task
        2048,              // Stack size
        NULL,              // Tham số
        1,                 // Ưu tiên
        NULL               // Handle
    );

    stream_door_data();
}

void loop() {
    all_door(); 
}
