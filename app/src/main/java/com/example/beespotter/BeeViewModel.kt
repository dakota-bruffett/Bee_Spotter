package com.example.beespotter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BeeViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>() // constructor


    fun addBee(bee: Bee) {
        TODO("Not yet implimented")
    }
    // TODO Other Additions as needed.
}