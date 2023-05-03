package com.example.beespotter

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint

const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    private lateinit var containerView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestLocationPermission()
        verifyLocation()
        containerView = findViewById(R.id.fragmentContainer)

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume(): ${currentUserLocation.toString()}")
    }

    @SuppressLint("MissingPermission")  // There are checks in place already.
    fun verifyLocation() {
        // check for permissions and existance of maps, locations, etc.
//        if (map == null) { return } // TODO This might be needed later
        if (fusedLocationProvider == null) { return }
        if (!locationPermissionGranted) {
            currentUserLocation = null
            showSnackbar(getString(R.string.user_message_location_permission_needed))
            return
        }

        // all tests pass
        fusedLocationProvider?.lastLocation?.addOnCompleteListener(this) {
            locationRequestTask ->
            val location = locationRequestTask.result
            if (location != null) {
                Log.d(TAG, "User located at $location")
                val userLocation = GeoPoint(location.latitude, location.longitude)
                currentUserLocation = userLocation
            } else {
                Log.d(TAG, "User NOT located at $location")

                showSnackbar(getString(R.string.no_location))
            }
        }

    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        } else {
            // need to ask permission
            val requestLocationPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    granted ->
                    if (granted) {
                        Log.d(TAG, "User Granted Permission")
                        locationPermissionGranted = true
                        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                    } else {
                        Log.d(TAG, "User did not grant permission")
                        locationPermissionGranted = false
                    }
                }
            requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }
    private fun showSnackbar(message: String) {
        // helper function
        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show()
    }

}
