import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.beespotter.Bee
import com.example.beespotter.BeeViewModel
import com.example.beespotter.R
import com.example.beespotter.toGeoPoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var takePictureButton: MaterialButton
    private lateinit var viewModel: BeeViewModel
    private var currentPhotoPath: String? = null
    private lateinit var db: FirebaseFirestore


    companion object {
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }

        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 2
        private const val REQUEST_LOCATION_PERMISSION = 3
        private const val TAG = "CameraFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_camera, container, false)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Obtain a reference to the BeeViewModel
        viewModel = ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)

        // Get a reference to the Take Picture button and set its OnClickListener
        takePictureButton = rootView.findViewById(R.id.take_picture_button)
        takePictureButton.setOnClickListener {
            // Check if the camera permission has been granted
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the camera permission if it has not been granted
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                // Launch the camera app if the camera permission has been granted
                launchCamera()
            }
        }

        return rootView
    }

    private fun launchCamera() {
        // Create an Intent to launch the camera app
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create a file to store the captured image
            val photoFile = createImageFile()

            // Save the file path to a variable so we can access it later
            currentPhotoPath = photoFile.absolutePath

            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )

            // Add the file path as an extra to the camera intent
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            // Launch the camera app and wait for the result
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        // Create a unique file name for the captured image
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // Get the directory where the image file should be stored
        val storageDir = requireContext().getExternalFilesDir(null)

        // Create the file and return it
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // The image was successfully captured, so save its metadata to the database
            saveBeeToDatabase()
        } else {
            // Delete the temporary file that was created for the image
            val photoFile = File(currentPhotoPath)
            if (photoFile.exists()) {
                photoFile.delete()
            }
        }
    }

    private fun saveBeeToDatabase() {
        // Check if the location permission has been granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the location permission if it has not been granted
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            // Get the user's current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Create a Bee object with the captured image's metadata and the user's location
                val bee = Bee(
                    pictureFileName = currentPhotoPath,
                    dateSpotted = Date(),
                    location = location?.toGeoPoint()
                )

                // Print the Bee object to the console
                Log.d(TAG, "Bee object: $bee")

                // Clear the current photo path variable
                currentPhotoPath = null

                // Navigate back to the home screen
                requireActivity().supportFragmentManager.popBackStack()

                // Update the BeeViewModel to reflect the newly added Bee
                viewModel.addBee(db, bee)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Launch the camera app if the camera permission has been granted
                    launchCamera()
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.camera_permission_denied,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Get the user's current location and save the Bee to the database
                    saveBeeToDatabase()
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.location_permission_denied,
                        Toast.LENGTH_SHORT
                    ).show()

                    // Delete the temporary file of the saved image
                    val photoFile = File(currentPhotoPath)
                    if (photoFile.exists()) {
                        photoFile.delete()
                    }

                    // Clear the current photo path variable
                    currentPhotoPath = null
                }
            }
        }
    }
}