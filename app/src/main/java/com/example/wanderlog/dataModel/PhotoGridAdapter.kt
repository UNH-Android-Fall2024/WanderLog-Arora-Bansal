package com.example.wanderlog.dataModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanderlog.R

// Adapter for the photo grid
class PhotoGridAdapter(
    private val context: Context,
    private val photoList: ArrayList<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    // ViewHolder for each grid item
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
        init {
            // Set click listener on the entire item view
            itemView.setOnClickListener {
                // Call the onItemClick function with the current item
                onItemClick(photoList[adapterPosition])
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val post = photoList[position]
        Glide.with(context)
            .load(post.imageURL) // Load image from URL
            .placeholder(R.drawable.baseline_image_24) // Optional placeholder
            .into(holder.photoImageView)
//        holder.photoImageView.setImageResource(photo)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}
