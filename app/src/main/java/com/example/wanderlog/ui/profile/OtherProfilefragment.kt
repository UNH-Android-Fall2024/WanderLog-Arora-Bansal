package com.example.wanderlog.ui.profile


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Connection
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.FragmentOtherProfilefragmentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.style


class OtherProfileFragment : Fragment() {

    private var _binding: FragmentOtherProfilefragmentBinding? = null
    private val binding get() = _binding!!
    private var userID = ""
    private var db = Firebase.firestore
    private lateinit var mapView: MapView
    private companion object {
        private const val ZOOM = 0.45
        private val CENTER = Point.fromLngLat(30.0, 50.0)
    }

    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOtherProfilefragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Create a map programmatically and set the initial camera
        mapView = binding.mapView
        mapView.mapboxMap.apply {
            setCamera(
                cameraOptions {
                    center(CENTER)
                    zoom(ZOOM)
                }
            )
            loadStyle(
                style(Style.LIGHT) {
                    +atmosphere { }
                    +projection(ProjectionName.GLOBE)
                }
            )
        }

        binding.showPhotos.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userID", userID)
            }
            findNavController().navigate(R.id.action_otherUserProfile_to_showPhotosNavigation, bundle)
        }
        binding.follow.setOnClickListener{
            followUser()
            binding.follow.visibility = View.GONE
            binding.unfollow.visibility = View.VISIBLE

        }
        binding.unfollow.setOnClickListener{
            unfollowUser()
            binding.follow.visibility = View.VISIBLE
            binding.unfollow.visibility = View.GONE

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userID = arguments?.getString("userID").toString()
        Log.d("navigate",userID)

        getUserDetails()
        getPostCount()
        getFollowerCount()
        getFollowingCount()
        getFollowingBoolean()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun followUser(){
        val submit = hashMapOf(
            "userID1" to userID,
            "userID2" to auth.currentUser!!.uid
        )
        db.collection("connections").document("$userID ${auth.currentUser!!.uid}")
            .set(submit)
            .addOnSuccessListener {
                Log.d("Follow", "DocumentSnapshot added with ID:$userID ${auth.currentUser!!.uid}")
            }
    }
    private fun unfollowUser(){
        db.collection("connections").document("$userID ${auth.currentUser!!.uid}")
            .delete()
    }
    private fun getUserDetails(){
        db.collection("users").document(userID).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()!!
                "@${user.username}".also { binding.username.text = it }
                binding.fullname.text = user.fullname
                binding.bio.text = user.bio
            }

    }

    private fun getFollowingBoolean(){
        db.collection("connections").whereEqualTo("userID1", userID)
            .whereEqualTo("userID2", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                if (result.size() != 0){
                   binding.follow.visibility = View.GONE
                    binding.unfollow.visibility = View.VISIBLE
                }
            }

    }
    private fun getPostCount(){
        var count = 0
        db.collection("posts").whereEqualTo("userID",userID).get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    count++
                }
                "$count\nPosts".also { binding.postCount.text = it }
            }
    }

    private fun getFollowerCount(){
        var count = 0
        db.collection("connections").whereEqualTo("userID1",userID).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
                "$count\nFollowers".also { binding.followerCount.text = it }
            }
    }

    private fun getFollowingCount(){
        var count = 0
        db.collection("connections").whereEqualTo("userID2",userID).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
                "$count\nFollowing".also { binding.followingCount.text = it }
            }
    }



}