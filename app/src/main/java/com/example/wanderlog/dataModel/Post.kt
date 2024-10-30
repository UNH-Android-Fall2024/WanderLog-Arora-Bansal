package com.example.wanderlog.dataModel


class Post (
    val userID: String = "",
    val content: String = "",
    val imageURL: String = "",
    val comments: ArrayList<List<String>> = ArrayList(),
    val likes: ArrayList<String> = ArrayList(),
    val location: ArrayList<String> = ArrayList(),
    )

class Connection (
    val userID1: String = "",
    val userID2: String = "",

   )

