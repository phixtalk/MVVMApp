package com.mvvmapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mvvmapp.data.db.AppDatabase
import com.mvvmapp.data.db.entities.Quote
import com.mvvmapp.data.network.MyApi
import com.mvvmapp.data.network.SafeApiRequest
import com.mvvmapp.data.preferences.PreferenceProvider
import com.mvvmapp.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private val MINIMUM_INTERVAL = 6

class QuotesRepository(
    private val api: MyApi,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : SafeApiRequest() {

    private val quotes = MutableLiveData<List<Quote>>()

    init {
        quotes.observeForever {
            saveQuotes(it)
        }
    }

    suspend fun getQuotes(): LiveData<List<Quote>> {
        return withContext(Dispatchers.IO) {
            fetchQuotes()
            db.getQuoteDao().getQuotes()
        }
    }

    private suspend fun fetchQuotes() {
        val lastSavedAt = prefs.getLastSavedAt()

        if (lastSavedAt == null || isFetchNeeded(LocalDateTime.parse(lastSavedAt))) {
            try {
                val response = apiRequest { api.getQuotes() }
                quotes.postValue(response.quotes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isFetchNeeded(savedAt: LocalDateTime): Boolean {
        return ChronoUnit.HOURS.between(savedAt, LocalDateTime.now()) > MINIMUM_INTERVAL
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveQuotes(quotes: List<Quote>) {
        Coroutines.io {
            prefs.savelastSavedAt(LocalDateTime.now().toString())
            db.getQuoteDao().saveAllQuotes(quotes)
        }
    }

}