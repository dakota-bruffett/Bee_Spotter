package com.example.beespotter

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Bee(val location: GeoPoint? = null, // todo insert GeoPoint from current location.
               val dateSpotted: Date = Date(),
                val pictureFileName: String? = null, // todo use this connect the picture data with the database entry
    // documentReference doesn't need to be saved as a field in Firebase,
    // during get and set documentReference field will be ignored.
    // it is part of Firebase
               @get:Exclude @set:Exclude var documentReference: DocumentReference? = null,
)