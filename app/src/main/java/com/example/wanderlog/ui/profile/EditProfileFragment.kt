package com.example.wanderlog.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wanderlog.R
import com.example.wanderlog.R.*
import com.example.wanderlog.dataModel.User
import com.example.wanderlog.databinding.ActivityMainBinding
import com.example.wanderlog.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private var currentUser= User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_edit_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

        currentUser = profileViewModel.getCurrentUserDetails()
//         {
//            Log.d("editProfileSubmit", "Button clicked")
//            findNavController().navigate(R.id.action_navigation_profile_to_editProfileNavigation)
//         }


    }


}