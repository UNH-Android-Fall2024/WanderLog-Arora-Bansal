package com.example.wanderlog.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.PhotoGridAdapter
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.FragmentPhotoGalleryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class PhotoGalleryFragment : Fragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private var userID = ""
    private lateinit var photoAdapter: PhotoGridAdapter
    private var photoList : ArrayList<Post> = arrayListOf()
    private var username = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userID = arguments?.getString("userID").toString()
        Log.d("navigate",userID)
        // Initialize RecyclerView with binding
        binding.photoGridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        // Initialize the adapter with click listener

        photoAdapter = PhotoGridAdapter(requireContext(), photoList)
        binding.photoGridRecyclerView.adapter = photoAdapter

        db.collection("posts").whereEqualTo("userID",userID)
            .get()
            .addOnSuccessListener {result ->
                photoList.clear()
                for (document in result){
                    val post = document.toObject<Post>()
                    photoList.add(post)
                    Log.d("ShowPhotos", post.imageUrl)

                }
                photoAdapter.notifyDataSetChanged()
            }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}

