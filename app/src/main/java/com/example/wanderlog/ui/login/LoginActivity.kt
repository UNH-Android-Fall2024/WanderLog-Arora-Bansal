package com.example.wanderlog.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wanderlog.MainActivity
import com.example.wanderlog.databinding.ActivityLoginBinding
import com.example.wanderlog.ui.signup.SignupActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private var auth = Firebase.auth
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val signup = binding.signup

        signup!!.setOnClickListener {
            val myIntent = Intent(
                this@LoginActivity,
                SignupActivity::class.java
            )
            startActivity(myIntent)
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        binding.forgotPassword!!.setOnClickListener {
            val myIntent = Intent(
                this@LoginActivity,
                ForgotPasswordActivity::class.java
            )
            startActivity(myIntent)
        }

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        signIn(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                binding.failed!!.visibility  = View.GONE
                signIn(username.text.toString(),password.text.toString())
            }


        }
    }
    public override fun onStart() {
        super.onStart()
    }

    private fun signIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        binding.failed!!.visibility = View.VISIBLE
                        binding.loading.visibility = View.GONE

                    }
                }
        }
        catch(e: Throwable){
            Log.e(TAG, "Email Not Found?")
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            val myIntent = Intent(
                this@LoginActivity,
                MainActivity::class.java
            )
            startActivity(myIntent)
        }
        else{
            binding.username.text.clear()
            binding.password.text.clear()
            binding.loading.visibility = View.INVISIBLE
            showLoginFailed()

        }
    }
    companion object {
        private const val TAG = "Login"
    }

    private fun showLoginFailed() {
        Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Toast.makeText(applicationContext, "You are not logged in", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })

}