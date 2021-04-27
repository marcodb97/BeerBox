package com.marcodallaba.beerbox.data.source.remote

import androidx.lifecycle.LiveData
import com.marcodallaba.beerbox.data.Beer
import com.marcodallaba.beerbox.data.source.BeersDataSource
import com.marcodallaba.beerbox.util.BeerType
import io.reactivex.Single

class BeersRemoteDataSource constructor(private val punkServices: PunkServices) :
    BeersDataSource {

    override fun insertBeers(beers: List<Beer>) {}

    override fun getBeers(): LiveData<List<Beer>>? = null

    override fun getBeers(page: Int, perPage: Int): Single<List<Beer>> = punkServices.getBeers(page, perPage)

    override fun getBeerTypes(): List<BeerType>? = null
}