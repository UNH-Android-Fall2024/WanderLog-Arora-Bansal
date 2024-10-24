package com.example.wanderlog.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.dataModel.UserAdapter
import com.example.wanderlog.dataModel.UserCard
import com.example.wanderlog.databinding.FragmentSearchListBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

/**
 * A fragment representing a list of Items.
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var mRecyclerView: RecyclerView
    val UserList: ArrayList<UserCard> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchListBinding.inflate(inflater, container, false)
        val root: View = binding.root



        db.collection("users").get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val user = document.toObject<User>()
                    UserList.add(
                        UserCard(user.profilePicture, user.fullname, user.username)
                    )
                }
                Log.d("gotusers","$UserList")

            }




        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = binding.recyclerViewUser
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = UserAdapter(UserList, this)

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}





