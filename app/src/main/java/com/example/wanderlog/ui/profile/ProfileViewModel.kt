package com.example.wanderlog.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileViewModel : ViewModel() {
    private var auth = Firebase.auth

    private val _text = MutableLiveData<String>().apply {
        val user = auth.currentUser
        value = "Welcome ${user!!.email.toString()}!"
    }
    val text: LiveData<String> = _text
}