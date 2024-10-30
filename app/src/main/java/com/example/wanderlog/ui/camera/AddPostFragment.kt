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
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
//            val userID: String = "",
//    val content: String = "",
//    val imageUrl: String = "",
//    val comments: ArrayList<HashMap<String,String>> = ArrayList(),
//    val likes: ArrayList<String> = ArrayList(),
//    val location: ArrayList<String> = ArrayList(),
            val submit = Post(
                auth.currentUser!!.uid,
                viewBinding.captionInput.text.toString(),
                postPath
            )
            db.collection("posts").document(uniqueID.toString()).set(submit, SetOptions.merge())
            Log.d("uploaded","Success $postPath")
            Toast.makeText(requireContext(), "Selected Image: $imageUri", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addPostFragment_to_navigation_home)

        }
//        uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let { throw it }
//            }
//            fileRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            val imageUrl = task.result.toString()
//            val post = Post(auth.currentUser!!.uid,
//                viewBinding.captionInput.text.toString(), path
//            )
//
//            db.collection("posts").document(postId).set(post, SetOptions.merge())
//                .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Post uploaded successfully", Toast.LENGTH_LONG).show()
//
//                }
//                .addOnFailureListener { exception ->
//                    Toast.makeText(requireContext(), "Failed to upload post: ${exception.message}", Toast.LENGTH_SHORT).show()
//                }
//
//                }
//                        .addOnFailureListener { exception ->
//                            Toast.makeText(
//                                requireContext(),
//                                "Failed to upload post: ${exception.message}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//            .addOnFailureListener {
//            Toast.makeText(requireContext(), "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
//        }
    }
    companion object {
        fun newInstance(imageUri: Uri): AddPostFragment {
            return AddPostFragment().apply {
                arguments = bundleOf("imageUri" to imageUri)
            }
        }
    }
}
