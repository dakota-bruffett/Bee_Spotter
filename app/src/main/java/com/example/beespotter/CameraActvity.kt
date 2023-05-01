package com.example.beespotter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val BEE = "Bee"
private const val BEELOCATION = "Bee location"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraActvity.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraActvity : Fragment() {

    private var Bee: String? = null
    private var Beelocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Bee = it.getString(BEE)
            Beelocation = it.getString(BEELOCATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Camera.
         */

        @JvmStatic
        fun newInstance(BEE: String, BEELOCATION : String) =
            CameraActvity().apply {
                arguments = Bundle().apply {
                    putString(Bee, BEE)
                    putString(Beelocation, BEELOCATION)
                }
            }
    }
}