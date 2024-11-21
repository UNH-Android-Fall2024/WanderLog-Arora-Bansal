package com.example.wanderlog.ui.signup
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.wanderlog.databinding.ActivityCreateBucketListBinding
import com.example.wanderlog.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CreateBucketList : AppCompatActivity() {
    private var auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityCreateBucketListBinding
    private val geocoder by lazy { Geocoder(this, Locale.getDefault()) }

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

    private fun getLocationCoordinates(city: String, country: String, callback: (Double, Double) -> Unit) {
        try {
            val locationName = "$city, $country"
            Log.d("Geocoding", "Attempting to get coordinates for: $locationName")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(locationName, 1) { addresses ->
                    val latitude = addresses.firstOrNull()?.latitude ?: 0.0
                    val longitude = addresses.firstOrNull()?.longitude ?: 0.0
                    Log.d("Geocoding", "Found coordinates for $locationName: $latitude, $longitude")
                    runOnUiThread {
                        callback(latitude, longitude)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(locationName, 1)
                val latitude = addresses?.firstOrNull()?.latitude ?: 0.0
                val longitude = addresses?.firstOrNull()?.longitude ?: 0.0
                Log.d("Geocoding", "Found coordinates for $locationName: $latitude, $longitude")
                callback(latitude, longitude)
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error getting coordinates for $city, $country", e)
            callback(0.0, 0.0)
        }
    }
    private fun AddItem(city: String, country: String) {

        if (country.isNotEmpty() && city.isNotEmpty()) {
        getLocationCoordinates(city, country) { latitude, longitude ->


            val location = hashMapOf(
                "city" to city,
                "country" to country,
                "userID" to auth.currentUser!!.uid,
                "latitude" to latitude,
                "longitude" to longitude,
                "visited" to false
            )

            // Add the new item to the list and notify the adapter
            db.collection("locations")
                .add(location)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "AddLocation",
                        "DocumentSnapshot written with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("AddLocation", "Error adding document", e)
                }
        }
        } else {
            // Show an error or prompt the user to fill both fields
            binding.countryInput.error = if (country.isEmpty()) "Please enter a country" else null
            binding.cityInput.error = if (city.isEmpty()) "Please enter a city" else null
        }
    }
}