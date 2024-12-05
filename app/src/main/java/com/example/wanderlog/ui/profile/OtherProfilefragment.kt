package com.example.wanderlog.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Location
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.FragmentOtherProfilefragmentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import java.io.File

class OtherProfileFragment : Fragment() {

    private val BLUE_ICON_ID = "blue"
    private val SOURCE_ID = "source_id"
    private val LAYER_ID = "layer_id"
    private var _binding: FragmentOtherProfilefragmentBinding? = null
    private val binding get() = _binding!!
    private var userID = ""
    private var storage = Firebase.storage
    private var db = Firebase.firestore
    private lateinit var mapView: MapView
    private val auth = Firebase.auth
    private companion object {
        private const val ZOOM = 0.80
        private val CENTER = Point.fromLngLat(30.0, 50.0)
        private val markerCoordinates = arrayListOf<Point>()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOtherProfilefragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userID = arguments?.getString("userID").toString()
        getUserDetails()
        getPostCount()
        getFollowerCount()
        getFollowingCount()
        getFollowingBoolean()
        getLocations()

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
    private fun correctImageOrientationFromFile(imagePath: String): Bitmap? {
        try {

            val exifInterface = ExifInterface(imagePath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val bitmap = BitmapFactory.decodeFile(imagePath)


            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap // No rotation needed
            }
        } catch (e: Exception) {
            Log.e("ImageRotationError", "Error correcting image orientation: ${e.message}")
        }
        return null
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getLocations(){
        db.collection("locations").whereEqualTo("userID",userID)
            .get()
            .addOnSuccessListener {result ->
                OtherProfileFragment.markerCoordinates.clear()
                for (document in result){
                    val location = document.toObject<Location>()
                    location.locationID = document.id
                    OtherProfileFragment.markerCoordinates.add(Point.fromLngLat(location.longitude, location.latitude))
                    Log.d("ShowLocation", document.id)

                }

            }
        mapView = binding.mapView
        mapView.mapboxMap.apply {
            setCamera(
                cameraOptions {
                    center(OtherProfileFragment.CENTER)
                    zoom(OtherProfileFragment.ZOOM)
                }
            )
            loadStyle(
                style(Style.LIGHT) {
                    +atmosphere { }
                    +projection(ProjectionName.GLOBE)
                    +image(
                        BLUE_ICON_ID,
                        ContextCompat.getDrawable(requireContext(),R.drawable.baseline_location_pin_24_blue)!!.toBitmap()
                    )
                    +geoJsonSource(SOURCE_ID) {
                        featureCollection(
                            FeatureCollection.fromFeatures(OtherProfileFragment.markerCoordinates.map { Feature.fromGeometry(it) })
                        )
                    }
                    +symbolLayer(LAYER_ID, SOURCE_ID) {
                        iconImage(BLUE_ICON_ID)
                        iconAllowOverlap(true)
                        iconAnchor(IconAnchor.BOTTOM)
                    }
                }
            )
        }
    }
    private fun getUserDetails(){
        db.collection("users").document(userID).get()
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
                        val bitmap = correctImageOrientationFromFile(localFile.toString())
                        binding.profilePicture.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        binding.profilePicture.setImageResource(R.drawable.baseline_person_24)
                    }
                }
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