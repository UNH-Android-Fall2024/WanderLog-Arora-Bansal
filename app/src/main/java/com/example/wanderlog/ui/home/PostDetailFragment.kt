package com.example.wanderlog.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wanderlog.databinding.PostCardLayoutBinding

class PostDetailFragment : Fragment() {

    private var _binding: PostCardLayoutBinding? = null
    private val binding get() = _binding!!

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
        Log.d("navigate",username.toString())
//        val postImageResId = arguments?.getInt("postImageResId") ?: 0
        val likes = arguments?.getString("likes")
        val comments = arguments?.getString("comments")
        val caption = arguments?.getString("caption")

        // Bind data to views
        binding.username.text = username
//        binding.postImage.setImageResource(postImageResId)
        binding.likeCount.text = likes
        binding.viewComments.text = comments
        binding.postDescription.text = caption
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
