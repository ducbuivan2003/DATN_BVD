#include "wifi_and_firebase.h"
#include "rfid.h"
#include "oled.h"
#include "keyboard.h"

// pin ko thể dùng khi dùng wifi 34 35 36 39  
RFID_Door door(5, 15, 2); 
OLED oled; 
keyBoard kb; 
bool isTypePW = false;

QueueHandle_t keyQueue; // Hàng đợi cho phím nhấn

void StreamDoorData() {
    static unsigned long lastTime = 0;
    unsigned long currentTime = millis(); 
    if (currentTime - lastTime > 1000) {
        lastTime = currentTime;
        String st = downloadData(did + "/status");
        if (st == "on") {
            if (!door.getState()) {
                door.openDoor();
                oled.showOpen_door();
                oled.updatePrevious();
            }
        } else if (st == "off") {
            if (door.getState()) {
                door.closeDoor();
                oled.showClose_door();
                oled.updatePrevious();
            }
        } else {
            Serial.println("❌ Trạng thái không hợp lệ!");
            Serial.println("Trạng thái: " + st);
        }

        String pw = downloadData(did + "/password");
        door.updatePassword(pw);
    }
}

void allDoor() {
    StreamDoorData();

    if (door.checkCard()) {
        if (door.getState()) {
            //door.closeDoor();
            door.updateToFB();
            oled.showClose_door();
            oled.updatePrevious();
            
            isTypePW = false;
        } else {
            door.openDoor();
            oled.showAccessGranted();
            oled.updatePrevious();
            door.updateToFB();
            isTypePW = false;
        }
    }

    String key = "";
    if (xQueueReceive(keyQueue, &key, 0) == pdTRUE) {
        isTypePW = true;
        Serial.println(key);
        if (key == "L") {
            kb.deleteStr();
            oled.showPassword(kb.getStr());
        } else if (key == "R") {
            oled.showPassword(kb.getStr());
            if (door.getState() && kb.getStr() == "") {
                door.closeDoor();
                door.updateToFB();
                oled.showClose_door();
                oled.updatePrevious();
                isTypePW = false;
            } else {
                if (door.checkPassword(kb.getStr())) {
                    door.openDoor();
                    door.updateToFB();
                    oled.displayOK();
                    oled.updatePrevious();
                    isTypePW = false;
                } else {
                    oled.showWrongPassword();
                    oled.updatePrevious();
                    isTypePW = false;
                }
            }
            kb.deleteStr();
        } else {
            kb.updateStr(key);
            oled.showPassword(kb.getStr());
            isTypePW = true;
        }
        kb.reset();
    }

    if (!isTypePW) {
        oled.returnHomeScreen();
    }
}

// Task chạy riêng để đọc bàn phím
void keyboardTask(void *parameter) {
    while (true) {
        String key = kb.getPressKey();
        if (key != "null") {
            xQueueSend(keyQueue, &key, portMAX_DELAY);
        }
        vTaskDelay(50 / portTICK_PERIOD_MS); // giảm tần suất quét
    }
}

void setup() {
    setupwifiFirebase();
    Serial.begin(115200);
    door.init();
    oled.init();
    kb.init();

    // Tạo hàng đợi cho phím
    keyQueue = xQueueCreate(10, sizeof(String));

    // Tạo task cho bàn phím
    xTaskCreate(
        keyboardTask,      // Hàm task
        "Keyboard Task",   // Tên task
        2048,              // Stack size
        NULL,              // Tham số
        1,                 // Ưu tiên
        NULL               // Handle
    );

    StreamDoorData();
}

void loop() {
    allDoor(); 
}
