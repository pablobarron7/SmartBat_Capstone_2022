//I2Cdev and MPU6050 must be installed as libraries
#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"  //or #include "MPU6050.h" // but motion apps has included
#include "SoftwareSerial.h"

//Arduino Wire librabry is required if I2Cdev I2CDEV_ARDUNIO_WIRE implentation is used
#if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
#include "Wire.h"
#endif

MPU6050 mpu;

//default sensitivty of gyro = 250 deg, accel = +- 2 g
/* power, ground, SDA, SCL, and Interrupt pin will be used, must reassign the int pin to digital pin 2
   because our microcontroller has no INT pin
*/

// "OUTPUT_BINARY_ACCELGYRO" sends all 6 axes of data as 16-bit
// binary, one right after the other (ex: ). This is quickest method, and easy to parse
//#define OUTPUT_BINARY

// uncomment "OUTPUT_READABLE_EULER" if you want to see Euler angles
// (in degrees) calculated from the quaternions coming from the FIFO.
// Note that Euler angles suffer from gimbal lock (for more info, see
// http://en.wikipedia.org/wiki/Gimbal_lock)
#define OUTPUT_READABLE_EULER

// uncomment "OUTPUT_READABLE_REALACCEL" if you want to see acceleration
// components with gravity removed. This acceleration reference frame is
// not compensated for orientation, so +X is always +X according to the
// sensor, just without the effects of gravity. If you want acceleration
// compensated for orientation, us OUTPUT_READABLE_WORLDACCEL instead.
#define OUTPUT_READABLE_REALACCEL

// uncomment "OUTPUT_READABLE_WORLDACCEL" if you want to see acceleration
// components with gravity removed and adjusted for the world frame of
// reference (yaw is relative to initial orientation, since no magnetometer
// is present in this case). Could be quite handy in some cases.
//#define OUTPUT_READABLE_WORLDACCEL

#define LED_PIN 13
bool blinkState = false;

//Bluetooth Module Pins
#define rxPin 3
#define txPin 4

//defining functions
void calibration();
void meansensors();

// MPU control/status vars
bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer

//Binary Output of IMU Vars
int16_t ax, ay, az;
int16_t gx, gy, gz;

//for auto-calibration/offset caculation
bool startUpFinished = true; //CHANGE TO TRUE ONCE THE APP IS ALL SET UP AND MAKE A WAY TO RECIEVE THE INPUT OF THE BUTTON
int resumeDump = 0;
int receivedData = 0; //vaiable for data sent from app by user to tell MCU to start or stop, etc

//Change this 3 variables if you want to fine tune the skecth to your needs.
int buffersize = 500;     //Amount of readings used to average, make it higher to get more precision but sketch will be slower  (default:1000)
int acel_deadzone = 8;     //Acelerometer error allowed, make it lower to get more precision, but sketch may not converge  (default:8)
int giro_deadzone = 1;     //Giro error allowed, make it lower to get more precision, but sketch may not converge  (default:1)

//int16_t ax, ay, az,gx, gy, gz;

int mean_ax,mean_ay,mean_az,mean_gx,mean_gy,mean_gz,state=0;
int ax_offset,ay_offset,az_offset,gx_offset,gy_offset,gz_offset;

//Battery Percentage
float battPercent = 100.0;

// orientation/motion vars
Quaternion q;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
float euler[3];         // [psi, theta, phi]    Euler angle container

