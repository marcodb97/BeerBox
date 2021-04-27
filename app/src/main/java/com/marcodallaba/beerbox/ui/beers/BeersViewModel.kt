package com.marcodallaba.beerbox.ui.beers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.marcodallaba.beerbox.data.source.BeersRepository
import com.marcodallaba.beerbox.util.BeerType
import com.marcodallaba.beerbox.util.asUIModel

class BeersViewModel constructor(private val beersRepository: BeersRepository) : ViewModel() {


    private val currentFilter: MutableLiveData<Pair<String, MutableSet<BeerType>>> =
        MutableLiveData<Pair<String, MutableSet<BeerType>>>().apply {
            value = Pair("", mutableSetOf())
        }

    val beers: LiveData<List<UIBeerItem>> = Transformations.switchMap(currentFilter) { filter ->
        beersSource?.let { beersData ->
            Transformations.map(beersData) { beersList ->
                beersList.filter { beer ->
                    val searchQueryFilter = beer.name.contains(filter.first, true)
                            || beer.tagLine?.contains(filter.first, true) == true
                            || beer.description?.contains(filter.first, true) == true

                    val typeFilter = filter.second.isEmpty() || filter.second.contains(beer.beerType)

                    searchQueryFilter && typeFilter
                }
            }
        }
    }

    private val beersSource = beersRepository.getBeers(ITEM_PER_PAGE) {
        it.asUIModel()
    }
    val beerTypes = beersRepository.getBeerTypes()

    fun addFilter(beerType: BeerType) {
        val filter = currentFilter.value?.copy()
        filter?.second?.add(beerType)
        currentFilter.value = filter
    }

    fun removeFilter(beerType: BeerType) {
        val filter = currentFilter.value?.copy()
        filter?.second?.remove(beerType)
        currentFilter.value = filter
    }

    fun onQuery(query: String) {
        val filter = currentFilter.value?.copy(first = query)
        currentFilter.value = filter
    }

    override fun onCleared() {
        super.onCleared()
        beersRepository.dispose()
    }

    //This could be migrated to new Paged Library
    fun getBeers(visibleItemCount: Int?, firstVisibleItemPosition: Int?, totalItemCount: Int?) {
        if (visibleItemCount?.plus(firstVisibleItemPosition ?: 0) ?: 0 >= (totalItemCount ?: 0) - 10
            && firstVisibleItemPosition ?: 0 >= 0
            && totalItemCount ?: 0 >= ITEM_PER_PAGE
        ) {
            beersRepository.getBeers(ITEM_PER_PAGE) {
                it.asUIModel()
            }
        }
    }


    companion object {
        private const val ITEM_PER_PAGE = 25
    }

}