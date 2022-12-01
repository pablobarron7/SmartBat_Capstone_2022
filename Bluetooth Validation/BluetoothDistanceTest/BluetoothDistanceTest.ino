#include <SoftwareSerial.h>
SoftwareSerial BTserial(3, 4); // RX | TX

char c = ' ';
int distance = 10;
char total = 0;
 
void setup() 
{
    Serial.begin(9600);
    Serial.println("Arduino is ready");
 
    // HC-05 default serial speed for commincation mode is 9600
    BTserial.begin(9600);  
    BTserial.println("Bluetooth is ready");
}
 
void loop()
{
 
    // Keep reading from HC-05 and send to Arduino Serial Monitor
    if (BTserial.available())
    {  
        c = BTserial.read();
        Serial.print(c);
        if(c == 48){
          BTserial.print("BT connection at: ");
          BTserial.print(distance);  
          BTserial.println(" ft");
          distance = distance + 10;
        }
    }
 
    // Keep reading from Arduino Serial Monitor and send to HC-05 
} 
