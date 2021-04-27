package com.marcodallaba.beerbox.data.source.local

import com.marcodallaba.beerbox.data.Beer
import com.marcodallaba.beerbox.data.source.BeersDataSource
import com.marcodallaba.beerbox.util.BeerType
import io.reactivex.Single

class BeersLocalDataSource constructor(private val beersDao: BeersDao) : BeersDataSource {

    override fun getBeers(page: Int, perPage: Int): Single<List<Beer>>? = null

    override fun getBeers() = beersDao.getBeers()

    override fun insertBeers(beers: List<Beer>) {
        beersDao.insertBeers(beers)
    }

    override fun getBeerTypes() = BeerType.values()
        .filter { it != BeerType.UNKNOWN }
        .sortedBy { it.displayName }

}