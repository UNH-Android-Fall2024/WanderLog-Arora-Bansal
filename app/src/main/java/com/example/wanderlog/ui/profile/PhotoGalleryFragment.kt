package com.example.wanderlog.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wanderlog.dataModel.PhotoGridAdapter
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.databinding.FragmentPhotoGalleryBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class PhotoGalleryFragment : Fragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private var userID = ""
    private lateinit var photoAdapter: PhotoGridAdapter
    private var photoList : ArrayList<Post> = arrayListOf()


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
        binding.photoGridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        photoAdapter = PhotoGridAdapter(requireContext(), photoList)
        binding.photoGridRecyclerView.adapter = photoAdapter

        db.collection("posts").whereEqualTo("userID",userID)
        .get()
        .addOnSuccessListener {result ->
            photoList.clear()
            for (document in result){
                val post = document.toObject<Post>()
                post.postID = document.id
                photoList.add(post)

            }
            photoAdapter.notifyDataSetChanged()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

