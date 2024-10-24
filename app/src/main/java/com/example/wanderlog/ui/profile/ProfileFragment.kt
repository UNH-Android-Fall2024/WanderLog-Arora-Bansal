package com.example.wanderlog.ui.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.style
import kotlin.properties.Delegates


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var postCount by Delegates.notNull<Int>()
    private var followerCount by Delegates.notNull<Int>()
    private var followingCount by Delegates.notNull<Int>()
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
        val ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        ProfileViewModel.text.observe(viewLifecycleOwner) {
            binding.username.text = "@${it.username}"
            binding.fullname.text = it.fullname
            binding.bio.text = it.bio
        }
        postCount = ProfileViewModel.getPostCount()
        followerCount = ProfileViewModel.getFollowerCount()
        followingCount = ProfileViewModel.getFollowingCount()
        binding.postCount.text = "$postCount\nPosts"
        binding.followerCount.text = "$followerCount\nFollowers"
        binding.followingCount.text = "$followingCount\nFollowing"


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

        // Add the map view to the activity (you can also add it to other views as a child)
        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_editProfileNavigation)
        }

        binding.showPhotos.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_showPhotosNavigation)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}