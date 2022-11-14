package com.example.smartbat


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Amplify
import com.ingenieriajhr.blujhr.BluJhr
import java.nio.FloatBuffer

class MainActivity : AppCompatActivity() {

    /* creating bluetooth object*/
    lateinit var blue: BluJhr
    /* holds list of bluetooth devices*/
    var devicesBluetooth = ArrayList<String>()

    /* variable to communicate with MCU */
    var start = "4"
    var pause =  "1"
    val calibrate = "0"
    var calibResult = ""
    val batteryLife = "7"
    var data_input = ""
    var ML_in = "8.5"

    var eff = ""
    var hitLoc = ""
    var amplifyint = 40
    var speedswing = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* initializing bluetooth object */
        blue = BluJhr(this )
        blue.onBluetooth()

        val listDeviceBluetooth = findViewById<ListView>(R.id.listDeviceBluetooth)
        val viewConn = findViewById<LinearLayout>(R.id.viewConn)
        val buttonStart = findViewById<Button>(R.id.buttonSend)
        val buttonDis = findViewById<Button>(R.id.buttonDis)
        val buttonPredict = findViewById<Button>(R.id.buttonPredict)
        val buttonefficiency = findViewById<Button>(R.id.buttonEfficiency)
        val buttonLocation = findViewById<Button>(R.id.buttonLocation)
        val buttonSpeed = findViewById<Button>(R.id.buttonSwingSpeed)
        val buttonCalibration = findViewById<Button>(R.id.buttonCalibration)
        val buttonPause = findViewById<Button>(R.id.buttonPause)
        val buttonDev = findViewById<Button>(R.id.deviceConn)


