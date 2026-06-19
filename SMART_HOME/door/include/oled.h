#ifndef OLED_H
#define OLED_H
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
class OLED {
    private:
        // Thông số màn hình OLED
        static constexpr int SCREEN_WIDTH = 128; // Kích thước màn hình OLED
        static constexpr int SCREEN_HEIGHT = 64;
        static constexpr int OLED_RESET = -1;  // Chân reset OLED (-1 nếu không dùng)
        static constexpr int SCREEN_ADDRESS = 0x3C;  // Địa chỉ I2C của OLED
    
        // Chân I2C của ESP32
        static constexpr int SDA = 21;
        static constexpr int SCL = 22;
        unsigned long  previous_millis = 0;   // Biến xử lý thời gian
        unsigned long current_millis = 0;
        const long interval = 2000;  // Thời gian tự quay về màn hình chính
        Adafruit_SSD1306 display;       // Đối tượng điều khiển OLED
    public:
        OLED() : display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET) {}      // Constructor khởi tạo màn hình OLED
        void init() {    // Khởi tạo OLED
            Wire.begin(SDA, SCL); // Khởi tạo I2C
            if (!display.begin(SSD1306_SWITCHCAPVCC, SCREEN_ADDRESS)) {     // Kiểm tra kết nối OLED
                Serial.println(F("OLED không tìm thấy! Kiểm tra kết nối."));
                while (true); // Dừng chương trình nếu lỗi
            }
    
            display.clearDisplay();       // Xóa màn hình
            display.setTextSize(2);        // Cấu hình chữ hiển thị
            display.setTextColor(SSD1306_WHITE);
            display.setCursor(10, 25);      // Hiển thị lời chào
            display.print("Hello!");
            display.display();         // Hiển thị giao diện chính
            show_home();
        }
        // Chuyển về màn hình chính sau khoảng thời gian chờ
        void return_home_screen(){
            current_millis = millis();
            if(current_millis -  previous_millis >= interval){
                 previous_millis = current_millis;
                show_home();
            }
        }
        // Cập nhật thời gian thao tác gần nhất
        void update_previous(){
             previous_millis = millis();
        }
    // Xóa nội dung màn hình OLED
        void clearDisplay() {
            display.clearDisplay();
            display.display();
        }
    // Hiển thị text tại vị trí chỉ định
       void show_text(const String& text, int x, int y) { // Hiển thị text tại vị trí chỉ định
    display.clearDisplay(); // Xóa màn hình cũ
    display.setCursor(x, y); // Đặt vị trí con trỏ
    display.print(text); // In text lên OLED
    display.display(); // Cập nhật màn hình
}
// Hiển thị mật khẩu đang nhập
void show_password(String pw){
    display.clearDisplay(); // Xóa màn hình
    display.setCursor(20, 15); // Hiển thị tiêu đề
    display.print("Password");
    int textWidth = pw.length() * (6 * 2); // Tính độ rộng text để căn giữa
    int xStart = (128 - textWidth) / 2; // Tính vị trí bắt đầu
    display.setCursor(xStart, 40); // Đặt vị trí hiển thị mật khẩu
    display.print(pw); // In mật khẩu
    display.display(); // Cập nhật màn hình
}
// Hiển thị mật khẩu sai
        void show_wrong_password(){
            display.clearDisplay();
            display.setCursor(35, 15);
            display.print("Wrong");
            display.setCursor(20, 35);
            display.print("Password");
            display.display();
        }
        // Hiển thị thẻ hợp lệ
        void show_access_granted(){
            display.clearDisplay();
            display.setCursor(40, 15);
            display.print("Card");
            display.setCursor(20, 35);
            display.print("Accepted");
            display.display();
        }
// Hiển thị thẻ bị từ chối
        void show_access_denied(){
            display.clearDisplay();
            display.setCursor(40, 15);
            display.print("Card");
            display.setCursor(20, 35);
            display.print("Denied");
            display.display();
    
        }
        // Hiển thị thông báo OK
        void display_ok(){
            display.clearDisplay();
            display.setCursor(30, 15);
            display.print("OK!!!!");
            display.display();
        }
        // Hiển thị màn hình chính
        void show_home(){
            display.clearDisplay();
            display.setCursor(10, 15);
            display.print("SmartHome");
            display.setCursor(18, 40);
            display.print("20213877");
            display.display();
        }
        // Hiển thị trạng thái đóng cửa
        void show_close_door(){
            display.clearDisplay();
            display.setCursor(5, 15);
            display.print("Close Door");
            display.display();
        }
        // Hiển thị trạng thái mở cửa
        void show_open_door(){
            display.clearDisplay();
            display.setCursor(5, 15);
            display.print("Open Door");
            display.display();
        }

    };

#endif