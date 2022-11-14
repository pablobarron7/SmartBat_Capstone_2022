package com.example.smartbat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class Efficiency : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_efficiency)

        val dashboard3 = findViewById<Button>(R.id.dashboard3)

        /* to get data from main activity*/
        val intent = intent
        val efficiency = intent.getStringExtra("key1")

        val efficiency_progress = findViewById<ProgressBar>(R.id.efficiencyProgress)
        val efficiencyTV = findViewById<TextView>(R.id.efficiencyTextView)
        efficiency_progress.progress = efficiency.toString().toInt()
        efficiencyTV.text= "$efficiency%"

        dashboard3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }


}