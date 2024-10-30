package com.example.wanderlog.dataModel

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import java.io.File


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
        val caption : TextView = itemView.findViewById(R.id.postDescription)
        val postUsername: TextView = itemView.findViewById(R.id.postUsername)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val likedButton: ImageView = itemView.findViewById(R.id.likedButton)
        val commentButton: ImageView = itemView.findViewById(R.id.commentButton)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_card_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val storage = Firebase.storage
        val db = Firebase.firestore
        val post = postList[position]
        db.collection("users").document(post.userID).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()!!
                holder.usernameTextView.text = user.username
                if(post.content == ""){
                    holder.postUsername.text = ""
                }
                else{
                    holder.postUsername.text = user.username
                }
                    if(user.profilePicture!="") {
                    val storageRef1 = storage.reference.child(user.profilePicture.toString())
                    val localFile1 = File.createTempFile(
                        "tempImage1", ".jpg"
                    )
                    storageRef1.getFile(localFile1).addOnSuccessListener {
                        // Local temp file has been created
                        val bitmap = BitmapFactory.decodeFile(localFile1.absolutePath)
                        holder.profileImageView.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        holder.profileImageView.setImageResource(R.drawable.baseline_image_24)
                    }
                }
            }

        val storageRef = storage.reference.child(post.imageUrl.toString())
        val localFile = File.createTempFile(
            "tempImage", ".jpg"
        )
        storageRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.postImageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            holder.postImageView.setImageResource(R.drawable.baseline_image_24)
        }

        if(post.userID in post.likes){
            holder.likeButton.visibility = View.GONE
            holder.likedButton.visibility = View.VISIBLE
        }
        holder.caption.text = post.content
        holder.likesTextView.text = "${post.likes.count()} Likes"
        holder.commentsTextView.text ="${post.comments.count()} Comments"
        val bundle = Bundle().apply {
            putString("userID", post.userID)
        }
        holder.itemView.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_navigation_home_to_otherUserProfile, bundle)
                .onClick(holder.usernameTextView)
        }
        var likeCount = post.likes.count()

        holder.likeButton.setOnClickListener{
            if (post.postID != null) {
                db.collection("posts").document(post.postID).update("likes", FieldValue.arrayUnion(post.userID))
            }
            holder.likeButton.visibility = View.GONE
            holder.likedButton.visibility = View.VISIBLE
            likeCount = likeCount+1
            holder.likesTextView.text = "${likeCount} Likes"
        }
        holder.likedButton.setOnClickListener{
            if (post.postID != null) {
                db.collection("posts").document(post.postID).update("likes", FieldValue.arrayRemove(post.userID))
            }
            holder.likedButton.visibility = View.GONE
            holder.likeButton.visibility = View.VISIBLE
            likeCount = likeCount-1
            holder.likesTextView.text = "${likeCount} Likes"
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
