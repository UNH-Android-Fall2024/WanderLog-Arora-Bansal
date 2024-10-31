package com.example.wanderlog.dataModel


class Post (
    val userID: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val comments: ArrayList<HashMap<String,String>> = ArrayList(),
    val likes: ArrayList<String> = ArrayList(),
    val location: ArrayList<String> = ArrayList(),
    var postID: String = "",

    )

class Connection (
    val userID1: String = "",
    val userID2: String = "",

   )

