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
            .addOnSuccessListener { documentSnapshot ->
                value = documentSnapshot.toObject<User>()
            }
    }

    val text: LiveData<User> = details





}