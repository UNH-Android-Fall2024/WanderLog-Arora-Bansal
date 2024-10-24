package com.example.wanderlog.dataModel


class Post (
    val userID: String = "",
    val content: String = "",
    val comments: ArrayList<List<String>> = ArrayList(),
    val likes: ArrayList<String> = ArrayList(),
    val location: ArrayList<String> = ArrayList(),
    val imageURL: String = "")