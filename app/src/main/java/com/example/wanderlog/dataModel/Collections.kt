package com.example.wanderlog.dataModel

class User (
    val email: String = "",
    val fullname: String = "",
    val username: String = "",
    val bio: String = "",
    val FirebaseAuthID: String = "",
    val profilePicture: String = "") {

}

class Post (
    var postID: String = "",
    val userID: String = "",
    val content: String = "",
    val comments: ArrayList<HashMap<String,String>> = ArrayList(),
    val likes: ArrayList<String> = ArrayList(),
    val location: ArrayList<Double> = ArrayList(),
    val imageUrl: String = "")

class Connection (
    val userID1: String = "",
    val userID2: String = "",

   )

class Location (
    var locationID: String = "",
    val userID: String = "",
    val city: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var visited: Boolean = false)