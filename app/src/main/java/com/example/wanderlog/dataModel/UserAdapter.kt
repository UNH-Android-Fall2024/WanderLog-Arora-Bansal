package com.example.wanderlog.dataModel

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.example.wanderlog.ui.home.SearchFragment

class UserAdapter(
    private var mList: List<UserCard>,
    private val context: SearchFragment
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

//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
////        holder.profilePicture.setImageResource(mList[position].profilePicture)
//        holder.username.text = mList[position].username
//        holder.username.text = mList[position].fullname
//
//    }
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val(imageResource, text1, text2) = mList[position]
        holder.username.text = text1
        holder.fullname.text = text2
        //holder.mImageView.setImageResource(imageResource)
        holder.itemView.setOnClickListener {
            Log.d("MYTEST","Position $position")
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}