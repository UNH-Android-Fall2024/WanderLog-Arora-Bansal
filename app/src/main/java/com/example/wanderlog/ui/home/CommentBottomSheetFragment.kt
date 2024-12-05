package com.example.wanderlog.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanderlog.dataModel.CommentAdapter
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.FragmentCommentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class CommentBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCommentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private var auth = Firebase.auth
    private var username = ""
    private lateinit var commentAdapter: CommentAdapter
    private var comments: ArrayList<HashMap<String,String>> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBottomSheetBinding.inflate(inflater, container, false)

        val postID = arguments?.getString(ARG_POST_ID)

        commentAdapter = CommentAdapter(comments)
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewComments.adapter = commentAdapter

        db.collection("posts").document(postID.toString()).get()
            .addOnSuccessListener {documentSnapshot ->
                val post = documentSnapshot.toObject<Post>()
                if (post != null && post.comments.size != 0 ) {
                    post.comments.forEach { hashMap ->
                            comments.add(hashMap)

                        }
                    }
                commentAdapter.notifyDataSetChanged()
                }

        binding.submitComment.setOnClickListener{
            if(binding.newComment.text.toString() != ""){
                if (postID != null) {
                    postComment(postID)
                }
            }
        }
        return binding.root
    }

    private fun postComment(postID: String){

        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                username = user!!.username
                Log.d("PostComment",username)
                var newComment = hashMapOf("comment" to binding.newComment.text.toString(), "username" to username )
                comments.add(newComment)
                commentAdapter.notifyDataSetChanged()
                db.collection("posts").document(postID).update("comments", FieldValue.arrayUnion(newComment))
                binding.newComment.setText("")
            }


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
