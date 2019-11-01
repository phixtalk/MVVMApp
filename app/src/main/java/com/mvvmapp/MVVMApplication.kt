package com.mvvmapp

import android.app.Application
import com.mvvmapp.data.db.AppDatabase
import com.mvvmapp.data.network.MyApi
import com.mvvmapp.data.network.NetworkConnectionInterceptor
import com.mvvmapp.data.repositories.UserRepository
import com.mvvmapp.ui.auth.AuthViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.generic.provider
import org.kodein.di.generic.instance


class MVVMApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        //bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
//        bind() from singleton { QuotesRepository(instance(), instance(), instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
//        bind() from provider { ProfileViewModelFactory(instance()) }
//        bind() from provider{ QuotesViewModelFactory(instance())}


    }

}