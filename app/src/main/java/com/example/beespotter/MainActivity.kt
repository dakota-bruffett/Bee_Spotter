package com.example.beespotter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {


    // todo update the icon graphic

    /*
    todo design overall layout in activity_main.xml
     - view map
     - Add bee at location
     - background, other design elements
     - Constrain layouts
    */

    private lateinit var viewMapButton: Button
    private lateinit var addBeeButton: Button
    private lateinit var beeListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeWidgets()
        setButtonOnClickListeners()
    }

    private fun setButtonOnClickListeners() {
        viewMapButton.setOnClickListener {
            TODO("Set Map Button On Click Listener")
            // Map Intent - start.
        }

        addBeeButton.setOnClickListener {
            TODO("Set Bee Button On Click Listener")
            // Camera Intent - start.

            // Set listener for result codes:
            // RESULT_OK -> "Bee Added"  Snackbar
            // RESULT_CANCELLED ->  I think do nothing.
            // no result
        }
    }

    private fun initializeWidgets() {
        viewMapButton = findViewById(R.id.view_map_button)
        addBeeButton = findViewById(R.id.add_bee_at_location_button)
        beeListRecyclerView = findViewById(R.id.bee_list_recycler_view)
    }
}
