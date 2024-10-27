package com.example.wanderlog.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.dataModel.PostAdapter
import com.example.wanderlog.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var auth = Firebase.auth

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    private val postList = arrayListOf(
        Post(auth.currentUser!!.uid, "user1"),
        Post(auth.currentUser!!.uid, "user2"),
        // Add more Post items
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_searchNavigation)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        postAdapter = PostAdapter(requireContext(), postList)
        recyclerView.adapter = postAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}