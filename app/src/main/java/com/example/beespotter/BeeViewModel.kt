package com.example.beespotter

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var currentUserLocation: GeoPoint? = null
var locationPermissionGranted: Boolean = false
var latestImageUri: Uri? = null
var fusedLocationProvider: FusedLocationProviderClient? = null
// var map: GoogleMap? = null



class BeeViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>() // constructor



    fun addBee(bee: Bee) {
        TODO("Not yet implemented")

        // todo if currentUserLocation is null, "Location not specified"
    }



    fun recallBees() {
        TODO("Not yet implemented")
    }


    // TODO Other Additions as needed.
}