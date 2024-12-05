package com.example.wanderlog.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.dataModel.UserAdapter
import com.example.wanderlog.dataModel.UserCard
import com.example.wanderlog.databinding.FragmentSearchListBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.util.Locale
import com.example.wanderlog.dataModel.UserList
import com.google.firebase.auth.auth

/**
 * A fragment representing a list of Items.
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchListBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var mRecyclerView: RecyclerView
    private var auth = Firebase.auth
    private lateinit var searchView: SearchView
    private lateinit var adapter: UserAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        UserList.clear()
        db.collection("users")
            .whereNotEqualTo("FirebaseAuthID",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val user = document.toObject<User>()
                    UserList.add(
                        UserCard(user.profilePicture, user.fullname, user.username, user.FirebaseAuthID)
                    )
                }
            }
        mRecyclerView = binding.recyclerViewUser
        searchView = binding.searchBar
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = UserAdapter(UserList)
        adapter = mRecyclerView.adapter as UserAdapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
        return root
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<UserCard>()
            for (i in UserList) {
                if (i.fullname.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isNotEmpty()) {
                adapter.setFilteredList(filteredList)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}





