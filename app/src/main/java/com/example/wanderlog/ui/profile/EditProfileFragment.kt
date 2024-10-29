package com.example.wanderlog.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import java.io.File


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentUser : User
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var storage = Firebase.storage
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getUserDetails()
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openGallery() // If permission is granted, open the gallery
            } else {
                Toast.makeText(requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImageUri = result.data!!.data
                // Handle the selected image here
                selectedImageUri?.let { uploadImage(it) }
            }
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

        binding.profilePicture.setOnClickListener{
            checkGalleryPermission()
        }


        return root
    }

    private fun uploadImage(image:Uri){
        val storageRef = storage.reference
        val riversRef = storageRef.child("profile/${auth.currentUser!!.uid}.jpg")
        val uploadTask = riversRef.putFile(image)
        Log.d("uploaded",image.toString())

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Log.d("uploadedfail","profile/${auth.currentUser!!.uid}.jpg")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            val submit = hashMapOf(
                "profilePicture" to "profile/${auth.currentUser!!.uid}.jpg"
            )
            db.collection("users").document(auth.currentUser!!.uid).set(submit, SetOptions.merge())
            Log.d("uploaded","Success profile/${auth.currentUser!!.uid}.jpg")
            Toast.makeText(requireContext(), "Selected Image: $image", Toast.LENGTH_SHORT).show()

        }
    }
    // check if gallery permission is granted
    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                // Request permission for Android 13+
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // For below Android 13
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                // Request permission for below Android 13
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)


    }


    private fun getUserDetails(){
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()!!
                currentUser = user
                binding.username.setText(user.username)
                binding.fullname.setText(user.fullname)
                binding.bio.setText(user.bio)
                binding.email.setText(user.email)
                if(user.profilePicture!=""){
                    val storageRef = storage.reference.child(user.profilePicture)
                    val localFile = File.createTempFile(
                        "tempImage", ".jpg"
                    )
                    storageRef.getFile(localFile).addOnSuccessListener {
                        // Local temp file has been created
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.profilePictureImage.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                        binding.profilePictureImage.setImageResource(R.drawable.baseline_person_24)
                    }
                }
            }

    }

    private fun storeUserData(uid: String, name: String, bio:String ){
        val submit = hashMapOf(
            "bio" to bio,
            "fullname" to name,
        )
        db.collection("users").document(uid).set(submit, SetOptions.merge())
    }


}