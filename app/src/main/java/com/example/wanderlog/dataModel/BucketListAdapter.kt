package com.example.wanderlog.dataModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class BucketListAdapter(
    private val items: ArrayList<Location>
) : RecyclerView.Adapter<BucketListAdapter.BucketListViewHolder>() {

    inner class BucketListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: CheckBox = itemView.findViewById(R.id.itemText)
        //val deleteButton: TextView = itemView.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bucket_list_item, parent, false)
        return BucketListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BucketListViewHolder, position: Int) {
        val item = items[position]
        Log.d("ShowLocations",item.city.toString())
        holder.itemText.text = "${item.city}, ${item.country}"
        holder.itemText.isChecked = item.visited

        holder.itemText.setOnCheckedChangeListener { _, isChecked ->
            item.visited = isChecked // Update the local data
            updateDatabase(item.locationID, isChecked) // Update the database
        }
        // Placeholder for checked/unchecked icon logic

        // Handle delete button visibility or swipe logic in item touch helper (not shown here)

    }
    private fun updateDatabase(itemId: String, isChecked: Boolean) {
        val db = Firebase.firestore
        val itemDocRef = db.collection("locations").document(itemId)
        itemDocRef.update("visited", isChecked)
            .addOnSuccessListener {
                Log.d("FirestoreUpdate", "Successfully updated item: $itemId")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Failed to update item: $itemId", e)
            }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}
