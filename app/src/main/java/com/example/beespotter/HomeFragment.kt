package com.example.beespotter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

// TODO ***** Need to update to be used with Fragments instead ****

// todo update the icon graphic


/*
todo design overall layout in fragment_home.xml
 - view map
 - Add bee at location
 - background, other design elements
 - Constrain layouts
*/

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var viewMapButton: Button
    private lateinit var addBeeButton: Button
    private lateinit var beeListRecyclerView: RecyclerView

    val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(beeViewModel::class.java)
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        initializeWidgets(view)
        setButtonOnClickListeners()
        return view
    }


    private fun setButtonOnClickListeners() {
        viewMapButton.setOnClickListener {
            // Map Intent - start.
            showFragment("MAP")
        }

        addBeeButton.setOnClickListener {
            // Camera Intent - start.

            // Set listener for result codes:
            // RESULT_OK -> "Bee Added"  Snackbar
            // RESULT_CANCELLED ->  I think do nothing.
            // no result
            showFragment("CAMERA")
        }
    }

    private fun showFragment(s: String) {
        TODO("Not Yet Implemented - need clarification")
    }


    private fun initializeWidgets(view: View) {
        viewMapButton = view.findViewById(R.id.view_map_button)
        addBeeButton = view.findViewById(R.id.add_bee_at_location_button)
        beeListRecyclerView = view.findViewById(R.id.bee_list_recycler_view)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
            }
    }
}