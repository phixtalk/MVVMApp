package com.mvvmapp.ui.auth

import androidx.lifecycle.LiveData
import com.mvvmapp.data.db.entities.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user: User)
    fun onFailure(message: String)
}