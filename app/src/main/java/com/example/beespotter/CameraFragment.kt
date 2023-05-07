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
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.lang.Exception
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

    private var bee: String? = null
    private var beelocation: String? = null
    private var beePhotoPath: String? = null
    private var photoUri: Uri? = null
    private var visibleImagePath: String? = null
    private val storage = Firebase.storage
    private var beeFilename: String? = null
    private val cameraActivityLanucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result -> handleBeeImage(result)
    }

    private fun handleBeeImage(result: ActivityResult?) {
        if (result != null) {
            when(result.resultCode){
                AppCompatActivity.RESULT_OK ->{
                    Log.d(TAG,"Result is all good and ready to be used ")
                    visibleImagePath = beePhotoPath

                }
                AppCompatActivity.RESULT_CANCELED ->
                    Log.d(TAG,"You cancelled your image")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bee = it.getString(BEE)
            beelocation = it.getString(BEELOCATION)
        }
    }

    private fun CreatebeeImageFile():Pair<File?,String?>{
        try {
            val dateTime = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
            beeFilename ="BeeImageFile_$dateTime"
            val StorageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(beeFilename!!,"jpg",StorageDir)
            val filepath = file.absolutePath
            return file to filepath
        }catch(ex:IOException){
            return null to null
        }
    }
    private fun SaveBee(){
        if (photoUri != null && beeFilename != null){
            val BeeStorageRootReference = storage.reference
            val BeeImageCollection = BeeStorageRootReference.child("BeeImage")
            val BeeFileReference = BeeImageCollection.child(beeFilename!!)
            BeeFileReference.putFile(photoUri!!).addOnCompleteListener{

                Snackbar.make(requireView(),"BeeloadImage",Snackbar.LENGTH_LONG ).show()
            }.addOnFailureListener{ error ->
                Snackbar.make(requireView(), "BeeloadImage",Snackbar.LENGTH_LONG ).show()
            Log.e(TAG, "Could not upload$beeFilename",error)}
        }
    }
    private fun takeBeePicture(){
        val BeePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val (photoFile,PathPhotoFile) = CreatebeeImageFile()
        if (photoFile != null){
            beePhotoPath = PathPhotoFile
            val photoUri = FileProvider.getUriForFile(
                requireActivity(),
                "com.example.BeeSpotter.fileprovider",
                photoFile

            )

        }
        BeePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
        cameraActivityLanucher.launch(BeePictureIntent)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment




        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        view.findViewById<ImageButton>(R.id.CameraImage).setOnClickListener { cameraActivityLanucher}
        view.findViewById<Button>(R.id.Save).setOnClickListener { SaveBee() }
        view.findViewById<Button>(R.id.Picture_Button).setOnClickListener { takeBeePicture() }
        return view
    }
    private fun loadBeeImage(CameraImage:ImageButton ,imagepath : String){
        Picasso.get()
            .load(imagepath)
            .error(android.R.drawable.stat_notify_error)
            .fit()
            .centerCrop()
            .into(CameraImage,object: Callback {
            override fun onSuccess(){
                Log.d(TAG, "Loaded Image$CameraImage")
            }

                override fun onError(e: Exception?) {
                    Log.e(TAG,"Failed to load image $CameraImage",e)
                }
            })
    }


    companion object {
        @JvmStatic
        fun newInstance() = CameraFragment()
    }
}