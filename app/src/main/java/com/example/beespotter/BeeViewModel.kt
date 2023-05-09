package com.example.beespotter

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "BEE_VIEW_MODEL"

class BeeViewModel: ViewModel() {

    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>() // constructor

    var currentUserLocation: GeoPoint? = null
    var latestImageUri: Uri? = null
    var locationPermissionGranted: Boolean = false
    var fusedLocationProvider: FusedLocationProviderClient? = null
    var map: GoogleMap? = null

       
    fun addBee(bee: Bee) {
        beeCollectionReference.add(bee)
            .addOnSuccessListener { beeCollectionReference ->
                Log.d(TAG, "Added bee document $beeCollectionReference")
                bee.documentReference = beeCollectionReference
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error adding bee $bee", error)
            }
    }



    fun recallBees() {
        TODO("Not yet implemented")
    }


    // TODO Other Additions as needed.
}