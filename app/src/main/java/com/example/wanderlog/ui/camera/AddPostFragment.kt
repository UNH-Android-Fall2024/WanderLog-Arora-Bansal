package com.example.wanderlog.ui.camera

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.dataModel.Post
import com.example.wanderlog.databinding.FragmentAddPostBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class AddPostFragment : Fragment() {
    private lateinit var viewBinding: FragmentAddPostBinding
    private var imageUri: Uri? = null
    private var auth = Firebase.auth
    private val db = Firebase.firestore
    private var storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAddPostBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("imageUri", Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("imageUri")
        }

        imageUri?.let {
            viewBinding.postImage.setImageURI(it)
        } ?: run {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }

        viewBinding.postButton.setOnClickListener {
            uploadPost()
        }
    }

    private fun uploadPost() {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select an image.", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(viewBinding.captionInput.text.toString())) {
            Toast.makeText(requireContext(), "Please write a caption.", Toast.LENGTH_LONG).show()
            return
        }
        var uniqueID= System.currentTimeMillis()
        var postPath = "posts/${auth.currentUser!!.uid}/${uniqueID}.jpg"
        val storageRef = storage.reference
        val riversRef = storageRef.child(postPath)
        val uploadTask = riversRef.putFile(imageUri!!)

        uploadTask.addOnFailureListener {
            Log.d("uploadedfail",postPath)
        }.addOnSuccessListener { taskSnapshot ->
            val submit = Post(
                userID = auth.currentUser!!.uid,
                content = viewBinding.captionInput.text.toString(),
                imageUrl = postPath
            )
            db.collection("posts").document(uniqueID.toString()).set(submit, SetOptions.merge())
            Log.d("uploaded","Success $postPath")
            Toast.makeText(requireContext(), "Selected Image: $imageUri", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addPostFragment_to_navigation_home)

        }
    }
    companion object {
        fun newInstance(imageUri: Uri): AddPostFragment {
            return AddPostFragment().apply {
                arguments = bundleOf("imageUri" to imageUri)
            }
        }
    }
}
