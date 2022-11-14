package com.example.smartbat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HitLocation : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hit_location)

        val dashboard2 = findViewById<Button>(R.id.dashboard2)
        val hitTV = findViewById<TextView>(R.id.HitTV)
        val adviceTV = findViewById<TextView>(R.id.adviseTV)

        /* get location intent from main activity*/
        val intent = intent
        val location = intent.getStringExtra("key2")
        val loc = location.toString().toInt()
        if (loc >= 1 && loc <= 3) {  /* Bad- Region */
            hitTV.text = "Zone $loc"
            if (loc == 2) {
                adviceTV.text = "Bad, move down the bat"
            }

            else if(loc == 3) {
                adviceTV.text = "Bad, move down the bat and swing higher"
            }
            else if(loc == 1) {
                adviceTV.text = "Bad, move down the bat and swing lower"
            }

        }
        else if (loc >= 4 && loc <= 6) {  /* Ok- Region */
            hitTV.text = "Zone $loc"
            if (loc == 5) {
                adviceTV.text = "Ok, move down the bat"
            }

            else if(loc == 6) {
                adviceTV.text = "Ok, move down the bat and swing higher"
            }
            else if(loc == 4) {
                adviceTV.text = "Ok, move down the bat and swing lower"
            }

        }

        else if (loc >= 7 && loc <= 9) {  /* Perfect Region */
            hitTV.text = "Zone $loc"
            if (loc == 8) {
                adviceTV.text = "Perfect Swing"
            }

            else if(loc == 9) {
                adviceTV.text = "Great, but move down the bat and swing higher"
            }
            else if(loc == 7) {
                adviceTV.text = "Great, but move down the bat and swing lower"
            }

        }

        else if (loc >= 10 && loc <= 12) {  /* Ok+ Region */
            hitTV.text = "Zone $loc"
            if (loc == 11) {
                adviceTV.text = "Ok, move up the bat"
            }

            else if(loc == 12) {
                adviceTV.text = "Ok, but move up the bat and swing higher"
            }
            else if(loc == 10) {
                adviceTV.text = "Ok, but move up the bat and swing lower"
            }

        }

        else if (loc >= 13 && loc <= 15) {  /* Bad+ Region */
            hitTV.text = "Zone $loc"
            if (loc == 14) {
                adviceTV.text = "Bad, move up the bat"
            }

            else if(loc == 15) {
                adviceTV.text = "Ok, but move up the bat and swing higher"
            }
            else if(loc == 13) {
                adviceTV.text = "Ok, but move up the bat and swing lower"
            }

        }


        dashboard2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}