package com.example.wanderlog.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.wanderlog.dataModel.CommentAdapter
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.databinding.FragmentCommentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.divider.MaterialDivider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class CommentBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCommentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var db = Firebase.firestore
    private lateinit var commentAdapter: CommentAdapter
    private var comments: ArrayList<HashMap<String,String>> = arrayListOf() // Pass this data to the fragment or fetch from ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
//                            Log.d("Show Comment 1",hashMap.toString())
                            comments.add(hashMap)

                        }
                    }
                commentAdapter.notifyDataSetChanged()
                Log.d("ShowComment", comments.toString())
                }









        return binding.root
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
