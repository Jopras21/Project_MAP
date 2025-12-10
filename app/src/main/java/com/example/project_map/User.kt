package com.example.project_map

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var passwordHash: String = "",
    var photoUrl: String = "",
    var createdAt: Long = 0,
    var lastLogin: Long = 0
)
