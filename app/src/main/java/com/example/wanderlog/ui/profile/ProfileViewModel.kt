package com.example.wanderlog.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderlog.dataModel.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
                    Log.d("profileView","${value}")
                }
            }
    }
    val text: LiveData<User> = details
}