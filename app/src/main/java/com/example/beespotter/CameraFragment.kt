package com.example.beespotter

import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.util.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val BEE = "Bee"
private const val BEELOCATION = "Bee location"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {

    private var Bee: String? = null
    private var Beelocation: String? = null
    private var BeePhotoPath: String? = null
    private var photoUri: Uri? = null
    private val storage = Firebase.storage
    private var BeeFilename: String? = null
    private val cameraActivityLanucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> handleBeeImage(result)
    }

    private fun handleBeeImage(result: ActivityResult?) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Bee = it.getString(BEE)
            Beelocation = it.getString(BEELOCATION)
        }
    }

    private fun CreateBeeImageFile():Pair<File?,String?>{
        try {
            val dateTime = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
            BeeFilename ="BeeImageFile_$dateTime"
            val StorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(BeeFilename!!,"jpg",StorageDir)
            val filepath = file.absolutePath
            return file to filepath
        }catch(ex:IOException){
            return null to null
        }
    }
    private fun SaveBee(){
        if (photoUri != null && BeeFilename != null){
            val BeeStorageRootReference = storage.reference
            val BeeImageCollection = BeeStorageRootReference.child("BeeImage")
            val BeeFileReference = BeeImageCollection.child(BeeFilename!!)
            BeeFileReference.putFile(photoUri!!).addOnCompleteListener{

                Snackbar.make(CameraLayout,"BeeloadImage",Snackbar.LENGTH_LONG ).show()
            }.addOnFailureListener{ error ->
                Snackbar.make(CameraLayout, "BeeloadImage",Snackbar.LENGTH_LONG ).show()
            Log.e(TAG, "Could not upload$BeeFilename",error)}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = CameraFragment()
    }private fun takeBeePicture(){
        val BeePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val (photoFile,PathPhotoFile) = CreateBeeImageFile()
        if (photoFile != null){
            BeePhotoPath = PathPhotoFile
            val photoUri = FileProvider.getUriForFile(
                this,
                "com.example.BeeSpotter.fileprovider",
                photoFile

            )

        }
        BeePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
        cameraActivityLanucher.launch(BeePictureIntent)
    }
}