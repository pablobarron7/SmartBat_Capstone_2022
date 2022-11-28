# SmartBat_Capstone_2022
  The purpose of this project is to allow a Cricket player to get real-time feedback on the user's swing data with out the need of assistance from a coach. The user will be able to view their efficiency, swing speed, and hit location on a user-friendly Android App. 
  
ANDROID APP SUBSYSTEM:
  
  The necessary source files for the android app are located under the "src" folder and include the "build.gradle" and "settings.gradle" files. The Andriod App receives data from our MCU via bluetooth which is then sent to an AWS cloud to be processed by the ML Model. The ML Model will then calcuate the hit location and swing speed. The app will then fetch the calculated results from our AWS Cloud.


CONTROL SUBSYSTEM:
  
  Download the SmartCricketBatOwnCode.ino file, along with the MCU libraries, and use both within Ardunio studio to flash desired ATmega328p microcontroller (make sure  MCU libraries are properly added to Arduino studios directory after download to avoid any errors). Within the IMU Validation Plots folder are the results from several validation tests performed on the MPU-6050 inertial measurement unit, the plots prove the data recieved from the IMU is accurate, within some margin, to the expected results (raw data text files used to create plots are available within folder as well). 
  
POWER SUBSYSTEM:
  
ML SUBSYSTEM:
