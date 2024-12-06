package com.example.wanderlog.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Connection
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.dataModel.PostAdapter
import com.example.wanderlog.databinding.FragmentHomeBinding
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}