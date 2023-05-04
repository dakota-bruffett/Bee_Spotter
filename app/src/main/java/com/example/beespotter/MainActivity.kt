package com.example.beespotter

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint

const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    private lateinit var containerView: View

    private val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(this).get(BeeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        containerView = findViewById(R.id.fragmentContainer)
        requestLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume(): beeViewModel.currentUserLocation = ${beeViewModel.currentUserLocation.toString()}")
        verifyLocation()
    }

    @SuppressLint("MissingPermission")  // There are checks in place already.
    fun verifyLocation() {
        // check for permissions and existance of maps, locations, etc.
//        if (map == null) { return } // TODO This might be needed later
        if (beeViewModel.fusedLocationProvider == null) { return }
        Log.d(TAG, "Value of fusedLocationProvider within verifyLocation() = ${beeViewModel.fusedLocationProvider.toString()}")
        if (!beeViewModel.locationPermissionGranted) {
            beeViewModel.currentUserLocation = null
            showSnackbar(getString(R.string.user_message_location_permission_needed))
            return
        }

        // all tests pass
        Log.d(TAG, "Value just before Listener: ${beeViewModel.fusedLocationProvider?.lastLocation}")
            beeViewModel.fusedLocationProvider?.lastLocation?.addOnCompleteListener(this) {
            locationRequestTask ->
            val location = locationRequestTask.result
            Log.d(TAG, "User located at $location")
            if (location != null) {
                val userLocation = GeoPoint(location.latitude, location.longitude)
                    beeViewModel.currentUserLocation = userLocation
            } else {
                Log.e(TAG, "User location returned null")

                showSnackbar(getString(R.string.no_location))
            }
        }

    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            beeViewModel.locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
                beeViewModel.fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
            Log.d(TAG, "Value of fusedLocationProvider = ${beeViewModel.fusedLocationProvider.toString()}")
        } else {
            // need to ask permission
            val requestLocationPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    granted ->
                    if (granted) {
                        Log.d(TAG, "User Granted Permission")
                            beeViewModel.locationPermissionGranted = true
                                beeViewModel.fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
                        Log.d(TAG, "Value of fusedLocationProvider = ${beeViewModel.fusedLocationProvider.toString()}")
                    } else {
                        Log.d(TAG, "User did not grant permission")
                            beeViewModel.locationPermissionGranted = false
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
