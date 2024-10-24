package com.example.wanderlog.ui.home

import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {
    private lateinit var listView: ListView
    private lateinit var search_bar: SearchView

    // Define array adapter for ListView
    var adapter: ArrayAdapter<String>? = null

    // Define array List for List View data
    var mylist: ArrayList<String>? = null
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}