//INTERRUPT Detection Routine
volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
  mpuInterrupt = true;
}
///////////////////////////////////  BLUETOOTH  ///////////////////////////////////
SoftwareSerial SCBApp(3, 4); // connect to smart cricket bat app (RX | TX)
///////////////////////////////////   SETUP   ////////////////////////////////////
void setup() {
  //must set the int pin for our MCU
  attachInterrupt(digitalPinToInterrupt(2), dmpDataReady, CHANGE);
  // join I2C bus (I2Cdev library doesn't do this automatically)
  #if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
    Wire.begin();
    TWBR = 24; // 400kHz/24 I2C clock (200kHz if CPU is 8MHz)
  #elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
    Fastwire::setup(400, true);
  #endif

  // initialize serial communication
  // (115200 chosen because it is required for Teapot Demo output, but it's
  // really up to you depending on your project)
  Serial.begin(9600);
  SCBApp.begin(9600); //has to be 9600 bc software serial is unstable above it
  while (!Serial); // wait for Leonardo enumeration, others continue immediately

  // initialize device
  Serial.println(F("Initializing I2C devices..."));
  mpu.initialize();

  // verify connection
  Serial.println(F("Testing device connections..."));
  Serial.println(mpu.testConnection() ? F("MPU6050 connection successful") : F("MPU6050 connection failed"));

  // wait for ready
  Serial.println(F("\nSend any character to begin DMP programming and demo: "));
  while ((Serial.available() && Serial.read()) && (SCBApp.available() && SCBApp.read())); // empty buffer
  while (!Serial.available() && !SCBApp.available());                 // wait for data
  while ((Serial.available() && Serial.read()) && (SCBApp.available() && SCBApp.read())); // empty buffer again

  // load and configure the DMP
  Serial.println(F("Initializing DMP..."));
  devStatus = mpu.dmpInitialize();

  //change sensitivty of imu to be +-8 g 
  mpu.setFullScaleAccelRange(MPU6050_ACCEL_FS_8);
  // supply your own gyro offsets here, scaled for min sensitivity
  //THESE ARE BASIC PLACE HOLDER VALUES FOR THE OFFSETS, I WOULD STILL RECOMMEND USING THE CALIBRATE SENSORS FEATURE
  mpu.setXGyroOffset(-84);  //
  mpu.setYGyroOffset(84);   //
  mpu.setZGyroOffset(5);  //
  mpu.setXAccelOffset(-3947);   //-6824
  mpu.setYAccelOffset(-4303); //-645
  mpu.setZAccelOffset(10496); // 2315 will need to switch z and y i think for the real bat 

  // make sure it worked (returns 0 if so)
  if (devStatus == 0) {
    // turn on the DMP, now that it's ready
    Serial.println(F("Enabling DMP..."));
    mpu.setDMPEnabled(true);

    // enable Arduino interrupt detection
    Serial.println(F("Enabling interrupt detection (MCU external interrupt digital pin 2)...")); //CHANGED
    attachInterrupt(digitalPinToInterrupt(2), dmpDataReady, RISING); //changed first parameter from 0 to digitalPinToInterrupt(2)
    mpuIntStatus = mpu.getIntStatus();

    // set our DMP Ready flag so the main loop() function knows it's okay to use it
    Serial.println(F("DMP ready! Waiting for first interrupt..."));
    dmpReady = true;

    // get expected DMP packet size for later comparison
    packetSize = mpu.dmpGetFIFOPacketSize();
  }
  else {
    // ERROR!
    // 1 = initial memory load failed
    // 2 = DMP configuration updates failed
    // (if it's going to break, usually the code will be 1)
    Serial.print(F("DMP Initialization failed (code "));
    Serial.print(devStatus);
    Serial.println(F(")"));
  }

  // configure LED for output
  pinMode(LED_PIN, OUTPUT);

}

