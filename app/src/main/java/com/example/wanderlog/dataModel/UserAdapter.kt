package com.example.wanderlog.dataModel

import android.annotation.SuppressLint
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
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File


class UserAdapter(
    private var mList: List<UserCard>,
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: ImageView = itemView.findViewById(R.id.image_view)
        val username: TextView = itemView.findViewById(R.id.text_view_1)
        val fullname: TextView = itemView.findViewById(R.id.text_view_2)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: List<UserCard>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_user_layout, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val(imageResource, text1, text2, text3) = mList[position]
        if(imageResource!="") {
            val storageRef = Firebase.storage.reference.child(imageResource)
            val localFile = File.createTempFile(
                "tempImage1", ".jpg"
            )
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = correctImageOrientationFromFile(localFile.toString())
                holder.profilePicture.setImageBitmap(bitmap)
            }.addOnFailureListener {
                holder.profilePicture.setImageResource(R.drawable.baseline_image_24)
            }
        }
        holder.username.text = text1
        holder.fullname.text = text2
        val bundle = Bundle().apply {
            putString("userID", text3)
        }
        holder.itemView.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_searchNavigation_to_otherUserProfile, bundle)
                .onClick(holder.username)
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
        return mList.size
    }

}