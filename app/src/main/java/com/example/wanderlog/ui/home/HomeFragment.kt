package com.example.wanderlog.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Connection
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.dataModel.PostAdapter
import com.example.wanderlog.databinding.FragmentHomeBinding
import com.example.wanderlog.ui.bucket_list.LocationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth = Firebase.auth
    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var followingList: ArrayList<String> = arrayListOf()
    private var postList: ArrayList<Post> = arrayListOf()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationHelper: LocationHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ShowHome", "Home page detected")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        locationHelper = LocationHelper(requireContext())
        Log.d("ShowHome", locationHelper.toString())
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            Log.d("ShowHome", "Checkpoint 2")
            if (fineLocationGranted || coarseLocationGranted) {
                Log.d("ShowHome", "Location permissions granted")
                    locationHelper.getCurrentLocation { location ->
                        if (location != null) {
                            Log.d("ShowHome", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                        } else {
                            Log.d("ShowHome", "Unable to fetch location")
                            Toast.makeText(requireContext(), "Unable to retrieve location. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permissions denied. Please enable them to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    binding.btnSearch.setOnClickListener {
        findNavController().navigate(R.id.action_navigation_home_to_searchNavigation)
    }


    recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        postAdapter = PostAdapter(requireContext(),this ,postList)
        recyclerView.adapter = postAdapter
        db.collection("connections").whereEqualTo("userID2", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                followingList.clear()
                for (document in result) {
                    val connection = document.toObject<Connection>()
                    followingList.add(connection.userID1)
                }
            }
        if (followingList.size != 0) {
            db.collection("posts").whereIn("userID", followingList)
                .get()
                .addOnSuccessListener { result ->
                    postList.clear()
                    for (document in result) {
                        val post = document.toObject<Post>()
                        post.postID = document.id
                        postList.add(post)

                    }
                    postAdapter.notifyDataSetChanged()
                    Log.d("ShowPhotos", postList[0].content)
                }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}