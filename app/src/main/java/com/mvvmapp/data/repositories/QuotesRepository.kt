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
//import jdk.nashorn.internal.objects.NativeDate.getTime
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.text.SimpleDateFormat
import java.util.*


private val MINIMUM_INTERVAL = 6
private val MILLIS_TO_HOURS = 3600000

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

        if (lastSavedAt == null || isFetchNeeded(lastSavedAt)) {
            println("TRACK_ isFetched: true")

            try {
                val response = apiRequest { api.getQuotes() }
                quotes.postValue(response.quotes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isFetchNeeded(savedAt: String): Boolean {
        //return ChronoUnit.HOURS.between(savedAt, LocalDateTime.now()) > MINIMUM_INTERVAL
        return getHoursElapsed(savedAt, getTimeNow(), MINIMUM_INTERVAL)
    }


    private fun saveQuotes(quotes: List<Quote>) {
        Coroutines.io {
            prefs.savelastSavedAt(getTimeNow())
            db.getQuoteDao().saveAllQuotes(quotes)
        }
    }

    private fun getTimeNow(): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun getHoursElapsed(firstDate: String, secondDate: String, hoursVal: Int) : Boolean {
        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
        val startDate = simpleDateFormat.parse(firstDate)
        val endDate = simpleDateFormat.parse(secondDate)

        val hoursMilli: Int = MILLIS_TO_HOURS * hoursVal

        val timeDiff = endDate.getTime() - startDate.getTime()

        return  timeDiff > hoursMilli
    }

}