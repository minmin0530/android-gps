package com.example.setouchigps

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.osmdroid.tileprovider.tilesource.XYTileSource


class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var startButton: Button
    lateinit var stopButton: Button
    private lateinit var contryList: ListView
//    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        startButton = findViewById(R.id.button4)
        stopButton = findViewById(R.id.button5)
//        mapView = findViewById(R.id.mapview)

        // Listに表示する配列を用意する
        val countries = mutableListOf<String>();

        // Listに配置する
    contryList = findViewById<ListView>(R.id.listview)
    contryList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)

//    val intent = Intent(this, LocationService::class.java)
//    startForegroundService(intent)

        val tileSource = XYTileSource("GSI Map", 5, 21, 256, ".png", arrayOf("https://cyberjapandata.gsi.go.jp/xyz/std/"))
//        mapView.setTileSource(tileSource)
//        mapView.setMultiTouchControls(true)
//
//        val mapController = mapView.controller
        // 地図センター位置
        val locationSensor = LocationSensor(this)
        locationSensor.requestLocationPermission()

//        locationSensor.start()
        locationSensor.onStartCommand(Intent(this, LocationSensor::class.java), 0, 0)
//        stopButton.isEnabled = false
        locationSensor.location.observe(this, Observer {
            countries.add("${locationSensor.getCountA().last()}")
            contryList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)

//            textView.text = "lat = ${it.latitude}\n long = ${it.longitude}"
//            mapController.setZoom(21.0) // ズームレベル 15
//            mapController.setCenter(GeoPoint(it.latitude,  it.longitude))
        })
//
        startButton.setOnClickListener {
            if (!locationSensor.run) {
                locationSensor.onStartCommand(Intent(this, LocationSensor::class.java), 0, 0)
                startButton.isEnabled = false
                stopButton.isEnabled = true

//                locationSensor.location.observe(this, Observer {
//                    textView.text = "lat = ${it.latitude}\n long = ${it.longitude}"
//                    mapController.setZoom(21.0) // ズームレベル 15
//                    mapController.setCenter(GeoPoint(it.latitude,  it.longitude))
//                })

//                val response = sendRequest(
//                    url = "https://setouchi34.com/get_position",
//                    method = "POST",
//                    headers = mapOf(
//                        "Content-Type" to "application/json"
//                    ),
//                    body = """{ "title": "Sample title", "body": "Sample body" }"""
//                )
//                textView.text = response.body
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
