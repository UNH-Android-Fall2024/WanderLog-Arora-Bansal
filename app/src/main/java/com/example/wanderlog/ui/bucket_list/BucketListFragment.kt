package com.example.wanderlog.ui.bucket_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wanderlog.databinding.FragmentBucketListBinding

class BucketListFragment : Fragment() {

    private var _binding: FragmentBucketListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bucketListViewModel =
            ViewModelProvider(this).get(BucketListViewModel::class.java)

        _binding = FragmentBucketListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textBucketList
        bucketListViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}