package com.example.beespotter

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.GeoPoint
import java.text.SimpleDateFormat
import java.util.Locale

class BeeRecyclerViewAdapter(var bees: List<Bee>, private val context: Context) : RecyclerView.Adapter<BeeRecyclerViewAdapter.ViewHolder>() {

    fun getLocationDetailsFromGeoPoint(geoPoint: GeoPoint): LocationDetails? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
        val address = addresses?.firstOrNull()
        val townOrPark = address?.subLocality ?: address?.subAdminArea
        val city = address?.locality ?: address?.adminArea
        return if (townOrPark != null || city != null) {
            LocationDetails(townOrPark, city)
        } else {
            null
        }
    }

    fun updateBees(bees: MutableList<Bee>) {
        this.bees = bees
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bee_recycler_view_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bee = bees[position]
        val simpleDate = SimpleDateFormat("MMM d yy", Locale.getDefault())
        val simplifiedDate = simpleDate.format(bee.dateSpotted)
        holder.dateSpottedTextView.text = simplifiedDate.toString()

        val locationDetails = getLocationDetailsFromGeoPoint(bee.location ?: return)
        val locationText = if (locationDetails != null) {
            "${locationDetails.townOrPark ?: ""}, ${locationDetails.city ?: ""}"
        } else {
            "Unknown location"
        }
        holder.locationTextView.text = locationText
    }

    override fun getItemCount() = bees.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationTextView: TextView = itemView.findViewById(R.id.bee_location)
        val dateSpottedTextView: TextView = itemView.findViewById(R.id.dateSpotted)

//        fun bind(bee: Bee) {
//            locationTextView.text = bee.location.toString()
//            dateSpottedTextView.text = bee.dateSpotted.toString()
//        }
    }
}