package com.example.wanderlog.ui.bucket_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BucketListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Bucket List Fragment"
    }
    val text: LiveData<String> = _text
}