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
import androidx.core.os.bundleOf
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAddPostBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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

        if (latitude != 0.0 && longitude != 0.0) {
            updateLocationField()
        }


        viewBinding.postButton.setOnClickListener {
            uploadPost()
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

        val locationList = arrayListOf("$latitude", "$longitude")

        uploadTask.addOnFailureListener {
            Log.d("uploadedfail",postPath)
        }.addOnSuccessListener { taskSnapshot ->
            val submit = Post(
                userID = auth.currentUser!!.uid,
                content = viewBinding.captionInput.text.toString(),
                imageUrl = postPath,
                location = locationList
            )
            db.collection("posts").document(uniqueID.toString()).set(submit, SetOptions.merge())
            Log.d("uploaded","Success $postPath")
            Toast.makeText(requireContext(), "Selected Image: $imageUri", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addPostFragment_to_navigation_home)

        }
    }

    private fun updateLocationField() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    activity?.runOnUiThread {
                        val locationText = if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            "${address.adminArea ?: ""}, ${address.countryName ?: ""}"
                        } else {
                            "$latitude, $longitude"
                        }
                        viewBinding.locationInput.setText(locationText)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val locationText = if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    "${address.adminArea ?: ""}, ${address.countryName ?: ""}"
                } else {
                    "$latitude, $longitude"
                }
                viewBinding.locationInput.setText(locationText)
            }
        } catch (e: Exception) {
            Log.e("AddPost", "Geocoding failed", e)
            viewBinding.locationInput.setText("$latitude, $longitude")
        }
    }

    companion object {
        fun newInstance(imageUri: Uri): AddPostFragment {
            return AddPostFragment().apply {
                arguments = bundleOf("imageUri" to imageUri)
            }
        }
    }
}