///////////////////////////////////   MAIN LOOP   ////////////////////////////////////
void loop() {
  /* receivedData = Serial.read();
   * if(receivedData == 48){  //if recieve (0) than do the calibration
   *    startUpFinished = false;
   * } 
   * //maybe make if statement for (1) that enables that actual function of the sensor(recording swing data and giving to app) for like 5 secs or smth
   */
   delay(100);    //helps with the data sent being all weird
   startUpFinished = false;
   if (SCBApp.available()){  //if bluetooth is avilable read any input from user
      receivedData = SCBApp.read(); //use to end and resart loop whenever
   }
   else{
      receivedData = Serial.read();
   }
   if(receivedData == 48){  //if input 0, do calibration state
      //function call for calibration
      calibrateDevice();
      if(startUpFinished == true){
          SCBApp.println("Calibration Complete"); //Calibration Complete, 53
      }
      else{
          SCBApp.println("Calibration Failed");   //Calibration Failed, 54
      }
   }
   else if(receivedData == 49){  //if input 1, exit main loop, pause IMU dump
      while(resumeDump = SCBApp.read()){
        if(resumeDump == 52){   //resume = 4
          break;
        }
        else if (Serial.read() == 52){
          break;
        }
        else if (resumeDump == 51){ //print batt %
          printBattPercent();
        }
        else if (resumeDump == 48){ //calibrate
          calibrateDevice();
          if(startUpFinished == true){
              SCBApp.println("Calibration Complete"); //Calibration Complete, 53
          }
          else{
              SCBApp.println("Calibration Failed");   //Calibration Failed, 54
          }
        }
        else{
          delay(50);
        }
      }
   }
   else if(receivedData == 51){ //sending 3 give battery%
      printBattPercent();
   }
   else if(receivedData == 50){
      exit(0);
   }
   
 
  // if programming failed, don't try to do anything
  if (!dmpReady) return;

  // reset interrupt flag and get INT_STATUS byte
  mpuInterrupt = false;
  mpuIntStatus = mpu.getIntStatus();

  // get current FIFO count
  fifoCount = mpu.getFIFOCount();

  // check for overflow (this should never happen unless our code is too inefficient, besides when it does it right after setting inital offsets)
  
  if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
    // reset so we can continue cleanly
    mpu.resetFIFO();
    //Serial.println(F("FIFO Reset!"));  //will mostly be clearing values found during offset calculations, so we can start fresh
  }
  else if (mpuIntStatus & 0x02) {     // otherwise, check for DMP data ready interrupt (this should happen frequently)
    // wait for correct available data length, should be a VERY short wait
    while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

    // read a packet from FIFO
    mpu.getFIFOBytes(fifoBuffer, packetSize);

    // track FIFO count here in case there is > 1 packet available
    // (this lets us immediately read more without waiting for an interrupt)
    fifoCount -= packetSize;

#ifdef OUTPUT_BINARY
    mpu.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
    Serial.write((uint8_t)(ax >> 8)); Serial.write((uint8_t)(ax & 0xFF));
    Serial.write((uint8_t)(ay >> 8)); Serial.write((uint8_t)(ay & 0xFF));
    Serial.write((uint8_t)(az >> 8)); Serial.write((uint8_t)(az & 0xFF));
    Serial.write((uint8_t)(gx >> 8)); Serial.write((uint8_t)(gx & 0xFF));
    Serial.write((uint8_t)(gy >> 8)); Serial.write((uint8_t)(gy & 0xFF));
    Serial.write((uint8_t)(gz >> 8)); Serial.write((uint8_t)(gz & 0xFF));
#endif

#ifdef OUTPUT_READABLE_EULER
    // display Euler angles in degrees
    mpu.dmpGetQuaternion(&q, fifoBuffer);
    mpu.dmpGetEuler(euler, &q);
    Serial.print(millis()); //print current time
    Serial.print("\t");
    Serial.print("euler\t");
    Serial.print(euler[0] * 180 / M_PI);  //multiply the euler array by 180/pi to get it in degrees
    Serial.print("\t");
    Serial.print(euler[1] * 180 / M_PI);
    Serial.print("\t");
    Serial.print(euler[2] * 180 / M_PI);
    Serial.print("\t");

    //////Bluetooth Message////
    SCBApp.print(millis()); //print current time
    SCBApp.print("\t");
    SCBApp.print(euler[0] * 180 / M_PI);  //multiply the euler array by 180/pi to get it in degrees
    SCBApp.print("\t");
    SCBApp.print(euler[1] * 180 / M_PI);
    SCBApp.print("\t");
    SCBApp.print(euler[2] * 180 / M_PI);
    SCBApp.print("\t");
#endif

#ifdef OUTPUT_READABLE_REALACCEL
    // display real acceleration, adjusted to remove gravity
    mpu.dmpGetQuaternion(&q, fifoBuffer);
    mpu.dmpGetAccel(&aa, fifoBuffer);
    mpu.dmpGetGravity(&gravity, &q);
    mpu.dmpGetLinearAccel(&aaReal, &aa, &gravity);
    Serial.print("areal\t");
    Serial.print(aaReal.x);
    Serial.print("\t");
    Serial.print(aaReal.y);
    Serial.print("\t");
    Serial.println(aaReal.z);

    //////Bluetooth Message/////
    SCBApp.print("\t");
    SCBApp.print(aaReal.x);
    SCBApp.print("\t");
    SCBApp.print(aaReal.y);
    SCBApp.print("\t");
    SCBApp.println(aaReal.z);
#endif

//originally just for validation, but i think it gives better data overall
#ifdef OUTPUT_READABLE_WORLDACCEL
    // display initial world-frame acceleration, adjusted to remove gravity
    // and rotated based on known orientation from quaternion
    mpu.dmpGetQuaternion(&q, fifoBuffer);
    mpu.dmpGetAccel(&aa, fifoBuffer);
    mpu.dmpGetGravity(&gravity, &q);
    mpu.dmpGetLinearAccel(&aaReal, &aa, &gravity);
    mpu.dmpGetLinearAccelInWorld(&aaWorld, &aaReal, &q);
    Serial.print("aworld\t");
    Serial.print(aaWorld.x);
    Serial.print("\t");
    Serial.print(aaWorld.y);
    Serial.print("\t");
    Serial.println(aaWorld.z);

    //////Bluetooth Message//////
    SCBApp.print("\t");
    SCBApp.print(aaWorld.x);
    SCBApp.print("\t");
    SCBApp.print(aaWorld.y);
    SCBApp.print("\t");
    SCBApp.println(aaWorld.z);
#endif

    // blink LED to indicate activity
    blinkState = !blinkState;
    digitalWrite(LED_PIN, blinkState);
  }
  
}


