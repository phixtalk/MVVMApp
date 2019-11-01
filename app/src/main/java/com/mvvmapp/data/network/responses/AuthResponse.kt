package com.mvvmapp.data.network.responses

import com.mvvmapp.data.db.entities.User

data class AuthResponse(
    val isSuccessful : Boolean?,
    val message: String?,
    val user: User?
)