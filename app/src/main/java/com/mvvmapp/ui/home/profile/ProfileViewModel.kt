package com.mvvmapp.ui.home.profile

import androidx.lifecycle.ViewModel
import com.mvvmapp.data.repositories.UserRepository

class ProfileViewModel(
    repository: UserRepository
) : ViewModel() {

    val user = repository.getUser()

}
