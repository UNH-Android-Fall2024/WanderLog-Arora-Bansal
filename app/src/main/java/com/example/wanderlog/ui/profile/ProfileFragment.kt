package com.example.wanderlog.ui.profile


import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.style
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private var storage = Firebase.storage
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private var auth = Firebase.auth
    private lateinit var mapView: MapView
    private companion object {
        private const val ZOOM = 0.45
        private val CENTER = Point.fromLngLat(30.0, 50.0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getUserDetails()
        getPostCount()
        getFollowerCount()
        getFollowingCount()

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
                style(Style.SATELLITE_STREETS) {
                    +atmosphere { }
                    +projection(ProjectionName.GLOBE)
                }
            )
        }

        binding.editProfile.setOnClickListener {

            findNavController().navigate(R.id.action_navigation_profile_to_editProfileNavigation)
        }

        binding.showPhotos.setOnClickListener {
            val bundle = Bundle().apply {
                putString("userID", auth.currentUser!!.uid)
            }
            findNavController().navigate(R.id.action_navigation_profile_to_showPhotosNavigation, bundle)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getUserDetails(){
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()!!
                "@${user.username}".also { binding.username.text = it }
                binding.fullname.text = user.fullname
                binding.bio.text = user.bio
                if(user.profilePicture!=""){
                    val storageRef = storage.reference.child(user.profilePicture)
                    val localFile = File.createTempFile(
                        "tempImage", ".jpg"
                    )
                    storageRef.getFile(localFile).addOnSuccessListener {
                        // Local temp file has been created
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.profilePicture.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        binding.profilePicture.setImageResource(R.drawable.baseline_person_24)
                    }
                }
            }

    }
    private fun getPostCount(){
        var count = 0
        db.collection("posts").whereEqualTo("userID",auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    count++
                }
                "$count\nPosts".also { binding.postCount.text = it }
            }
    }

    private fun getFollowerCount(){
        var count = 0
        db.collection("connections").whereEqualTo("userID1",auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
                "$count\nFollowers".also { binding.followerCount.text = it }
            }
    }

    private fun getFollowingCount(){
        var count = 0
        db.collection("connections").whereEqualTo("userID2",auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
                "$count\nFollowing".also { binding.followingCount.text = it }
            }
    }



}