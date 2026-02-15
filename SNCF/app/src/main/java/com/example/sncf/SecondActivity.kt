package com.example.sncf

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.ComponentActivity
import java.util.Calendar
import android.content.DialogInterface
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SecondActivity : ComponentActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sncf.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(SncfService::class.java)
    private val myToken = BuildConfig.SNCF_TOKEN
    private val authHeader = "Basic " + Base64.encodeToString("$myToken:".toByteArray(), Base64.NO_WRAP)
    private var idDepart: String? = null
    private var idArrivee: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val txtDepart = findViewById<AutoCompleteTextView>(R.id.editDepart)
        val txtArrivee = findViewById<AutoCompleteTextView>(R.id.editArrivee)
        val btnAjoutDateAller = findViewById<EditText>(R.id.editDepartDate)
        val btnAjoutDateRetour = findViewById<Button>(R.id.editArriveeDate)
        val labelRetour = findViewById<TextView>(R.id.labelRetour)
        val btnVoirTrajets = findViewById<Button>(R.id.button3)

        configurerAutocompletion(txtDepart)
        configurerAutocompletion(txtArrivee)

        txtArrivee.setText(intent.getStringExtra("Arrivee"))
        idArrivee = intent.getStringExtra("Arrivee_ID")

        val c = Calendar.getInstance()
        val dateAujourdhui = "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}"
        btnAjoutDateAller.setText(dateAujourdhui)
        btnAjoutDateAller.textSize = 18f

        configurerCalendrier(btnAjoutDateAller, null)
        configurerCalendrier(btnAjoutDateRetour, labelRetour)

        findViewById<Button>(R.id.button2).setOnClickListener {
            finish()
        }

        btnVoirTrajets.setOnClickListener {
            if (idDepart != null && idArrivee != null) {
                val intentResultats = Intent(this, ResultatActivity::class.java)

                intentResultats.putExtra("ID_DEPART", idDepart)
                intentResultats.putExtra("ID_ARRIVEE", idArrivee)
                intentResultats.putExtra("DEPART_NOM", txtDepart.text.toString())
                intentResultats.putExtra("ARRIVEE_NOM", txtArrivee.text.toString())
                intentResultats.putExtra("DATE_SNCF", formaterDateSNCF(btnAjoutDateAller.text.toString()))

                startActivity(intentResultats)
            } else {
                Toast.makeText(this, "Cliquez sur une gare dans la liste pour valider", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurerAutocompletion(searchBar: AutoCompleteTextView) {
        val adapter = ArrayAdapter(this, R.layout.item_gare, mutableListOf<String>())
        searchBar.setAdapter(adapter)

        var placesRecues: List<Place> = listOf()

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
                        placesRecues = response.places
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
                    } catch (e: Exception) { Log.e("SNCF_DEBUG", "Erreur : ${e.message}") }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchBar.setOnItemClickListener { parent, _, position, _ ->
            val selection = parent.getItemAtPosition(position) as String
            val place = placesRecues.find { it.name == selection }

            if (searchBar.id == R.id.editDepart) {
                idDepart = place?.id
            } else {
                idArrivee = place?.id
            }
        }
    }

    private fun configurerCalendrier(vueCible: TextView, labelAssocie: TextView?) {
        vueCible.setOnClickListener {
            val c = Calendar.getInstance()
            val scale = resources.displayMetrics.density
            val datePicker = DatePickerDialog(this, { _, annee, mois, jour ->
                val dateString = "$jour/${mois + 1}/$annee"
                vueCible.text = dateString
                vueCible.textSize = 18f
                if (labelAssocie != null) {
                    labelAssocie.visibility = View.VISIBLE
                    vueCible.setPadding((85 * scale + 0.5f).toInt(), 0, 0, 0)
                    vueCible.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler") { dialog, _ ->
                if (labelAssocie != null) {
                    labelAssocie.visibility = View.GONE
                    vueCible.text = "Ajouter le retour"
                    vueCible.setPadding(0, 0, 0, 0)
                    vueCible.gravity = Gravity.CENTER
                    vueCible.textSize = 14f
                } else {
                    vueCible.text = ""
                }
                dialog.dismiss()
            }
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        }
    }

    private fun formaterDateSNCF(date: String): String {
        return try {
            val parts = date.split("/")
            val d = parts[0].padStart(2, '0')
            val m = parts[1].padStart(2, '0')
            val y = parts[2]
            "${y}${m}${d}T120000"
        } catch (e: Exception) { "" }
    }
}