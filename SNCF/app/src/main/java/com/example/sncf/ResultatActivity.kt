package com.example.sncf;
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity;
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import kotlinx.coroutines.launch

class ResultatActivity : ComponentActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sncf.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(SncfService::class.java)
    private val authHeader = "Basic " + Base64.encodeToString("${BuildConfig.SNCF_TOKEN}:".toByteArray(), Base64.NO_WRAP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultat)

        val idDep = intent.getStringExtra("ID_DEPART") ?: ""
        val idArr = intent.getStringExtra("ID_ARRIVEE") ?: ""
        val nomDep = intent.getStringExtra("DEPART_NOM") ?: "Départ"
        val nomArr = intent.getStringExtra("ARRIVEE_NOM") ?: "Arrivée"
        val dateSncf = intent.getStringExtra("DATE_SNCF") ?: "20260213T120000"

        findViewById<TextView>(R.id.tvResumeDepart).text = nomDep
        findViewById<TextView>(R.id.tvResumeArrivee).text = "vers $nomArr"

        val rv = findViewById<RecyclerView>(R.id.rvTrajets)
        rv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val response = apiService.getJourneys(authHeader, idDep, idArr, dateSncf)
                runOnUiThread {
                    rv.adapter = TrajetAdapter(response.journeys ?: listOf())
                }
            } catch (e: Exception) {
                Log.e("SNCF", "Erreur : ${e.message}")
            }
        }
    }
}
