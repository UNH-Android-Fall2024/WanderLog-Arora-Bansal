package com.example.wanderlog.dataModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R


class PostAdapter(
    private val context: Context,
    private val postList: List<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImage)
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val postImageView: ImageView = itemView.findViewById(R.id.postImage)
        val likesTextView: TextView = itemView.findViewById(R.id.likeCount)
//        val commentsTextView: TextView = itemView.findViewById(R.id.viewComments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_card_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
//        holder.profileImageView.setImageResource(post.)
        holder.usernameTextView.text = post.userID
//        holder.postImageView.setImageResource(post.postImageResId)
        holder.likesTextView.text = post.likes.count().toString()
//        holder.commentsTextView.text = post.comments
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
