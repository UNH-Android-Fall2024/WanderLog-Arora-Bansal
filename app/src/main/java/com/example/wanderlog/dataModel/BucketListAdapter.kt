// / https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView
package com.example.wanderlog.dataModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class BucketListAdapter(
    private val items: ArrayList<Location>
) : RecyclerView.Adapter<BucketListAdapter.BucketListViewHolder>() {

    inner class BucketListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: CheckBox = itemView.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bucket_list_item, parent, false)
        return BucketListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BucketListViewHolder, position: Int) {
        val item = items[position]
        holder.itemText.text = "${item.city}, ${item.country}"
        holder.itemText.isChecked = item.visited

        holder.itemText.setOnCheckedChangeListener { _, isChecked ->
            item.visited = isChecked
            updateDatabase(item.locationID, isChecked)
        }
    }
    private fun updateDatabase(itemId: String, isChecked: Boolean) {
        val db = Firebase.firestore
        db.collection("locations").document(itemId).update("visited", isChecked)
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
