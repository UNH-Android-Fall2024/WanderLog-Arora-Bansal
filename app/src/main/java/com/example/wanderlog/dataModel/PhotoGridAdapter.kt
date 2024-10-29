package com.example.wanderlog.dataModel

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import java.io.File

// Adapter for the photo grid
class PhotoGridAdapter(
    private val context: Context,
    private val photoList: ArrayList<Post>,
) : RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    // ViewHolder for each grid item
    private var storage = Firebase.storage
    private var db = Firebase.firestore
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)


    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val post = photoList[position]

        Log.d("PostImage", post.imageUrl)
        val storageRef = storage.reference.child(post.imageUrl)
        val localFile = File.createTempFile(
            "tempImage", ".jpg"
        )
        storageRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.photoImageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            holder.photoImageView.setImageResource(R.drawable.baseline_image_24)
        }


        val bundle = Bundle().apply {
            db.collection("users").document(post.userID).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()!!
                    putString("username", user.username)
                    putString("profilePic", user.profilePicture)
                }

            putString("imageUrl", post.imageUrl.toString())
            putString("likes", "${post.likes.count().toString()} Likes")
            putString("comments", "${post.comments.count().toString()} Comments")
            putString("caption", post.content)

        }


        holder.itemView.setOnClickListener {
            Log.d("click", post.imageUrl)
            Navigation.createNavigateOnClickListener(R.id.action_showPhotosNavigation_to_postDetailFragment, bundle)
                .onClick(holder.itemView)

        }

    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}
