package com.marcodallaba.beerbox.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.marcodallaba.beerbox.data.Beer
import io.reactivex.disposables.CompositeDisposable

class BeersRepository constructor(
    private val beersRemoteDataSource: BeersDataSource,
    private val beersLocalDataSource: BeersDataSource
) {

    private val compositeDisposable = CompositeDisposable()
    var currentPage = 0
    var reachedLastBeer = false
    var isLoading = false

    fun <T> getBeers(perPage: Int, mapper: (Beer) -> (T)): LiveData<List<T>>? {
        if (!reachedLastBeer && !isLoading) {
            currentPage++
            isLoading = true
            beersRemoteDataSource.getBeers(currentPage, perPage)?.subscribe({
                isLoading = false
                reachedLastBeer = it.isEmpty() && currentPage > 0
                beersLocalDataSource.insertBeers(it)
            }, {
                currentPage--
                isLoading = false
            })?.let { compositeDisposable.add(it) }
        }
        return beersLocalDataSource.getBeers()?.let {
            Transformations.map(it) { beers ->
                beers.map { beerItem -> mapper.invoke(beerItem) }
            }
        }
    }

    fun getBeerTypes() = beersLocalDataSource.getBeerTypes()

    fun dispose() {
        compositeDisposable.dispose()
    }

}