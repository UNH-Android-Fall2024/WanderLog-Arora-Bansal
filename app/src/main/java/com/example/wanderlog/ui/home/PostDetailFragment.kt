package com.example.wanderlog.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FontRes
import androidx.fragment.app.Fragment
import com.example.wanderlog.R
import com.example.wanderlog.databinding.PostCardLayoutBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.io.File
import android.location.Geocoder
import java.util.Locale
import android.os.Build

class PostDetailFragment : Fragment() {

    private var _binding: PostCardLayoutBinding? = null
    private val binding get() = _binding!!
    private var storage = Firebase.storage
    private val db = Firebase.firestore
    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PostCardLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from arguments
        val postID = arguments?.getString("postID")
        val userID = arguments?.getString("userID")
        val username = arguments?.getString("username")
        val postImageUrl = arguments?.getString("imageUrl")
        var likes = arguments?.getInt("likes")
        val comments = arguments?.getInt("comments")
        val caption = arguments?.getString("caption")
        val liked = arguments?.getBoolean("liked")
        val profilePicture = arguments?.getString("profilePic")
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0


        if (latitude != 0.0 && longitude != 0.0) {
            getLocationFromCoordinates(latitude, longitude)
        } else {
            binding.location.visibility = View.GONE
        }

        
        if (liked!!){
            binding.likeButton.visibility = View.GONE
            binding.likedButton.visibility = View.VISIBLE
        }
        binding.username.text = username
        Log.d("Post Detail", postImageUrl.toString())
        val storageRef = storage.reference.child(postImageUrl.toString())
        val localFile = File.createTempFile(
            "tempImage", ".jpg"
        )
        storageRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.postImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            binding.postImage.setImageResource(R.drawable.baseline_image_24)
        }
        if(profilePicture!=""){
            val storageRef1 = storage.reference.child(profilePicture.toString())
            val localFile1 = File.createTempFile(
                "tempImage1", ".jpg"
            )
            storageRef1.getFile(localFile1).addOnSuccessListener {
                // Local temp file has been created
                val bitmap1 = BitmapFactory.decodeFile(localFile1.absolutePath)
                binding.profileImage.setImageBitmap(bitmap1)
            }.addOnFailureListener {
                binding.profileImage.setImageResource(R.drawable.baseline_person_24)
            }
        }
        binding.likeCount.text = "${likes.toString()} Likes"
        binding.viewComments.text = "${comments.toString()} Comments"
        binding.postDescription.text = caption
        binding.postUsername.text = username
        binding.likeButton.setOnClickListener{
            if (postID != null) {
                db.collection("posts").document(postID).update("likes", FieldValue.arrayUnion(userID))
            }
            binding.likeButton.visibility = View.GONE
            binding.likedButton.visibility = View.VISIBLE
            likes = likes!!+1
            binding.likeCount.text = "${likes.toString()} Likes"
        }


        binding.likedButton.setOnClickListener{
            if (postID != null) {
                db.collection("posts").document(postID).update("likes", FieldValue.arrayRemove(userID))
            }
            binding.likedButton.visibility = View.GONE
            binding.likeButton.visibility = View.VISIBLE
            likes = likes!!-1
            binding.likeCount.text = "${likes.toString()} Likes"
        }

        binding.commentButton.setOnClickListener{
            val bottomSheetFragment = CommentBottomSheetFragment.newInstance(postID!!)
            bottomSheetFragment.show(parentFragmentManager,"Comment Box")
        }
    }

    private fun getLocationFromCoordinates(latitude: Double, longitude: Double) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    activity?.runOnUiThread {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val state = address.adminArea
                            val country = address.countryName
                            if (state != null && country != null) {
                                binding.location.visibility = View.VISIBLE
                                binding.location.text = "$state, $country"
                            } else {
                                binding.location.visibility = View.GONE
                            }
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val state = address.adminArea
                    val country = address.countryName
                    if (state != null && country != null) {
                        binding.location.visibility = View.VISIBLE
                        binding.location.text = "$state, $country"
                    } else {
                        binding.location.visibility = View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PostDetail", "Error getting location name", e)
            binding.location.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
