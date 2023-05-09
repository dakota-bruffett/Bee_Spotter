package com.example.beespotter

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Bee(
    val location: GeoPoint? = null,
    val dateSpotted: Date = Date(),
    val pictureFileName: String? = null,
    @get:Exclude @set:Exclude var documentReference: DocumentReference? = null
)  {
    constructor(latitude: Double, longitude: Double, dateSpotted: Date = Date(), pictureFileName: String? = null) : this(GeoPoint(latitude, longitude), dateSpotted, pictureFileName)

}

fun Location.toGeoPoint(): GeoPoint {
    return GeoPoint(latitude, longitude)
}