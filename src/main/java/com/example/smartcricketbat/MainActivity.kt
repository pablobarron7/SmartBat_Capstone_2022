package com.example.smartcricketbat

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.DataIMU
import com.amplifyframework.datastore.generated.model.MLOutput
import com.ingenieriajhr.blujhr.BluJhr


class MainActivity : AppCompatActivity() {

    /* creating bluetooth object*/
    lateinit var blue: BluJhr
    /* holds list of bluetooth devices*/
    var devicesBluetooth = ArrayList<String>()

    /* variable to communicate with MCU */
    var start = "4"
    var pause =  "1"
    var calibrate = "0"
    var calibResult = ""
    var getBatteryLife = "3"
    var actualBattery = ""
    var data_input = ""
    var batAngle = ""
    var ML_in = "8.5"

    var eff = ""
    var hitLoc = ""
    var amplifyint = 40
    var speedswing = ""
    var nameTXT = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
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
        //val buttonDev = findViewById<Button>(R.id.deviceConn)
        val buttonReceive = findViewById<Button>(R.id.buttonReceive)

        //hit region layout
        val hitLocCL = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.hitlocCL)
        val dashboard1 = findViewById<Button>(R.id.dashboard1)
        //val hitregionTV = findViewById<TextView>(R.id.hitregion)
        val hitIV = findViewById<ImageView>(R.id.hitIV)
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

        //battery life
        val batterylifeBTN = findViewById<Button>(R.id.batterylifeBTN)

        //sending to s3 bucket layout
        val s3sendingCL = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.s3sendingCL)
        val dashboard7 = findViewById<Button>(R.id.dashboard7)
        val enterS3 = findViewById<Button>(R.id.s3sendBTN)
        val nameET = findViewById<EditText>(R.id.enterNameET)


        listDeviceBluetooth.setOnItemClickListener { adapterView, view, i, l ->
            if (devicesBluetooth.isNotEmpty()){
                blue.connect(devicesBluetooth[i])
                blue.setDataLoadFinishedListener(object:BluJhr.ConnectedBluetooth{
                    override fun onConnectState(state: BluJhr.Connected) {
                        when(state){
                            /* statements to show if we establish a connection with device*/
                            BluJhr.Connected.True->{
                                Toast.makeText(applicationContext,"True", Toast.LENGTH_SHORT).show()
                                listDeviceBluetooth.visibility = View.GONE
                                viewConn.visibility = View.VISIBLE
                                /* function to receive data*/
                                //rxReceived()
                            }

                            BluJhr.Connected.Pending->{
                                Toast.makeText(applicationContext,"Pending", Toast.LENGTH_SHORT).show()

                            }

                            BluJhr.Connected.False->{
                                Toast.makeText(applicationContext,"False", Toast.LENGTH_SHORT).show()
                            }

                            BluJhr.Connected.Disconnect->{
                                Toast.makeText(applicationContext,"Disconnect", Toast.LENGTH_SHORT).show()
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
            rxReceived()
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
        buttonCalibration.setOnClickListener {
            blue.bluTx(calibrate)
            Toast.makeText(applicationContext, "Calibration: Wait 2 Min", Toast.LENGTH_SHORT).show()
            rxReceivedCalib()
        }

        /* Paired Devices console*/
        /*
        buttonDev.setOnClickListener {
            listDeviceBluetooth.visibility = View.VISIBLE
            viewConn.visibility = View.GONE
        }
        */
        //TODO!!
        // to fetch MLOutput Model
        buttonReceive.setOnClickListener {
            Amplify.API.query(
                ModelQuery.get(MLOutput::class.java, "68311ff5-1133-424e-ab2c-e4f938e97ab5"),
                { Log.i("MyAmplifyApp", "Query results = ${(it.data as MLOutput).id}") },
                { Log.e("MyAmplifyApp", "Query failed", it) }
            );

            //calling getlocation and getspeed from MLOutput Class
            hitLoc = MLOutput::getLocation.toString()
            speedswing = MLOutput::getSpeed.toString()

            hitLoc = "5"
            speedswing = "127"
            batAngle = "5.23"

            System.out.print("location: $hitLoc ")
            System.out.print("speed: $speedswing")

            Toast.makeText(applicationContext, "Calculating Results...", Toast.LENGTH_SHORT).show()
            //Toast.makeText(applicationContext, "Location: $hitLoc", Toast.LENGTH_SHORT).show()
            //Toast.makeText(applicationContext, "Speed: $speedswing", Toast.LENGTH_SHORT).show()

        }
        //TODO !!
        buttonPredict.setOnClickListener {

            viewConn.visibility = View.GONE
            s3sendingCL.visibility = View.VISIBLE

            //Toast.makeText(applicationContext, "Model created", Toast.LENGTH_SHORT).show()
            //Toast.makeText(applicationContext, "Calculating Results...", Toast.LENGTH_SHORT).show()
        }

        /* button that sends data to s3 bucket*/
        enterS3.setOnClickListener {
            nameTXT = nameET.text.toString()

            val model = DataIMU.builder()
                .inputData(data_input)
                .name(nameTXT)
                .build()

            Amplify.API.mutate(ModelMutation.create(model),
                { Log.i("MyAmplifyApp", "DataIMU with id: ${it.data.id}") },
                { Log.e("MyAmplifyApp", "Create failed", it) }
            )


            /*
            val model2 = MLOutput.builder()
                .location("5")
                .speed("57")
                .build()

            Amplify.API.mutate(ModelMutation.create(model2),
                { Log.i("MyAmplifyApp", "MLOutput with id: ${it.data.id}") },
                { Log.e("MyAmplifyApp", "Create failed", it) }
            )
            */

            Toast.makeText(applicationContext, "Model created", Toast.LENGTH_SHORT).show()
            Toast.makeText(applicationContext, "Calculating Results...", Toast.LENGTH_SHORT).show()
        }

        dashboard7.setOnClickListener {
            s3sendingCL.visibility = View.GONE
            viewConn.visibility = View.VISIBLE
        }

        /* To navigate to Efficiency Page */
        buttonefficiency.setOnClickListener {
            //eff = "50"
            val loc = hitLoc.toInt()
            if (loc >= 1 && loc <= 3) {
                eff = "40"
            }
            else if (loc >= 4 && loc <= 6) {
                eff = "70"
            }
            else if (loc == 7 || loc == 9) {
                eff = "80"
            }
            else if (loc == 8) {
                eff = "100"
            }
            else if (loc >= 10 && loc <= 12) {
                eff = "60"
            }
            else if (loc >= 13 && loc <= 15) {
                eff = "25"
            }
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
            //hitLoc = "15"
            hitLoc = "5"
            speedswing = "127"
            batAngle = "5.23"
            val loc = hitLoc.toInt()
            if (loc >= 1 && loc <= 3) {  /* Bad- Region */
                //hitregionTV.text = "Zone $loc"
                if (loc == 2) {
                    hitIV.setImageResource(R.drawable.hit2)
                    hitadviseTV.setTextSize(28F)
                    hitadviseTV.text = "Bad, move down the bat"
                }

                else if(loc == 3) {
                    hitIV.setImageResource(R.drawable.hit3)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Bad, move down the bat and swing higher"
                }
                else if(loc == 1) {
                    hitIV.setImageResource(R.drawable.hit1)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Bad, move down the bat and swing lower"
                }

            }
            else if (loc >= 4 && loc <= 6) {  /* Ok- Region */
                //hitregionTV.text = "Zone $loc"
                if (loc == 5) {
                    hitIV.setImageResource(R.drawable.hit5)
                    hitadviseTV.setTextSize(28F)
                    hitadviseTV.text = "Ok, move down the bat"
                }

                else if(loc == 6) {
                    hitIV.setImageResource(R.drawable.hit6)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Ok, move down the bat and swing higher"
                }
                else if(loc == 4) {
                    hitIV.setImageResource(R.drawable.hit4)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Ok, move down the bat and swing lower"
                }

            }

            else if (loc >= 7 && loc <= 9) {  /* Perfect Region */
                //hitregionTV.text = "Zone $loc"
                if (loc == 8) {
                    hitIV.setImageResource(R.drawable.hit8)
                    hitadviseTV.text = "Perfect Swing"
                }

                else if(loc == 9) {
                    hitIV.setImageResource(R.drawable.hit9)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Great, but move down the bat and swing higher"
                }
                else if(loc == 7) {
                    hitIV.setImageResource(R.drawable.hit7)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Great, but move down the bat and swing lower"
                }

            }

            else if (loc >= 10 && loc <= 12) {  /* Ok+ Region */
                //hitregionTV.text = "Zone $loc"
                if (loc == 11) {
                    hitIV.setImageResource(R.drawable.hit11)
                    hitadviseTV.setTextSize(28F)
                    hitadviseTV.text = "Ok, move up the bat"
                }

                else if(loc == 12) {
                    hitIV.setImageResource(R.drawable.hit12)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Ok, but move up the bat and swing higher"
                }
                else if(loc == 10) {
                    hitIV.setImageResource(R.drawable.hit10)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Ok, but move up the bat and swing lower"
                }

            }

            else if (loc >= 13 && loc <= 15) {  /* Bad+ Region */
                //hitregionTV.text = "Zone $loc"
                if (loc == 14) {
                    hitIV.setImageResource(R.drawable.hit14)
                    hitadviseTV.setTextSize(28F)
                    hitadviseTV.text = "Bad, move up the bat"
                }

                else if(loc == 15) {
                    hitIV.setImageResource(R.drawable.hit15)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Bad, move up the bat and swing higher"
                }
                else if(loc == 13) {
                    hitIV.setImageResource(R.drawable.hit13)
                    hitadviseTV.setTextSize(22F)
                    hitadviseTV.text = "Bad, move up the bat and swing lower"
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
            //speedswing = "42"
            /*
            val intent = Intent(this@MainActivity, SwingSpeed::class.java)
            intent.putExtra("key3", speedswing)
            startActivity(intent)
             */

            speedTV.text = "$speedswing m/s and $batAngle deg"

            speedCL.visibility = View.VISIBLE
            viewConn.visibility = View.GONE

        }

        /* Navigate from efficiency view back to dashboard*/
        dashboard6.setOnClickListener {
            speedCL.visibility = View.GONE
            viewConn.visibility = View.VISIBLE
        }

        /* To get battery life of device */
        batterylifeBTN.setOnClickListener {
            blue.bluTx(getBatteryLife)
            Toast.makeText(applicationContext, "Getting Battery Percentage", Toast.LENGTH_SHORT).show()
            rxBatteryLife()
        }


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
                //System.out.print(calibResult)
                Toast.makeText(applicationContext, "Status: $calibResult ", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun rxBatteryLife(){
        blue.loadDateRx(object:BluJhr.ReceivedData{
            override fun rxDate(rx: String) {
                actualBattery = rx
                //System.out.print("Battery Life: $actualBattery")
                Toast.makeText(applicationContext, "Battery: $actualBattery", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "No available devices", Toast.LENGTH_SHORT).show()
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}