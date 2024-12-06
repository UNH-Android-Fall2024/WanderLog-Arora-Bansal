package com.example.wanderlog.ui.camera

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.databinding.FragmentAddPostBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import android.location.Geocoder
import com.example.wanderlog.ui.bucket_list.LocationHelper
import java.util.Locale

class AddPostFragment : Fragment() {
    private lateinit var viewBinding: FragmentAddPostBinding
    private var imageUri: Uri? = null
    private var auth = Firebase.auth
    private val db = Firebase.firestore
    private var storage = Firebase.storage
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }
    private lateinit var locationHelper: LocationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        locationHelper = LocationHelper(requireContext())
        viewBinding = FragmentAddPostBinding.inflate(inflater, container, false)
        return viewBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLocationUpdates()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("imageUri", Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("imageUri")
        }
        imageUri?.let {
            viewBinding.postImage.setImageURI(it)
        } ?: run {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
        latitude = arguments?.getDouble("latitude", 0.0) ?: 0.0
        longitude = arguments?.getDouble("longitude", 0.0) ?: 0.0
        viewBinding.postButton.setOnClickListener {
            uploadPost()
        }
    }

    private fun startLocationUpdates() {
        locationHelper.getCurrentLocation { location ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
                updateLocationField(latitude, longitude)
                activity?.runOnUiThread {
                    viewBinding.postButton.isEnabled = true
                    viewBinding.locationInput.text = "$latitude,$longitude"
                }
            } ?: run {
                Log.e("Location Error", "Failed to get location")
            }
        }
    }

    private fun uploadPost() {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select an image.", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(viewBinding.captionInput.text.toString())) {
            Toast.makeText(requireContext(), "Please write a caption.", Toast.LENGTH_LONG).show()
            return
        }
        var uniqueID= System.currentTimeMillis()
        var postPath = "posts/${auth.currentUser!!.uid}/${uniqueID}.jpg"
        val storageRef = storage.reference
        val riversRef = storageRef.child(postPath)
        val uploadTask = riversRef.putFile(imageUri!!)

        val locationList = arrayListOf(latitude, longitude)

        uploadTask.addOnFailureListener {
        }.addOnSuccessListener { taskSnapshot ->
            val submit = Post(
                userID = auth.currentUser!!.uid,
                content = viewBinding.captionInput.text.toString(),
                imageUrl = postPath,
                location = locationList
            )
            db.collection("posts").document(uniqueID.toString()).set(submit, SetOptions.merge())
            findNavController().navigate(R.id.action_addPostFragment_to_navigation_home)

        }
    }

    private fun updateLocationField(latitude1: Double, longitude1: Double) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude1, longitude1, 1) { addresses ->
                    activity?.runOnUiThread {
                        val locationText = if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            "${address.subAdminArea ?: ""}, ${address.countryName ?: ""}"
                        } else {
                            "$latitude, $longitude"
                        }
                        viewBinding.locationInput.text = locationText
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val locationText = if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    "${address.subAdminArea ?: ""}, ${address.countryName ?: ""}"
                } else {
                    "$latitude, $longitude"
                }
                viewBinding.locationInput.setText(locationText)
            }
        } catch (e: Exception) {
            Log.e("Geocoding Error", "Geocoding failed", e)
            viewBinding.locationInput.setText("$latitude, $longitude")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopLocationUpdates()
    }
}
