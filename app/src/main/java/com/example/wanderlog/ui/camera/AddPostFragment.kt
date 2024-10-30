package com.example.wanderlog.ui.camera

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
    private lateinit var viewBinding: FragmentAddPostBinding
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPictureRef: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAddPostBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storagePostPictureRef = FirebaseStorage.getInstance().reference.child("Post Picture")

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

        val fileRef = storagePostPictureRef!!.child("${System.currentTimeMillis()}.jpg")
        val uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                myUrl = task.result.toString()

                val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                val postId = ref.push().key!!

                val postMap = hashMapOf(
                    "caption" to viewBinding.captionInput.text.toString(),
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
