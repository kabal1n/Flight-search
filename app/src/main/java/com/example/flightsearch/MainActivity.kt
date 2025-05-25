package com.example.flightsearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightsearch.adapter.AirportAdapter
import com.example.flightsearch.adapter.RouteAdapter
import com.example.flightsearch.databinding.ActivityMainBinding
import com.example.flightsearch.viewmodel.FlightViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: FlightViewModel by viewModels()

    private lateinit var airportAdapter: AirportAdapter
    private lateinit var routeAdapter: RouteAdapter
    private lateinit var searchWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.preloadAirports()

        setupAdapters()
        setupSearchInput()
        observeViewModel()
        restoreSearchFromDataStore()
    }

    private fun setupAdapters() {
        airportAdapter = AirportAdapter(emptyList()) { selectedAirport ->
            safelySetSearchText(selectedAirport.iataCode)
            binding.autocompleteRecyclerView.visibility = View.GONE
            viewModel.getFlightsFrom(selectedAirport.iataCode)
        }

        binding.autocompleteRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.autocompleteRecyclerView.adapter = airportAdapter

        routeAdapter = RouteAdapter(
            emptyList(),
            isFavorite = { from, to, callback -> viewModel.isFavorite(from, to, callback) },
            toggleFavorite = { from, to -> viewModel.toggleFavorite(from, to) }
        )

        binding.routesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.routesRecyclerView.adapter = routeAdapter
    }

    private fun setupSearchInput() {
        searchWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                viewModel.saveQuery(this@MainActivity, query)
                if (query.isNotEmpty()) {
                    viewModel.searchAirports(query)
                } else {
                    viewModel.loadFavorites()
                    viewModel.preloadAirports()
                    binding.autocompleteRecyclerView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.searchEditText.addTextChangedListener(searchWatcher)
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { airports ->
            if (binding.searchEditText.text.isNotEmpty()) {
                binding.autocompleteRecyclerView.visibility = View.VISIBLE
                airportAdapter.updateData(airports)
            }
        }

        viewModel.flightsFromAirport.observe(this) { routes ->
            binding.autocompleteRecyclerView.visibility = View.GONE
            binding.routesRecyclerView.visibility = View.VISIBLE
            routeAdapter.updateData(routes)
        }

        viewModel.favorites.observe(this) { favorites ->
            if (binding.searchEditText.text.isEmpty()) {
                lifecycleScope.launch {
                    val airports = viewModel.searchResults.value.orEmpty()
                    val airportMap = airports.associateBy { it.iataCode }
                    val routes = favorites.mapNotNull { fav ->
                        val from = airportMap[fav.departureCode]
                        val to = airportMap[fav.destinationCode]
                        if (from != null && to != null) Pair(from, to) else null
                    }
                    routeAdapter.updateData(routes)
                }
            }
        }
    }

    private fun restoreSearchFromDataStore() {
        lifecycleScope.launch {
            viewModel.loadSavedQuery(this@MainActivity).collectLatest { savedQuery ->
                safelySetSearchText(savedQuery)
                if (savedQuery.isNotEmpty()) {
                    viewModel.searchAirports(savedQuery)
                } else {
                    viewModel.loadFavorites()
                }
            }
        }
    }

    private fun safelySetSearchText(newText: String) {
        binding.searchEditText.removeTextChangedListener(searchWatcher)
        binding.searchEditText.text?.clear()
        binding.searchEditText.text?.append(newText)
        binding.searchEditText.setSelection(newText.length)
        binding.searchEditText.addTextChangedListener(searchWatcher)
    }
}
