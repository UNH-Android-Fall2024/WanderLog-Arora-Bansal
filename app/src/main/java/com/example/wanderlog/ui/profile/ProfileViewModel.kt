package com.example.wanderlog.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderlog.dataModel.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ProfileViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var db = Firebase.firestore
    private val details = MutableLiveData<User>().apply {
        db.collection("users").whereEqualTo("FirebaseAuthID",auth.currentUser!!.uid).get()
            .addOnSuccessListener {result ->
                for( document in result){
                    value = document.toObject(User::class.java)
                }
            }
    }

    val text: LiveData<User> = details
    fun getCurrentUserDetails():User{
        var user=User()
        db.collection("users").whereEqualTo("FirebaseAuthID",auth.currentUser!!.uid).get()
            .addOnSuccessListener {result ->
                for( document in result){
                    user = document.toObject(User::class.java)
                }
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