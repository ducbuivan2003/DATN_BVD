#ifndef BUTTON_H
#define BUTTON_H
#include <Arduino.h>
#include <vector>

extern int numButtons;
extern std::vector<int> buttonPins;
extern std::vector<bool> buttonStates;
extern std::vector<bool> lastButtonStates;
extern std::vector<unsigned long> last_debounce_time;
extern const unsigned long debounce_delay;

void button_begin();
int scan_pressed_button();


#endif