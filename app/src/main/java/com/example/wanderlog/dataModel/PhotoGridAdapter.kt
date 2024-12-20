package com.example.wanderlog.dataModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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

class PhotoGridAdapter(
    private val context: Context,
    private val photoList: ArrayList<Post>,
) : RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

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
        val storageRef = storage.reference.child(post.imageUrl)
        val localFile = File.createTempFile(
            "tempImage", ".jpg"
        )
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = correctImageOrientationFromFile(localFile.toString())
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

            putString("imageUrl", post.imageUrl)
            putInt("likes", post.likes.count())
            putInt("comments", post.comments.count())
            putString("caption", post.content)
            putString("postID", post.postID)
            putString("userID", post.userID)
            if (post.location.size>0) {
                putDouble("latitude", post.location[0])
                putDouble("longitude", post.location[1])
            }
            if(post.userID in post.likes){
                putBoolean("liked", true )
            }
            else{
                putBoolean("liked", false )
            }

        }
        holder.itemView.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_showPhotosNavigation_to_postDetailFragment, bundle)
                .onClick(holder.itemView)

        }

    }
    private fun correctImageOrientationFromFile(imagePath: String): Bitmap? {
        try {
            val exifInterface = ExifInterface(imagePath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val bitmap = BitmapFactory.decodeFile(imagePath)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap // No rotation needed
            }
        } catch (e: Exception) {
            Log.e("ImageRotationError", "Error correcting image orientation: ${e.message}")
        }
        return null
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}
