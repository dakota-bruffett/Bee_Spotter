package com.example.beespotter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var viewMapButton: Button
    private lateinit var addBeeButton: Button
    private lateinit var beeListRecyclerView: RecyclerView
    private lateinit var beeRecyclerView: RecyclerView
    private lateinit var beeAdapter: BeeRecyclerViewAdapter

    val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(beeViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        beeRecyclerView = view.findViewById(R.id.bee_list_recycler_view)
        beeAdapter = BeeRecyclerViewAdapter(emptyList(), requireContext())
        beeRecyclerView.adapter = beeAdapter

        val db = Firebase.firestore
        val beesRef = db.collection("bees")

        beesRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val bees = mutableListOf<Bee>()
                Log.d(TAG, "Retrieved ${snapshot.documents.size} documents")
                for (document in snapshot.documents) {
                    val bee = document.toObject(Bee::class.java)
                    bee?.let { bees.add(it) }
                }
                beeAdapter.updateBees(bees)
            } else {
                Log.e(TAG, "Snapshot is null")
            }
        }     // Create a new instance of the BeeRecyclerViewAdapter

        // Set the RecyclerView's layout manager and adapter
        beeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        beeRecyclerView.adapter = beeAdapter

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

