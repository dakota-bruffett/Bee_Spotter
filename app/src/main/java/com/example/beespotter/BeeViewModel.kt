package com.example.beespotter

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


// var map: GoogleMap? = null


//
//class BeeViewModel: ViewModel() {
//    private val db = Firebase.firestore
//    private val beeCollectionReference = db.collection("bees")
//
//    val latestBees = MutableLiveData<List<Bee>>() // constructor
//
//    var currentUserLocation: GeoPoint? = null
//    var latestImageUri: Uri? = null
//    var locationPermissionGranted: Boolean = false
//    var fusedLocationProvider: FusedLocationProviderClient? = null
//
//
//
//
//    fun addBee(bee: Bee) {
//        TODO("Not yet implemented")
//    }
//
//
//
//    fun recallBees() {
//        TODO("Not yet implemented")
//    }
//
//
//    // TODO Other Additions as needed.
//}

class BeeViewModel : ViewModel() {

    var currentUserLocation: GeoPoint? = null
        set(value) {
            field = value
            currentUserLocationLiveData.value = value
        }

    val currentUserLocationLiveData = MutableLiveData<GeoPoint>()

    // val latestBees = MutableLiveData<List<Bee>>() // constructor

    var latestImageUri: Uri? = null
    var locationPermissionGranted: Boolean = false
    var fusedLocationProvider: FusedLocationProviderClient? = null

    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    fun addBee(db: FirebaseFirestore, bee: Bee) {
        beeCollectionReference.add(bee)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}