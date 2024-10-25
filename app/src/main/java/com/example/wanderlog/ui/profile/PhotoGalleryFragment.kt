package com.example.wanderlog.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Photo
import com.example.wanderlog.dataModel.PhotoGridAdapter
import com.example.wanderlog.databinding.FragmentPhotoGalleryBinding

class PhotoGalleryFragment : Fragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoAdapter: PhotoGridAdapter
    private val photoList = listOf(
        Photo(R.drawable.baseline_image_24),
        Photo(R.drawable.baseline_image_24),
        Photo(R.drawable.baseline_image_24),
        Photo(R.drawable.baseline_image_24),
        Photo(R.drawable.baseline_image_24),
        Photo(R.drawable.baseline_image_24),
        // Add more Photo items
    )


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
        photoAdapter = PhotoGridAdapter(requireContext(), photoList)
        binding.photoGridRecyclerView.adapter = photoAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}

