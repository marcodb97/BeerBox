package com.marcodallaba.beerbox

import androidx.room.Room
import com.marcodallaba.beerbox.data.source.BeersDataSource
import com.marcodallaba.beerbox.data.source.BeersRepository
import com.marcodallaba.beerbox.data.source.local.BeersDatabase
import com.marcodallaba.beerbox.data.source.local.BeersLocalDataSource
import com.marcodallaba.beerbox.data.source.remote.BeersRemoteDataSource
import com.marcodallaba.beerbox.data.source.remote.PunkServices
import com.marcodallaba.beerbox.ui.MainActivity
import com.marcodallaba.beerbox.ui.beers.BeersFragment
import com.marcodallaba.beerbox.ui.beers.BeersViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinExperimentalAPI
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@KoinExperimentalAPI
val appModule = module {

    single {
        Room.databaseBuilder(androidApplication(), BeersDatabase::class.java, "beers-database")
            .build()
    }

    single {
        get<BeersDatabase>().beersDao()
    }

    single<BeersDataSource>(named(Qualifiers.LOCAL_DATA_SOURCE)) {
        BeersLocalDataSource(get())
    }

    single(named(Qualifiers.BASE_URL)) {
        "https://api.punkapi.com/v2/"
    }

    single<CallAdapter.Factory> {
        RxJava2CallAdapterFactory.createAsync()
    }

    single {
        OkHttpClient.Builder().also {
            if (BuildConfig.DEBUG) {
                val logger = HttpLoggingInterceptor()
                logger.level = HttpLoggingInterceptor.Level.BODY
                it.addInterceptor(logger)
            }
        }.build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<String>(named(Qualifiers.BASE_URL)))
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(get())
            .build()
    }

    single {
        get<Retrofit>().create(
            PunkServices::class.java
        )
    }

    single<BeersDataSource>(named(Qualifiers.REMOTE_DATA_SOURCE)) {
        BeersRemoteDataSource(get())
    }

    single {
        BeersRepository(
            get(named(Qualifiers.REMOTE_DATA_SOURCE)),
            get(named(Qualifiers.LOCAL_DATA_SOURCE))
        )
    }

    viewModel {
        BeersViewModel(get())
    }

    scope<MainActivity> {
        fragment {
            BeersFragment.newInstance()
        }
    }
}

enum class Qualifiers {
    BASE_URL,
    LOCAL_DATA_SOURCE,
    REMOTE_DATA_SOURCE
}
