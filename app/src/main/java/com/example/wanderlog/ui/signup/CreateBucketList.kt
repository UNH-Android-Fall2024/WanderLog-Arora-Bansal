package com.example.wanderlog.ui.signup
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.databinding.ActivityCreateBucketListBinding
import com.example.wanderlog.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CreateBucketList : AppCompatActivity() {
    private var auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityCreateBucketListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateBucketListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signupButton.setOnClickListener {
            var country = binding.countryInput.text.toString()
            var city = binding.cityInput.text.toString()
            if(country!="" && city !=""){
                AddItem(city, country)
                val myIntent = Intent(
                    this@CreateBucketList,
                    LoginActivity::class.java
                )
                startActivity(myIntent)
            }

        }
    }


    private fun AddItem(city: String, country: String) {
        // Inflate the dialog layout
        val location = hashMapOf(
            "city" to city,
            "country" to country,
            "userID" to auth.currentUser!!.uid,
            "latitude" to 0.0,
            "longitude" to 0.0,
            "visited" to false
        )
        if (country.isNotEmpty() && city.isNotEmpty()) {
            // Add the new item to the list and notify the adapter
            db.collection("locations")
                .add(location)
                .addOnSuccessListener { documentReference ->
                    Log.d("AddLocation", "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("AddLocation", "Error adding document", e)
                }
        } else {
            // Show an error or prompt the user to fill both fields
            binding.countryInput.error = if (country.isEmpty()) "Please enter a country" else null
            binding.cityInput.error = if (city.isEmpty()) "Please enter a city" else null
        }
    }
}