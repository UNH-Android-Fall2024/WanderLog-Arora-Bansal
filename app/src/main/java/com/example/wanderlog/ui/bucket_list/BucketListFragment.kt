package com.example.wanderlog.ui.bucket_list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanderlog.databinding.FragmentBucketListBinding
import com.example.wanderlog.dataModel.BucketListAdapter
import com.example.wanderlog.dataModel.Location
import com.example.wanderlog.databinding.DialogAddBlItemBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import android.location.Geocoder
import java.util.Locale
import android.os.Build

class BucketListFragment : Fragment() {

    private var _binding: FragmentBucketListBinding? = null
    private val binding get() = _binding!!
    private val bucketListItems: ArrayList<Location> = arrayListOf()
    private lateinit var adapter: BucketListAdapter
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBucketListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadBucketListItems()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        adapter = BucketListAdapter(bucketListItems)
        binding.bucketListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BucketListFragment.adapter
        }
    }

    private fun loadBucketListItems() {
        db.collection("locations")
            .whereEqualTo("userID", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                bucketListItems.clear()
                for (document in result) {
                    val location = document.toObject<Location>()
                    location.locationID = document.id
                    bucketListItems.add(location)
                    Log.d("ShowLocation", "Loaded location: ${location.city}, ${location.country}")
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ShowLocation", "Error loading locations", e)
            }
    }

    private fun setupAddButton() {
        binding.addBucketListItemButton.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun getLocationCoordinates(city: String, country: String, callback: (Double, Double) -> Unit) {
        try {
            val locationName = "$city, $country"
            Log.d("Geocoding", "Attempting to get coordinates for: $locationName")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(locationName, 1) { addresses ->
                    val latitude = addresses.firstOrNull()?.latitude ?: 0.0
                    val longitude = addresses.firstOrNull()?.longitude ?: 0.0
                    Log.d("Geocoding", "Found coordinates for $locationName: $latitude, $longitude")
                    activity?.runOnUiThread {
                        callback(latitude, longitude)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(locationName, 1)
                val latitude = addresses?.firstOrNull()?.latitude ?: 0.0
                val longitude = addresses?.firstOrNull()?.longitude ?: 0.0
                Log.d("Geocoding", "Found coordinates for $locationName: $latitude, $longitude")
                callback(latitude, longitude)
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error getting coordinates for $city, $country", e)
            callback(0.0, 0.0)
        }
    }

    private fun showAddItemDialog() {
        val dialogBinding = DialogAddBlItemBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.buttonAdd.setOnClickListener {
            val country = dialogBinding.editTextCountry.text.toString().trim()
            val city = dialogBinding.editTextCity.text.toString().trim()

            if (country.isNotEmpty() && city.isNotEmpty()) {
                getLocationCoordinates(city, country) { latitude, longitude ->
                    val location = hashMapOf(
                        "userID" to auth.currentUser!!.uid,
                        "city" to city,
                        "country" to country,
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "visited" to false
                    )

                    db.collection("locations")
                        .add(location)
                        .addOnSuccessListener { documentReference ->
                            val newLocation = Location(
                                locationID = documentReference.id,
                                userID = auth.currentUser!!.uid,
                                city = city,
                                country = country,
                                latitude = latitude,
                                longitude = longitude,
                                visited = false
                            )
                            bucketListItems.add(newLocation)
                            Log.d("AddLocation", "Added location with ID: ${documentReference.id}, Lat: $latitude, Long: $longitude")
                            activity?.runOnUiThread {
                                adapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("AddLocation", "Error adding location", e)
                        }
                    dialog.dismiss()
                }
            } else {
                dialogBinding.editTextCountry.error = if (country.isEmpty()) "Please enter a country" else null
                dialogBinding.editTextCity.error = if (city.isEmpty()) "Please enter a city" else null
            }
        }

        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "BucketListFragment"
    }
}