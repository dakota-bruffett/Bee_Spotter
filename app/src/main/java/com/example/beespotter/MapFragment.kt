package com.example.beespotter

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import android.Manifest
import com.google.type.Date
import com.google.type.LatLng

private const val TAG = "BEE_MAP_FRAGMENT"
class MapFragment : Fragment() {

    private lateinit var addBeeButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    // Ask users for permission to allow their current locations
    private var locationPermissionGranted = false

    private var movedMapToUserLocation = false

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var map: GoogleMap? = null

    private val beeMarkers = mutableListOf<Marker>()

    private var beeList = listOf<Bee>()

    private val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)
    }

//    private val mapReadyCallback = OnMapReadyCallback { googleMap ->
//
//        Log.d(TAG, "Google map ready")
//        map = googleMap
//        updateMap()
//    }

    private fun updateMap() {

        if (locationPermissionGranted) {
            if (!movedMapToUserLocation) {
                moveMapToUserLocation()
            }
            setAddBeeButtonEnabled(true)
        }

        drawBees()
    }

    private fun setAddBeeButtonEnabled(isEnabled: Boolean) {
        addBeeButton.isClickable = isEnabled
        addBeeButton.isFocusable = isEnabled

        if (isEnabled) {
            addBeeButton.backgroundTintList = AppCompatResources.getColorStateList(
                requireActivity(),
                android.R.color.holo_green_light
            )
        } else {
            addBeeButton.backgroundTintList = AppCompatResources.getColorStateList(
                requireActivity(),
                android.R.color.holo_orange_light
            )
        }
    }

    private fun drawBees() {
        if (map == null) {
            return
        }

        for (marker in beeMarkers) {
            marker.remove()
        }

        beeMarkers.clear()

        for (bee in this.beeList) {

            val isFavorite = bee.location ?: false
            val iconId = if (isFavorite as Boolean) R.drawable.bee_icon else R.drawable.home

            bee.location?.let { location ->
                val markerOptions = MarkerOptions()
                    .position(
                        com.google.android.gms.maps.model.LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                    .title(bee.location.toString())
                    .snippet("Spotted on ${bee.dateSpotted}")
                    .icon(BitmapDescriptorFactory.fromResource(iconId))

                map?.addMarker(markerOptions)?.also { marker ->
                    beeMarkers.add(marker)
                    marker.tag = bee
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addBeeAtLocation() {

        if (fusedLocationProvider == null) {
            return
        }
        if (!locationPermissionGranted) {
            showSnackbar(getString(R.string.give_location_permission))
        }

        try {
            fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
                val location = task.result

                if (location != null) {

                    val bee = Bee(
                        dateSpotted = java.util.Date(),
                        location = GeoPoint(location.latitude, location.longitude)
                    )
                    beeViewModel.addBee(bee)
                    moveMapToUserLocation()
                    showSnackbar(getString(R.string.add_bee))
                } else {
                    showSnackbar(getString(R.string.no_location))
                }
            }
        } catch (ex: SecurityException) {
            Log.e(TAG, "Adding bee at location - permission not granted", ex)

        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }


    @SuppressLint("MissingPermission")
    private fun moveMapToUserLocation() {

        if (map == null) {
            return
        }

        try {

            if (locationPermissionGranted) {
                fusedLocationProvider =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                map?.isMyLocationEnabled = true   // show blue dot at user's location
                map?.uiSettings?.isMyLocationButtonEnabled = true  // show move to my location

                fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
                    val location = task.result
                    if (location != null) {
                        Log.d(TAG, "User location $location")
                        val center = com.google.android.gms.maps.model.LatLng(
                            location.latitude,
                            location.longitude
                        )


                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8f))?.also {
                            movedMapToUserLocation = true
                        }
                    } else {
                        showSnackbar(getString(R.string.no_location))
                    }
                }

            }

        } catch (ex: SecurityException) {
            Log.e(TAG, "Showing user's location on map - permission not requested", ex)
            locationPermissionGranted = false
        }
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
    // }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.fragment_map, container, false)

        // Add bee and redirect to camera fragment
        val addBeeButton: FloatingActionButton = mainView.findViewById(R.id.add_bee)
        addBeeButton.setOnClickListener {
            addBeeAtLocation()
            getCamera()
        }

        // Take user back to home fragment
        val homeButton: FloatingActionButton = mainView.findViewById(R.id.go_home)
        homeButton.setOnClickListener {
            getHome()

        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)

        setAddBeeButtonEnabled(false)

        requestLocationPermission()

        return mainView
    }


    val mapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        updateMap()
    }


    private fun requestLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
            updateMap()
            setAddBeeButtonEnabled(true)

        } else {
            val requestLocationPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) {
                        Log.d(TAG, "User granted permission")
                        setAddBeeButtonEnabled(true)
                        locationPermissionGranted = true
                        fusedLocationProvider =
                            LocationServices.getFusedLocationProviderClient(requireActivity())

                    } else {
                        Log.d(TAG, "User did not grant permission")
                        setAddBeeButtonEnabled(false)
                        locationPermissionGranted = false
                        showSnackbar(getString(R.string.give_permission))
                    }

                    updateMap()
                }

            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getHome(): Any {
        return HomeFragment
    }

    private fun getCamera(): CameraFragment.Companion {
        return CameraFragment
    }


    companion object {

        @JvmStatic
        fun newInstance() = MapFragment()
    }

}