///////////////////////////////////   FUNCTIONS   ////////////////////////////////////
void meansensors(){   //gets mean values of all 6 axes
  long i=0,buff_ax=0,buff_ay=0,buff_az=0,buff_gx=0,buff_gy=0,buff_gz=0;

  while (i<(buffersize+101)){
    // read raw accel/gyro measurements from device
    mpu.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
    
    if (i>100 && i<=(buffersize+100)){ //First 100 measures are discarded
      buff_ax=buff_ax+ax;
      buff_ay=buff_ay+ay;
      buff_az=buff_az+az;
      buff_gx=buff_gx+gx;
      buff_gy=buff_gy+gy;
      buff_gz=buff_gz+gz;
    }
    if (i==(buffersize+100)){
      mean_ax=buff_ax/buffersize;
      mean_ay=buff_ay/buffersize;
      mean_az=buff_az/buffersize;
      mean_gx=buff_gx/buffersize;
      mean_gy=buff_gy/buffersize;
      mean_gz=buff_gz/buffersize;
    }
    i++;
    delay(2); //Needed so we don't get repeated measures
  }
}

void calibration(){   //used to get the Gyro and Accel offsets using mean value at rest
  ax_offset=-mean_ax/8; //take the mean values of the IMU at start up, and divide by the IMUs sensitvity (+- 8gs in this case)
  ay_offset=-mean_ay/8;
  az_offset=(16384-mean_az)/8;  //the 16384 is compensating for the gravity

  gx_offset=-mean_gx/4;
  gy_offset=-mean_gy/4;
  gz_offset=-mean_gz/4;
  while (1){
    int ready=0;
    mpu.setXAccelOffset(ax_offset);   //set current value for offsets in, to see if the current offsets are good, or if need to keep going
    mpu.setYAccelOffset(ay_offset);
    mpu.setZAccelOffset(az_offset);

    mpu.setXGyroOffset(gx_offset);
    mpu.setYGyroOffset(gy_offset);
    mpu.setZGyroOffset(gz_offset);

    meansensors();
    Serial.println("...");  //let user know something is happening, not stuck in a loop

    //deadzone is equal to sensitivy of IMU device
    if (abs(mean_ax)<=acel_deadzone) ready++;   //if mean of axis is less than deadzone, then that offset is accurate and ready to implement in main loop
    else  ax_offset=ax_offset-mean_ax/acel_deadzone;  //else like before we devide mean by deadzone until it is less than 
                                                        // ( if mean is 64 then offset whould be 8 bc offset is in terms of sensitivity)
    if (abs(mean_ay)<=acel_deadzone) ready++; 
    else  ay_offset=ay_offset-mean_ay/acel_deadzone; 

    if (abs(16384-mean_az)<=acel_deadzone) ready++; 
    else  az_offset=az_offset+(16384-mean_az)/acel_deadzone; 

    if (abs(mean_gx)<=giro_deadzone)  ready++; 
    else   gx_offset=gx_offset-mean_gx/(giro_deadzone+1); 

    if (abs(mean_gy)<=giro_deadzone) { ready++; }
    else   gy_offset=gy_offset-mean_gy/(giro_deadzone+1); 

    if (abs(mean_gz)<=giro_deadzone){ ready++; }
    else   gz_offset=gz_offset-mean_gz/(giro_deadzone+1); 

    if (ready==6)  break;    //once all 6 axes are ready to return, we return to main loop
  }
}

