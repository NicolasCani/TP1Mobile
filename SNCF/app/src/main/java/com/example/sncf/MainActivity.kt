package com.example.sncf

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.ComponentActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class MainActivity : ComponentActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sncf.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(SncfService::class.java)
    private val myToken = BuildConfig.SNCF_TOKEN
    private val authHeader = "Basic " + Base64.encodeToString("$myToken:".toByteArray(), Base64.NO_WRAP)

    private var garesActuelles: List<Place> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<AutoCompleteTextView>(R.id.searchBar)
        val adapter = ArrayAdapter(this, R.layout.item_gare, mutableListOf<String>())
        searchBar.setAdapter(adapter)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length < 3) {
                    adapter.clear()
                    searchBar.dismissDropDown()
                    return
                }
                lifecycleScope.launch {
                    try {
                        val response = apiService.getPlaces(authHeader, query)
                        garesActuelles = response.places
                        val names = response.places.map { it.name }

                        runOnUiThread {
                            adapter.clear()
                            adapter.addAll(names)
                            adapter.filter.filter(null)
                            if (names.isNotEmpty()) {
                                adapter.notifyDataSetChanged()
                                searchBar.showDropDown()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SNCF_DEBUG", "Erreur : ${e.message}")
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchBar.setOnItemClickListener { parent, _, position, _ ->
            val selection = parent.getItemAtPosition(position) as String
            val selectedPlace = garesActuelles.find { it.name == selection }
            lancerSecondeActivite(selection, selectedPlace?.id ?: "")
        }

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                lancerSecondeActivite(searchBar.text.toString(), "")
                true
            } else false
        }
    }

    private fun lancerSecondeActivite(destination: String, id: String) {
        if (destination.isNotBlank()) {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("Arrivee", destination)
            intent.putExtra("Arrivee_ID", id)
            startActivity(intent)
        }
    }
}