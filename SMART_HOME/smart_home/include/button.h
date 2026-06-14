#ifndef BUTTON_H
#define BUTTON_H
#include <Arduino.h>
#include <vector>

extern int numButtons;
extern std::vector<int> buttonPins;
extern std::vector<bool> buttonStates;
extern std::vector<bool> lastButtonStates;
extern std::vector<unsigned long> lastDebounceTime;
extern const unsigned long debounceDelay;

void buttonBegin();
int scanPressedButton();


#endif