        //hit region layout
        val hitLocCL = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.hitlocCL)
        val dashboard1 = findViewById<Button>(R.id.dashboard1)
        val hitregionTV = findViewById<TextView>(R.id.hitregion)
        val hitadviseTV = findViewById<TextView>(R.id.hitadvise)

        //efficiency layout
        val effCL = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.efficiencyCL)
        val dashboard5 = findViewById<Button>(R.id.dashboard5)
        val effPB = findViewById<ProgressBar>(R.id.efficiencyPB)
        val effTV = findViewById<TextView>(R.id.efficiencyTV)

        //speed layout
        val speedCL = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.speedCL)
        val dashboard6 = findViewById<Button>(R.id.dashboard6)
        val speedTV = findViewById<TextView>(R.id.swingspeedTV)


        listDeviceBluetooth.setOnItemClickListener { adapterView, view, i, l ->
            if (devicesBluetooth.isNotEmpty()){
                blue.connect(devicesBluetooth[i])
                blue.setDataLoadFinishedListener(object:BluJhr.ConnectedBluetooth{
                    override fun onConnectState(state: BluJhr.Connected) {
                        when(state){
                            /* statements to show if we establish a connection with device*/
                            BluJhr.Connected.True->{
                                Toast.makeText(applicationContext,"True",Toast.LENGTH_SHORT).show()
                                listDeviceBluetooth.visibility = View.GONE
                                viewConn.visibility = View.VISIBLE
                                /* function to receive data*/
                                rxReceived()
                            }

                            BluJhr.Connected.Pending->{
                                Toast.makeText(applicationContext,"Pending",Toast.LENGTH_SHORT).show()

                            }

                            BluJhr.Connected.False->{
                                Toast.makeText(applicationContext,"False",Toast.LENGTH_SHORT).show()
                            }

                            BluJhr.Connected.Disconnect->{
                                Toast.makeText(applicationContext,"Disconnect",Toast.LENGTH_SHORT).show()
                                listDeviceBluetooth.visibility = View.VISIBLE
                                viewConn.visibility = View.GONE
                            }

                        }
                    }
                })
            }

        }


        /* start button to begin data receiving*/
        buttonStart.setOnClickListener {
            data_input = "" // to clear any other data stored in variable
            blue.bluTx(start)
            Toast.makeText(applicationContext, "Started Data Receiving", Toast.LENGTH_SHORT).show()
            //rxReceived()
        }
        /* button to disconnect from device */
        buttonDis.setOnClickListener {
            blue.closeConnection()
            Toast.makeText(applicationContext, "Disconnected", Toast.LENGTH_SHORT).show()
        }

        /* Button to pause data sending and collection*/
        buttonPause.setOnClickListener {
            blue.bluTx(pause)
            Toast.makeText(applicationContext, "Paused Data Collection", Toast.LENGTH_SHORT).show()
        }

        /* Button to begin intial calibration*/
        //TODO !!!
        buttonCalibration.setOnClickListener {
            blue.bluTx(calibrate)
            Toast.makeText(applicationContext, "Calibration: Wait 2 Min", Toast.LENGTH_SHORT).show()
            rxReceivedCalib()
        }

        /* Paired Devices console*/
        buttonDev.setOnClickListener {
            listDeviceBluetooth.visibility = View.VISIBLE
            viewConn.visibility = View.GONE
        }

        //TODO !!
        buttonPredict.setOnClickListener {
            /*
            try {
                Amplify.configure(applicationContext)
                Log.i("SmartBatApp", "Initialized Amplify")
            } catch (error: AmplifyException) {
                Log.e("SmartBatApp", "Could not initialize Amplify", error)
            }
             */
            Toast.makeText(applicationContext, "Amplify Initialized", Toast.LENGTH_SHORT).show()
        }

        /* To navigate to Efficiency Page */
        buttonefficiency.setOnClickListener {
            eff = "50"
            /*
            val intent = Intent(this@MainActivity, Efficiency::class.java)
            intent.putExtra("key1",eff)  // send effiency result to efficiency activity
            startActivity(intent)
             */
            effPB.progress = eff.toInt()
            effTV.text = "$eff %"

            viewConn.visibility = View.GONE
            effCL.visibility = View.VISIBLE

        }

        /* Navigate from efficiency view back to dashboard*/
        dashboard5.setOnClickListener {
            viewConn.visibility = View.VISIBLE
            effCL.visibility = View.GONE
        }

        /* Button to Navigate to Hit Location Page*/
        buttonLocation.setOnClickListener {
            hitLoc = "9"
            /*
            val intent = Intent(this@MainActivity, HitLocation::class.java)
            intent.putExtra("key2",hitLoc)
            startActivity(intent)
             */
            val loc = hitLoc.toInt()
            if (loc >= 1 && loc <= 3) {  /* Bad- Region */
                hitregionTV.text = "Zone $loc"
                if (loc == 2) {
                    hitadviseTV.text = "Bad, move down the bat"
                }

                else if(loc == 3) {
                    hitadviseTV.text = "Bad, move down the bat and swing higher"
                }
                else if(loc == 1) {
                    hitadviseTV.text = "Bad, move down the bat and swing lower"
                }

            }
            else if (loc >= 4 && loc <= 6) {  /* Ok- Region */
                hitregionTV.text = "Zone $loc"
                if (loc == 5) {
                    hitadviseTV.text = "Ok, move down the bat"
                }

                else if(loc == 6) {
                    hitadviseTV.text = "Ok, move down the bat and swing higher"
                }
                else if(loc == 4) {
                    hitadviseTV.text = "Ok, move down the bat and swing lower"
                }

            }

            else if (loc >= 7 && loc <= 9) {  /* Perfect Region */
                hitregionTV.text = "Zone $loc"
                if (loc == 8) {
                    hitadviseTV.text = "Perfect Swing"
                }

                else if(loc == 9) {
                    hitadviseTV.text = "Great, but move down the bat and swing higher"
                }
                else if(loc == 7) {
                    hitadviseTV.text = "Great, but move down the bat and swing lower"
                }

            }

            else if (loc >= 10 && loc <= 12) {  /* Ok+ Region */
                hitregionTV.text = "Zone $loc"
                if (loc == 11) {
                    hitadviseTV.text = "Ok, move up the bat"
                }

                else if(loc == 12) {
                    hitadviseTV.text = "Ok, but move up the bat and swing higher"
                }
                else if(loc == 10) {
                    hitadviseTV.text = "Ok, but move up the bat and swing lower"
                }

            }

            else if (loc >= 13 && loc <= 15) {  /* Bad+ Region */
                hitregionTV.text = "Zone $loc"
                if (loc == 14) {
                    hitadviseTV.text = "Bad, move up the bat"
                }

                else if(loc == 15) {
                    hitadviseTV.text = "Ok, but move up the bat and swing higher"
                }
                else if(loc == 13) {
                    hitadviseTV.text = "Ok, but move up the bat and swing lower"
                }

            }
            viewConn.visibility = View.GONE
            hitLocCL.visibility = View.VISIBLE

        }

        /* Navigate from efficiency view back to dashboard*/
        dashboard1.setOnClickListener {
            viewConn.visibility = View.VISIBLE
            hitLocCL.visibility = View.GONE
        }

        /* Button to Navigate to Speed Location Page*/
        buttonSpeed.setOnClickListener {
            speedswing = "42"
            /*
            val intent = Intent(this@MainActivity, SwingSpeed::class.java)
            intent.putExtra("key3", speedswing)
            startActivity(intent)
             */

            speedTV.text = "$speedswing mph"

            speedCL.visibility = View.VISIBLE
            viewConn.visibility = View.GONE

        }

        /* Navigate from efficiency view back to dashboard*/
        dashboard6.setOnClickListener {
            speedCL.visibility = View.GONE
            viewConn.visibility = View.VISIBLE
        }

        //AWS Amplify Intialization

        /*
        if (amplifyint == 40){
            try {
                Amplify.configure(applicationContext)
                Log.i("SmartBatApp", "Initialized Amplify")
            } catch (error: AmplifyException) {
                Log.e("SmartBatApp", "Could not initialize Amplify", error)
            }
            amplifyint = 0
        }
         */
    }

    /* display message received from MCU*/
    private fun rxReceived() {
        //val consola = findViewById<TextView>(R.id.consola)
        blue.loadDateRx(object:BluJhr.ReceivedData{
            override fun rxDate(rx: String) {
                //consola.text = consola.text.toString()+rx+"\n"
                data_input = data_input + rx + "\n"
                System.out.print(data_input)
            }
        })
    }

    private fun rxReceivedCalib() {
        //val consola = findViewById<TextView>(R.id.consola)
        blue.loadDateRx(object:BluJhr.ReceivedData{
            override fun rxDate(rx: String) {
                //consola.text = consola.text.toString()+rx+"\n"
                calibResult = rx
                System.out.print(calibResult)
                if (calibResult == "5") {
                    Toast.makeText(applicationContext, "Calibration Successful", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }



    /* asking permission to search for bluetooth devices for android 12 and up  */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (blue.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show()
            blue.initializeBluetooth()
        }else{
            /* this is so older andriod versions can have permission without asking*/
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth()
            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /* to display a window asking user for permission*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val listDeviceBluetooth = findViewById<ListView>(R.id.listDeviceBluetooth)
        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth()
        }else{
            /* if bluetooth is on then it will show a list of paired devices to be connected*/
            if (requestCode == 100){
                devicesBluetooth = blue.deviceBluetooth()
                if (devicesBluetooth.isNotEmpty()){
                    val adapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,devicesBluetooth)
                    listDeviceBluetooth.adapter = adapter
                }else{
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show()
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}