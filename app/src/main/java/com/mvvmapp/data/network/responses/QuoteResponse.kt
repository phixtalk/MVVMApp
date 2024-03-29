package com.mvvmapp.data.network.responses

import com.mvvmapp.data.db.entities.Quote

data class QuotesResponse (
    val isSuccessful: Boolean,
    val quotes: List<Quote>
)