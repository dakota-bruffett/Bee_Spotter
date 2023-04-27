package com.example.beespotter

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.google.firebase.ktx.Firebase
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var btnMap: ImageButton
    private lateinit var btnBee: ImageButton
    private lateinit var btnBack:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnMap = findViewById(R.id.btn_Map)
        btnBack = findViewById(R.id.btn_Back)
        btnBee = findViewById(R.id.btn_Bee)

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)


//            .commit {
//            .replace<ExampleFragment>(R.id.fragment_container)
//            setReorderingAllowed(true)
//            addToBackStack(null)



            val db = Firebase.firestore
        val bee = mapOf("name" to "bee", "dateSpotted" to Date())

        db.collection("bees").get().addOnSuccessListener { beeDocuments ->
            for ( beeDoc in beeDocuments) {
                val name = beeDoc["name"]
                val dateSpotted = treeDoc["dateSpotted"]
                Log.d(TAG, "$name, $dateSpotted")
            }
        }
    }
}