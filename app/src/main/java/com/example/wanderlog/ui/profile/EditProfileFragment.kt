package com.example.wanderlog.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.FragmentEditProfileBinding
import com.example.wanderlog.ui.login.ForgotPasswordActivity
import com.example.wanderlog.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var currentUser : User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root



        ProfileViewModel.text.observe(viewLifecycleOwner) {
            currentUser = it
            binding.username.setText(it.username)
            binding.fullname.setText(it.fullname)
            binding.bio.setText(it.bio)
            binding.email.setText(it.email)
        }

        binding.resetPassword.setOnClickListener{
            Log.d("Reset Password", currentUser.email)
            val myIntent = Intent(
                activity,
                ForgotPasswordActivity::class.java
            )
            startActivity(myIntent)
        }

        binding.submit.setOnClickListener{
            if (binding.fullname.text.toString() != currentUser.fullname ||
                binding.bio.text.toString() != currentUser.bio){
                storeUserData(currentUser.FirebaseAuthID,binding.fullname.text.toString(), binding.bio.text.toString())
            }

            findNavController().navigate(R.id.action_editProfileNavigation_to_navigation_profile)
        }

        binding.logout.setOnClickListener{
            auth.signOut()
            val myIntent = Intent(
                activity,
                LoginActivity::class.java
            )
            startActivity(myIntent)
        }


        return root
    }

    private fun storeUserData(uid: String, name: String, bio:String ){
        val submit = hashMapOf(
            "bio" to bio,
            "fullname" to name,
        )
        db.collection("users").document(uid).set(submit, SetOptions.merge())
    }


}