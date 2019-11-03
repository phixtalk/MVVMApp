package com.mvvmapp.ui.home.quotes

import androidx.lifecycle.ViewModel
import com.mvvmapp.data.repositories.QuotesRepository
import com.mvvmapp.util.lazyDeferred

class QuotesViewModel(
    repository: QuotesRepository
) : ViewModel() {

    //initialize only when needed
    val quotes by lazyDeferred {
        repository.getQuotes()
    }
}