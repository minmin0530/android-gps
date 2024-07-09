package com.example.setouchigps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var startButton: Button
    lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textview)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        val locationSensor = LocationSensor(this)
        locationSensor.requestLocationPermission()

        stopButton.isEnabled = false

        locationSensor.location.observe(this, Observer {
            textView.text = "lat = ${it.latitude}\n long = ${it.longitude}"
        })

        startButton.setOnClickListener {
            if (!locationSensor.run) {
                locationSensor.start()
                startButton.isEnabled = false
                stopButton.isEnabled = true
            }
        }

        stopButton.setOnClickListener {
            if (locationSensor.run) {
                locationSensor.stop()
                startButton.isEnabled = true
                stopButton.isEnabled = false
            }
        }

    }
}
