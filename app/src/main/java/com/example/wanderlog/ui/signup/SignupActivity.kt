package com.example.wanderlog.ui.signup
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.ActivitySignupBinding
import com.example.wanderlog.ui.login.LoginActivity
import com.example.wanderlog.ui.login.afterTextChanged
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySignupBinding
    private var userList: MutableList<User>? = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email
        val password = binding.password
        val login = binding.signup
        val loading = binding.loading
        val signin = binding.signin
        userList = mutableListOf()
        signin.setOnClickListener{

            val myIntent = Intent(
                this@SignupActivity,
                LoginActivity::class.java
            )
            startActivity(myIntent)
        }

        login.setOnClickListener {
            db.collection("users").whereEqualTo("username", binding.usernameId.text.toString())
                .get()
                .addOnSuccessListener { result ->

                    for (document in result) {
                        val user = document.toObject(User::class.java)
                        userList!!.add(user)
                    }
                }

            loading.visibility = View.VISIBLE
            createAccount(email.text.toString(), password.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
    }
    private fun verifyEnteredDetails() : Boolean{
        val pass = binding.password.text.toString()
        val cpass = binding.confirmpassword.text.toString()
        val email = binding.email.text.toString()
        val name = binding.fullname.text.toString()
        val username = binding.usernameId.text.toString()
        if (name != "" && email != "" && pass != "" && cpass != "" && username!="") {
            if (userList!!.size == 0 ) {
                if (pass == cpass) {
                    if (pass.length > 5) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            return true
                        } else {
                            binding.failed.text = "The email is incorrect!"
                            binding.failed.visibility = View.VISIBLE
                            return false
                        }
                    } else {
                        binding.failed.text = "The password must be atleast 6 characters long!"
                        binding.failed.visibility = View.VISIBLE
                        return false
                    }
                } else {
                    binding.failed.text = "Passwords do not match!"
                    binding.failed.visibility = View.VISIBLE
                    return false
                }
            }
            else{
                binding.failed.text = "This username already exists. Please choose a new one."
                binding.failed.visibility = View.VISIBLE
                return false
            }
        }
        else{
            binding.failed.text = "Please enter all the details to continue!"
            binding.failed.visibility = View.VISIBLE
            return false
        }
    }
    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        if (verifyEnteredDetails()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        user?.let {
                            // Name, email address, and profile photo Url
                            val name = binding.fullname.text.toString()
                            val username = binding.usernameId.text.toString()
                            val uid = it.uid
                            storeUserData(uid, name, email, username)
                        }
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    private fun storeUserData(uid: String, name: String, email: String, username:String ){

        val submit = hashMapOf(
            "bio" to "",
            "email" to email,
            "fullname" to name,
            "profilePicture" to "",
            "FirebaseAuthID" to uid,
            "username" to username,
        )
        // Add a new document with a generated ID
        db.collection("users").document(uid)
            .set(submit)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val myIntent = Intent(
                this@SignupActivity,
                CreateBucketList::class.java
            )
            startActivity(myIntent)
        } else {
            binding.email.text.clear()
            binding.password.text.clear()
            binding.fullname.text.clear()
            binding.loading.visibility = View.INVISIBLE

        }
    }

    companion object {
        private const val TAG = "Login"
    }


}
