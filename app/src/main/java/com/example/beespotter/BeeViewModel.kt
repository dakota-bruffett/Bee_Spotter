package com.example.beespotter

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BeeViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>() // constructor
    val userCurrentLocation: GeoPoint? = null
    val LatestImageUri: Uri? = null
    fun addBee(bee: Bee) {
        // TODO PLEASE REMEMBER
    }
    fun recallBees() {
        TODO("Not yet implemented")
    }
    // TODO Other Additions as needed.
}