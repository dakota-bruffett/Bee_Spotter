package com.example.beespotter

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


// var map: GoogleMap? = null



class BeeViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>() // constructor

    var currentUserLocation: GeoPoint? = null
    var latestImageUri: Uri? = null
    var locationPermissionGranted: Boolean = false
    var fusedLocationProvider: FusedLocationProviderClient? = null




    fun addBee(bee: Bee) {
        TODO("Not yet implemented")
    }



    fun recallBees() {
        TODO("Not yet implemented")
    }


    // TODO Other Additions as needed.
}