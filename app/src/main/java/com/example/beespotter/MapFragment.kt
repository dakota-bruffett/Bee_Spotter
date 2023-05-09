import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.beespotter.Bee
import com.example.beespotter.BeeViewModel
import com.example.beespotter.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var addBeeFab: FloatingActionButton
    private lateinit var viewModel: BeeViewModel
    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseFirestore

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 2
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Obtain a reference to the SupportMapFragment and register this class as the callback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get a reference to the BeeViewModel
        viewModel = ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)

        // Get a reference to the FloatingActionButton and set its OnClickListener
        addBeeFab = rootView.findViewById(R.id.add_bee_fab)
        addBeeFab.setOnClickListener {
            // Request camera permission before opening the camera
            requestCameraPermission()
            openCamera()
        }

        // Get a reference to the Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference

        // Get a reference to the Firebase Firestore
        db = FirebaseFirestore.getInstance()

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable zoom controls on the map
        mMap.uiSettings.isZoomControlsEnabled = true

        // Check if we have permission to access the user's location
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If we don't have permission, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // If we have permission, get the user's last known location and move the camera there
            fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                }
            }
        }

        // Set an OnMapClickListener to add new bees to the map
        mMap.setOnMapClickListener { latLng ->
            // Create a new Bee object with the current timestamp and the clicked location
            val bee = Bee(GeoPoint(latLng.latitude, latLng.longitude), Date())

            // Save the Bee to Firestore
            viewModel.addBee(db, bee)

            // Add a marker to the map at the clicked location
            mMap.addMarker(
                MarkerOptions().position(latLng).title("Bee").snippet("Click to see details")
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permission is granted, get the user's last known location and move the camera there
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    )
                    fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                        if (location != null) {
                            val userLatLng = LatLng(location.latitude, location.longitude)
                            mMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                        }
                    }
                } else {
                    // If permission is denied, show a message and disable the location functionality
                    Log.d("MapsActivity", "Permission denied")
                }
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If camera permission is granted, open the camera
                    openCamera()
                } else {
                    // If camera permission is denied, show a message
                    Snackbar.make(addBeeFab, R.string.camera_permission_denied_message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            // Get the image data from the intent and upload it to Firebase Storage
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()
            val uploadTask = imageRef.putBytes(imageData)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // If the upload is successful, create a new Bee object with the current timestamp, the image URL, and the location data
                val bee = Bee(
                    location = null, // Keeps erroring!!
                    dateSpotted = Date(),
                    pictureFileName = taskSnapshot.metadata?.name
                )

                // Save the Bee to Firestore
                viewModel.addBee(db, bee)

                // Show a message indicating that the Bee was added
                Snackbar.make(addBeeFab, R.string.bee_added_message, Snackbar.LENGTH_LONG).show()
            }.addOnFailureListener { exception ->
                // If the upload fails, show an error message
                Snackbar.make(addBeeFab, R.string.upload_failed_message, Snackbar.LENGTH_LONG).show()
                Log.e("MapFragment", "Failed to upload image: ${exception.message}")
            }
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            )
        ) {
            // If the user has previously denied the permission, show an explanation
            showCameraPermissionExplanation()
        } else {
            // Otherwise, request the permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showCameraPermissionExplanation() {
        Snackbar.make(addBeeFab, R.string.camera_permission_explanation_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.ok) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}