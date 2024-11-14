package com.example.wanderlog.ui.bucket_list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.databinding.FragmentBucketListBinding
import com.example.wanderlog.dataModel.BucketListAdapter
import com.example.wanderlog.dataModel.Location
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.databinding.DialogAddBlItemBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class BucketListFragment : Fragment() {

    private var _binding: FragmentBucketListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val bucketListItems : ArrayList<Location> = arrayListOf()
    private lateinit var adapter: BucketListAdapter
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBucketListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = BucketListAdapter(bucketListItems)
        binding.bucketListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bucketListRecyclerView.adapter = adapter
        db.collection("locations").whereEqualTo("userID",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {result ->
                bucketListItems.clear()
                for (document in result){
                    val location = document.toObject<Location>()
                    location.locationID = document.id
                    bucketListItems.add(location)
                    Log.d("ShowLocation", document.id)

                }
                adapter.notifyDataSetChanged()
            }

        binding.addBucketListItemButton.setOnClickListener {
            showAddItemDialog()
        }


    }

    private fun showAddItemDialog() {
        // Inflate the dialog layout
        val dialogBinding = DialogAddBlItemBinding.inflate(layoutInflater)

        // Create and show the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Handle Add button click
        dialogBinding.buttonAdd.setOnClickListener {
            val country = dialogBinding.editTextCountry.text.toString().trim()
            val city = dialogBinding.editTextCity.text.toString().trim()
            val location = hashMapOf(
                "city" to city,
                "country" to country,
                "userID" to auth.currentUser!!.uid,
                "latitude" to 0,
                "longitude" to 0,
                "visited" to false
            )
            if (country.isNotEmpty() && city.isNotEmpty()) {
                // Add the new item to the list and notify the adapter
                db.collection("locations")
                    .add(location)
                    .addOnSuccessListener { documentReference ->
                        bucketListItems.add(Location(documentReference.id,auth.currentUser!!.uid,city,country,0,0,false))
                        Log.d("AddLocation", "DocumentSnapshot written with ID: ${documentReference.id}")
                        adapter.notifyDataSetChanged()

                    }
                    .addOnFailureListener { e ->
                        Log.w("AddLocation", "Error adding document", e)
                    }
                dialog.dismiss()
            } else {
                // Show an error or prompt the user to fill both fields
                dialogBinding.editTextCountry.error = if (country.isEmpty()) "Please enter a country" else null
                dialogBinding.editTextCity.error = if (city.isEmpty()) "Please enter a city" else null
            }
        }

        // Handle Cancel button click
        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}