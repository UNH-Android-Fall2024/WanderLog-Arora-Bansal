package com.example.wanderlog.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.CommentAdapter
import com.example.wanderlog.dataModel.Post
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class CommentBottomSheetFragment : BottomSheetDialogFragment() {
    private var db = Firebase.firestore
    private lateinit var recyclerViewComments: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private var comments: ArrayList<HashMap<String,String>> = arrayListOf() // Pass this data to the fragment or fetch from ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false)
        val postID = arguments?.getString(ARG_POST_ID)
        Log.d("Comment", postID.toString())


        db.collection("posts").document(postID.toString()).get()
            .addOnSuccessListener {documentSnapshot ->
                val post = documentSnapshot.toObject<Post>()
                if (post != null) {
                    comments = post.comments
                }
                Log.d("Comment", comments[0].toString())
                commentAdapter.notifyDataSetChanged()
            }

        recyclerViewComments = view.findViewById(R.id.recyclerViewComments)
        commentAdapter = CommentAdapter(comments)
        recyclerViewComments.adapter = commentAdapter
        recyclerViewComments.layoutManager = LinearLayoutManager(context)
        return view
    }

    companion object {
        private const val ARG_POST_ID = "post_id"

        fun newInstance(postID: String): CommentBottomSheetFragment {
            val fragment = CommentBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_POST_ID, postID)
            fragment.arguments = args
            return fragment
        }
    }
}
