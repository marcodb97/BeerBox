package com.marcodallaba.beerbox.ui

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import com.marcodallaba.beerbox.R
import com.marcodallaba.beerbox.databinding.ActivityMainBinding
import com.marcodallaba.beerbox.ui.beers.BeersFragment
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.core.KoinExperimentalAPI

@KoinExperimentalAPI
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbarTitle.text = SpannableStringBuilder().append(getString(R.string.beer_name))
            .append(" ")
            .append(getString(R.string.box_name), StyleSpan(BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BeersFragment::class.java, null)
                .commit()
    }
}
