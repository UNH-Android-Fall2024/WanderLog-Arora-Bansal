package com.example.wanderlog.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.databinding.ActivityForgotPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private var auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email
        val send = binding.send
        send.setOnClickListener {
            auth.sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Email Sent.", Toast.LENGTH_LONG).show()
                    }
                }
            if(auth.currentUser!=null){
                auth.signOut()
            }
            val myIntent = Intent(
                this@ForgotPasswordActivity,
                LoginActivity::class.java
            )
            startActivity(myIntent)
        }
    }
}