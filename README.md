# SmartBat_Capstone_2022
  The purpose of this project is to allow a Cricket player to get real-time feedback on the user's swing data with out the need of assistance from a coach. The user will be able to view their efficiency, swing speed, and hit location on a user-friendly Android App. 
  
ANDROID APP SUBSYSTEM:
  
  The necessary source files for the android app are located under the "src" folder and include the "build.gradle" and "settings.gradle" files. The Andriod App receives data from our MCU via bluetooth which is then sent to an AWS cloud to be processed by the ML Model. The ML Model will then calcuate the hit location and swing speed. The app will then fetch the calculated results from our AWS Cloud.


CONTROL SUBSYSTEM:
  
POWER SUBSYSTEM:
  
ML SUBSYSTEM:
