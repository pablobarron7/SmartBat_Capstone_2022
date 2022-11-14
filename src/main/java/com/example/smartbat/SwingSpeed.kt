package com.example.smartbat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class SwingSpeed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swing_speed)

        val dashboard4 = findViewById<Button>(R.id.dashboard4)
        val speedTV = findViewById<TextView>(R.id.SpeedTV)

        val intent = intent
        val speedSwing = intent.getStringExtra("key3")
        speedTV.text = "$speedSwing mph"

        dashboard4.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}