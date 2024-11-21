package com.example.wanderlog.dataModel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R


// In your Kotlin code:
class CommentAdapter(
    private val comments: ArrayList<HashMap<String,String>>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentusername : TextView = itemView.findViewById(R.id.commentUsername)
        val textComment: TextView = itemView.findViewById(R.id.textComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentusername.text = comment["username"]
        holder.textComment.text = comment["comment"]
        Log.d("Comment", comment.toString())

    }

    override fun getItemCount() = comments.size
}
