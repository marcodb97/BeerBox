package com.marcodallaba.beerbox.ui.beers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.marcodallaba.beerbox.R
import com.marcodallaba.beerbox.ui.beer.BeerBottomSheetDialogFragment
import com.marcodallaba.beerbox.util.BeerType
import com.marcodallaba.beerbox.util.BeerTypeId
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.marcodallaba.beerbox.databinding.FragmentBeersBinding
import com.marcodallaba.beerbox.util.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class BeersFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private lateinit var binding: FragmentBeersBinding

    private val beerViewModel: BeersViewModel by viewModel()
    private val layoutManager = LinearLayoutManager(context)
    private var beersAdapter: BeersAdapter = BeersAdapter()
    private val queryPublisher = PublishSubject.create<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = layoutManager
        beersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart <= layoutManager.findFirstVisibleItemPosition())
                    binding.recyclerView.scrollToPosition(0)
            }
        })
        binding.recyclerView.adapter = beersAdapter
        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        context?.let { ContextCompat.getDrawable(it, R.drawable.list_divider_dark) }
            ?.let { dividerItemDecoration.setDrawable(it) }
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        binding.searchView.setOnQueryTextListener(this)
        val searchCloseButtonId = binding.searchView.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        binding.searchView.findViewById<View>(searchCloseButtonId).setOnClickListener { onClose() }

        beerViewModel.beers.observe(viewLifecycleOwner, { updateBeers(it) })

        binding.recyclerView.addOnScrollListener(ScrollListener(beerViewModel))
        beersAdapter.onMoreInfo.subscribe { openBeerBottomSheet(it) }.bindToLifecycle(this)

        queryPublisher.debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { beerViewModel.onQuery(it) }
            .bindToLifecycle(this)

        addFilters(beerViewModel.beerTypes)
    }

    override fun onClose(): Boolean {
        binding.searchView.setQuery("", true)
        binding.searchView.clearFocus()
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            queryPublisher.onNext(query)
            binding.searchView.clearFocus()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            queryPublisher.onNext(newText)
        }
        return true
    }

    private fun onChipChanged(view: CompoundButton, isChecked: Boolean) {
        if (isChecked)
            beerViewModel.addFilter(view.tag as BeerType)
        else
            beerViewModel.removeFilter(view.tag as BeerType)
    }

    private fun openBeerBottomSheet(beerItem: UIBeerItem) {
        BeerBottomSheetDialogFragment.newInstance(beerItem).apply {
            show(
                this@BeersFragment.parentFragmentManager,
                BottomSheetDialogFragment::class.java.canonicalName
            )
        }
    }

    private fun addFilters(beerTypes: List<BeerType>?) {
        val layoutInflater = LayoutInflater.from(context)
        beerTypes?.forEach {
            val chip: Chip =
                layoutInflater.inflate(R.layout.filter_chip, binding.filterChipGroup, false) as Chip
            chip.id = BeerTypeId.values().getOrNull(it.ordinal)?.id ?: View.generateViewId()
            chip.text = it.displayName
            chip.tag = it
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                onChipChanged(
                    buttonView,
                    isChecked
                )
            }
            TransitionManager.beginDelayedTransition(binding.filterChipGroup)
            binding.filterChipGroup.addView(chip)
        }
    }

    private fun updateBeers(beerItems: List<UIBeerItem>) {
        beersAdapter.submitList(beerItems)
    }

    //This should be changed with the new Paging Library
    class ScrollListener(private val beersViewModel: BeersViewModel?) :
        RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.layoutManager?.childCount
            val totalItemCount = recyclerView.layoutManager?.itemCount
            val firstVisibleItemPosition =
                (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()

            beersViewModel?.getBeers(visibleItemCount, firstVisibleItemPosition, totalItemCount)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = BeersFragment()
    }
}
