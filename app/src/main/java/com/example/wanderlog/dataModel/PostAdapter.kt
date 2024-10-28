package com.example.wanderlog.dataModel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class PostAdapter(
    private val context: Context,
    private val postList: ArrayList<Post>,
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImage)
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val postImageView: ImageView = itemView.findViewById(R.id.postImage)
        val likesTextView: TextView = itemView.findViewById(R.id.likeCount)
        val commentsTextView: TextView = itemView.findViewById(R.id.viewComments)
        val caption: TextView = itemView.findViewById(R.id.postDescription)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_card_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        val db = Firebase.firestore
        val post = postList[position]
        db.collection("users").document(post.userID).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()!!
                holder.usernameTextView.text = user.username
            }
//        holder.profileImageView.setImageResource(post.)
//        holder.postImageView.setImageResource(post.postImageResId)
        holder.caption.text = post.content
        holder.likesTextView.text = "${post.likes.count()} likes"
        holder.commentsTextView.text ="${post.comments.count()} comments"
        val bundle = Bundle().apply {
            putString("userID", post.userID)
        }
        holder.itemView.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_navigation_home_to_otherUserProfile, bundle)
                .onClick(holder.usernameTextView)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
