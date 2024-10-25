package com.example.wanderlog.dataModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R

// Sample Photo data class (if your data has more fields, you can add them here)
data class Photo(val imageResId: Int)

// Adapter for the photo grid
class PhotoGridAdapter(
    private val context: Context,
    private val photoList: List<Photo>
) : RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    // ViewHolder for each grid item
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        holder.photoImageView.setImageResource(photo.imageResId)

        // You can add a click listener if needed
        holder.itemView.setOnClickListener {
            // Handle item click
        }
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}
