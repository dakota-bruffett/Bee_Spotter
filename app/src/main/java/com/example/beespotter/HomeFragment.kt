package com.example.beespotter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initializeWidgets(view)
        setButtonOnClickListeners()
        return view
    }


    private fun setButtonOnClickListeners() {
        viewMapButton.setOnClickListener {
            // Map Intent - start.
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MapFragment.newInstance(), "MAP")
                .addToBackStack("MAP")
                .commit()
        }

        addBeeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CameraFragment.newInstance(), "CAMERA")
                .addToBackStack("CAMERA")
                .commit()
        }
//        }
//        val button: Button
//        button.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, HomeFragment.newInstance(), "HOME")
//                .commit()
//        }
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