package com.example.wanderlog.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderlog.dataModel.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ProfileViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var db = Firebase.firestore
    private val details = MutableLiveData<User>().apply {
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {documentSnapshot ->
                    value = documentSnapshot.toObject<User>()
            }
    }

    val text: LiveData<User> = details
    fun getCurrentUserDetails():User{
        var user=User()
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {documentSnapshot ->
                    user = documentSnapshot.toObject<User>()!!
            }
        return user
    }
    fun getPostCount(): Int{
        var count = 0
        db.collection("posts").whereEqualTo("userID",auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
            }
        return count
    }

    fun getFollowerCount(): Int{
        var count = 0
        db.collection("connections").whereEqualTo("userID1",auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
            }
        return count
    }

    fun getFollowingCount(): Int{
        var count = 0
        db.collection("connections").whereEqualTo("userID2",auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    count++
                }
            }
        return count
    }

}