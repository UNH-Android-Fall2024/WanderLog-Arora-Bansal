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
import com.google.firebase.storage.storage
import java.io.File

class PostDetailFragment : Fragment() {

    private var _binding: PostCardLayoutBinding? = null
    private val binding get() = _binding!!
    private var storage = Firebase.storage

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
        val username = arguments?.getString("username")
        val postImageUrl = arguments?.getString("imageUrl")
        val likes = arguments?.getString("likes")
        val comments = arguments?.getString("comments")
        val caption = arguments?.getString("caption")

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
        binding.likeCount.text = likes
        binding.viewComments.text = comments
        binding.postDescription.text = caption
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
