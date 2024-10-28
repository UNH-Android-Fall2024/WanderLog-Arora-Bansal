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
    private var auth = Firebase.auth
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

        // Initialize RecyclerView with binding
        binding.photoGridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        // Initialize the adapter with click listener

        photoAdapter = PhotoGridAdapter(requireContext(), photoList) { post ->

            navigateToPostDetailFragment(post) // Handle item click
        }
        binding.photoGridRecyclerView.adapter = photoAdapter

        db.collection("posts").whereEqualTo("userID",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {result ->
                photoList.clear()
                for (document in result){
                    val post = document.toObject<Post>()
                    photoList.add(post)
                }
                photoAdapter.notifyDataSetChanged()
                Log.d("ShowPhotos", photoList[0].content)
            }

        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()!!
                username = user.username
                photoAdapter.notifyDataSetChanged()
            }

    }
    private fun navigateToPostDetailFragment(post: Post) {
        // Create a bundle to pass the arguments

        val bundle = Bundle().apply {
            putString("username", username)
            putInt("postImageResId", 1)
            putString("likes", "${post.likes.count().toString()} Likes")
            putString("comments", "${post.comments.count().toString()} Comments")
            putString("caption", post.content)
        }

        // Navigate using the findNavController
        findNavController().navigate(R.id.action_showPhotosNavigation_to_postDetailFragment, bundle)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}

