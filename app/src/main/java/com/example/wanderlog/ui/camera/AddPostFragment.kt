package com.example.wanderlog.ui.camera

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.wanderlog.R
import com.example.wanderlog.databinding.FragmentAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class AddPostFragment : Fragment() {

    private lateinit var viewbinding: FragmentAddPostBinding
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPictureRef: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewbinding = FragmentAddPostBinding.inflate(inflater, container, false)
        return viewbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewbinding.progressBar.visibility = View.GONE
        storagePostPictureRef = FirebaseStorage.getInstance().reference.child("Post Picture")

        // Retrieve and display image URI
        imageUri = arguments?.getParcelable("imageUri")
        if (imageUri != null) {
            viewbinding.postImage.setImageURI(imageUri)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }

        viewbinding.postButton.setOnClickListener {
            uploadPost()
        }
    }


    private fun uploadPost() {
        // Show the ProgressBar
        viewbinding.progressBar.visibility = View.VISIBLE

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select an image.", Toast.LENGTH_LONG).show()
            viewbinding.progressBar.visibility = View.GONE
            return
        }
        if (TextUtils.isEmpty(viewbinding.captionInput.text.toString())) {
            Toast.makeText(requireContext(), "Please write a caption.", Toast.LENGTH_LONG).show()
            viewbinding.progressBar.visibility = View.GONE
            return
        }

        val fileRef = storagePostPictureRef!!.child("${System.currentTimeMillis()}.jpg")
        val uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            viewbinding.progressBar.visibility = View.GONE // Hide ProgressBar
            if (task.isSuccessful) {
                myUrl = task.result.toString()

                // Save post details to Firebase Database
                val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                val postId = ref.push().key!!

                val postMap = hashMapOf(
                    "caption" to viewbinding.captionInput.text.toString(),
                    "userID" to FirebaseAuth.getInstance().currentUser!!.uid,
                    "imageUrl" to myUrl
                )

                ref.child(postId).updateChildren(postMap as Map<String, Any>).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Post uploaded successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to upload post.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Upload failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
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
