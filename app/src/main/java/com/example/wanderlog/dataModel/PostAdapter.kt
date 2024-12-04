package com.example.wanderlog.dataModel

import androidx.fragment.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.example.wanderlog.ui.home.CommentBottomSheetFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import java.io.File
import java.util.Locale


class PostAdapter(
    private val context: Context,
    private val fragment: Fragment,
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
        val locationText: TextView = itemView.findViewById(R.id.location)



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
                    val storageRef1 = storage.reference.child(user.profilePicture)
                    val localFile1 = File.createTempFile(
                        "tempImage1", ".jpg"
                    )
                    storageRef1.getFile(localFile1).addOnSuccessListener {
                        // Local temp file has been created
                        val bitmap = correctImageOrientationFromFile(localFile1.toString())
                        holder.profileImageView.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        holder.profileImageView.setImageResource(R.drawable.baseline_image_24)
                    }
                }
            }
        if(post.location.size>0){
            getLocationFromCoordinates(holder, post.location[0], post.location[1])
        }
        else{
            holder.locationText.visibility = View.GONE
        }
        if (post.imageUrl!="") {
            val storageRef = storage.reference.child(post.imageUrl.toString())
            val localFile = File.createTempFile(
                "tempImage", ".jpg"
            )
            storageRef.getFile(localFile).addOnSuccessListener {
                // Local temp file has been created
                val bitmap = correctImageOrientationFromFile(localFile.toString())
                holder.postImageView.setImageBitmap(bitmap)
            }.addOnFailureListener {
                holder.postImageView.setImageResource(R.drawable.baseline_image_24)
            }
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
            if (post.postID != "") {
                db.collection("posts").document(post.postID).update("likes", FieldValue.arrayUnion(post.userID))
            }
            holder.likeButton.visibility = View.GONE
            holder.likedButton.visibility = View.VISIBLE
            likeCount = likeCount+1
            holder.likesTextView.text = "${likeCount} Likes"
        }
        holder.likedButton.setOnClickListener{
            if (post.postID != "") {
                db.collection("posts").document(post.postID).update("likes", FieldValue.arrayRemove(post.userID))
            }
            holder.likedButton.visibility = View.GONE
            holder.likeButton.visibility = View.VISIBLE
            likeCount = likeCount-1
            holder.likesTextView.text = "${likeCount} Likes"
        }

        holder.commentButton.setOnClickListener{
            val bottomSheetFragment = CommentBottomSheetFragment.newInstance(post.postID)
            bottomSheetFragment.show(fragment.parentFragmentManager,"Comment Box")
        }
    }
    private fun getLocationFromCoordinates(holder: PostViewHolder, latitude: Double, longitude: Double) {
        val geocoder by lazy { Geocoder(holder.itemView.context, Locale.getDefault()) }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val state = address.adminArea
                        val country = address.countryName
                        holder.itemView.post {
                            if (state != null && country != null) {
                                holder.locationText.visibility = View.VISIBLE
                                holder.locationText.text = "$state, $country"
                            } else {
                                holder.locationText.visibility = View.GONE
                            }
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val state = address.adminArea
                    val country = address.countryName
                    holder.itemView.post {
                        if (state != null && country != null) {
                            holder.locationText.visibility = View.VISIBLE
                            holder.locationText.text = "$state, $country"
                        } else {
                            holder.locationText.visibility = View.GONE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PostDetail", "Error getting location name", e)
            holder.itemView.post {
                holder.locationText.visibility = View.GONE
            }
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
        return postList.size
    }
}
