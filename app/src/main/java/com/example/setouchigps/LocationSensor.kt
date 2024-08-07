package com.example.setouchigps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

class LocationSensor(private val activity: Activity): Service() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private var locationCallback: LocationCallback? = null

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    var run: Boolean = false
    var count: Int = 0
    val gpsPositions = mutableListOf<String>()
    fun getCountA(): MutableList<String> { return gpsPositions }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // ここにバックグラウンドで行いたい処理を書く
        if (!run) {
            run = true
            Thread(Runnable {
                //var count = 0
                while (run) {
                    count++
                    if (checkLocationPermission()) {
                        val locationRequest: LocationRequest.Builder =
                            LocationRequest.Builder(5000)
                                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

                        locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                for (location in locationResult.locations) {

                                    gpsPositions.add("lat = ${location.latitude}  long = ${location.longitude}")
                                    _location.postValue(location)

                                }
                            }
                        }

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest.build(),
                            locationCallback as LocationCallback,
                            Looper.getMainLooper()
                        )
                    }




                    try {
                        Thread.sleep(5000) // 1秒間隔でカウントアップ
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }).start()
        }
        return START_NOT_STICKY
    }

//    @SuppressLint("MissingPermission")
//    fun start() {
//        if (checkLocationPermission()) {
//            val locationRequest: LocationRequest.Builder =
//                LocationRequest.Builder(5000)
//                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//
//            locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    for (location in locationResult.locations) {
//                        _location.postValue(location)
//                    }
//                }
//            }
//
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest.build(),
//                locationCallback as LocationCallback,
//                Looper.getMainLooper()
//            )
//
//        }
//        run = true
//    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun stop() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
        run = false
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    override fun onDestroy() {
        super.onDestroy()
        run = false // ループを終了させる
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}