void printBattPercent(){
  int sensorValue = analogRead(A0); //read the A0 pin value
  float voltage = sensorValue * (5.00 / 1023.00) * 2; //convert the value to a true voltage.
  float oldBattPercent = battPercent;
  battPercent = (voltage/4.2)*100;
  if(battPercent > 100.0){
    battPercent = 100.0;
  }
  else if(oldBattPercent < battPercent){
    battPercent = oldBattPercent;
  }  
  SCBApp.print(battPercent); //print the voltage to LCD
  SCBApp.println(" %");
  /*Serial.print(battPercent);
  Serial.println(" %");
  Serial.println(voltage);
  Serial.println(sensorValue);*/
  if (voltage < 3.3){ //set the voltage considered low battery here
    SCBApp.println("Low Battery");
  }
}

void calibrateDevice(){
  if (state==0){  //first, do state0 where the sensor data is read and averaged
      Serial.println("\nReading sensors for first time...");
      meansensors();
      state++;
      delay(500);
    }
    if (state==1) { //state1, is then done next, and it calculates the offsets needed for all the IMU values to be equal to 0
      Serial.println("\nCalibrating Bat Sensor...");
      calibration();
      state++;
      delay(500);
    }
    if (state==2) { //state2, set found values from state1 to the offsets
      meansensors();
      Serial.println("\nFINISHED CALIBRATING BAT SENSOR!");
      Serial.print("\nSensor readings with offsets:\t");
      Serial.print(mean_ax); 
      Serial.print("\t");
      Serial.print(mean_ay); 
      Serial.print("\t");
      Serial.print(mean_az); 
      Serial.print("\t");
      Serial.print(mean_gx); 
      Serial.print("\t");
      Serial.print(mean_gy); 
      Serial.print("\t");
      Serial.println(mean_gz);
      Serial.print("Your offsets:\t");
      Serial.print(ax_offset); 
      Serial.print("\t");
      Serial.print(ay_offset); 
      Serial.print("\t");
      Serial.print(az_offset); 
      Serial.print("\t");
      Serial.print(gx_offset); 
      Serial.print("\t");
      Serial.print(gy_offset); 
      Serial.print("\t");
      Serial.println(gz_offset); 
      Serial.println("\nData is printed as: acelX acelY acelZ giroX giroY giroZ");
      startUpFinished = true;
      mpu.setXAccelOffset(ax_offset);
      mpu.setYAccelOffset(ay_offset);
      mpu.setZAccelOffset(az_offset);
      mpu.setXGyroOffset(gx_offset); 
      mpu.setYGyroOffset(gy_offset);
      mpu.setZGyroOffset(gz_offset); 
    }
}
