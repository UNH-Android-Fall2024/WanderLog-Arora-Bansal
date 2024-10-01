package com.example.wanderlog.ui.signup
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlog.databinding.ActivitySignupBinding
import com.example.wanderlog.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySignupBinding

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

        signin.setOnClickListener{

            val myIntent = Intent(
                this@SignupActivity,
                LoginActivity::class.java
            )
            startActivity(myIntent)
        }

        login.setOnClickListener {
            Log.d("createUser","Continue clicked")
            loading.visibility = View.VISIBLE
            createAccount(email.text.toString(), password.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    // [END on_start_check_user]
    private fun verifyEnteredDetails() : Boolean{
        Log.d("Signup1", "Reached here" )

        val pass = binding.password.text.toString()
        val cpass = binding.confirmpassword.text.toString()
        val email = binding.email.text.toString()
        val name = binding.fullname.text.toString()
        val bool = pass == cpass
        if (name != "" && email != "" && pass != "" && cpass != "") {
            Log.d(/* tag = */ "Signup1",/* msg = */ "$bool")
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
            binding.failed.text = "Please enter all the details to continue!"
            binding.failed.visibility = View.VISIBLE
            return false
        }
    }
    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]



        if (verifyEnteredDetails()) {
            Log.d("Signup1", "Success?" )
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        user?.let {
                            // Name, email address, and profile photo Url
                            val name = binding.fullname.text.toString()
                            val uid = it.uid
                            Log.d("UserDetails", "$name $email $uid")
                            storeUserData(uid, name, email)
                        }
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
        else{
            Log.d("Signup","Fail")
        }

        // [END create_user_with_email]
    }

    private fun storeUserData(uid: String, name: String, email: String ){

        val submit = hashMapOf(
            "bio" to "",
            "email" to email,
            "fullname" to name,
            "profilePicture" to "",
            "FirebaseAuthID" to uid,
        )
        // Add a new document with a generated ID
        db.collection("users")
            .add(submit)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
                Log.d("Verification","$task")
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val myIntent = Intent(
                this@SignupActivity,
                LoginActivity::class.java
            )
            startActivity(myIntent)
        } else {
            binding.email.text.clear()
            binding.password.text.clear()
            binding.fullname.text.clear()
            binding.loading.visibility = View.INVISIBLE

        }
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "Login"
    }


}
