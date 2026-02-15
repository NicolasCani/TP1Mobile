package com.example.calendar

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Evenement(
    val titre: String,
    val date: String,
    val heure: String,
    val estAnnuel: Boolean = false
)

class MainActivity : ComponentActivity() {

    private val tousLesEvenements = mutableListOf<Evenement>()
    private lateinit var adaptateur: EvenementAdapter
    private var dateSelectionnee: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chargerDonnees()

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val rvEvents = findViewById<RecyclerView>(R.id.rvEvents)
        val btnAddEvent = findViewById<Button>(R.id.btnAddEvent)

        val c = java.util.Calendar.getInstance()
        dateSelectionnee = "${c.get(java.util.Calendar.DAY_OF_MONTH)}/${c.get(java.util.Calendar.MONTH) + 1}/${c.get(java.util.Calendar.YEAR)}"
        tvSelectedDate.text = getString(R.string.evenements_du, dateSelectionnee)

        adaptateur = EvenementAdapter(mutableListOf())
        rvEvents.layoutManager = LinearLayoutManager(this)
        rvEvents.adapter = adaptateur
        filtrerEtAfficherEvenements()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            dateSelectionnee = "$dayOfMonth/${month + 1}/$year"
            tvSelectedDate.text = getString(R.string.evenements_du, dateSelectionnee)
            filtrerEtAfficherEvenements()
        }

        btnAddEvent.setOnClickListener {
            afficherDialogueAjout()
        }
    }

    private fun afficherDialogueAjout() {
        val vueDialogue = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val etTaskName = vueDialogue.findViewById<EditText>(R.id.etTaskName)
        val btnPickTime = vueDialogue.findViewById<Button>(R.id.btnPickTime)
        val cbYearly = vueDialogue.findViewById<CheckBox>(R.id.cbYearly)

        var heureChoisie = "12:00"

        btnPickTime.setOnClickListener {
            val c = java.util.Calendar.getInstance()
            TimePickerDialog(this, { _, heure, minute ->
                heureChoisie = String.format("%02d:%02d", heure, minute)
                btnPickTime.text = heureChoisie
            }, c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.nouvel_evenement)
            .setView(vueDialogue)
            .setPositiveButton(R.string.enregistrer) { _, _ ->
                val nomTache = etTaskName.text.toString()
                if (nomTache.isNotBlank()) {
                    tousLesEvenements.add(Evenement(nomTache, dateSelectionnee, heureChoisie, cbYearly.isChecked))
                    sauvegarderDonnees()
                    filtrerEtAfficherEvenements()
                }
            }
            .setNegativeButton(R.string.annuler) { dialog, _ ->
                dialog.dismiss()
            }
            .show().show()
    }

    private fun filtrerEtAfficherEvenements() {
        val listeFiltree = tousLesEvenements.filter { evt ->
            if (evt.estAnnuel) {
                val pE = evt.date.split("/")
                val pS = dateSelectionnee.split("/")
                pE[0] == pS[0] && pE[1] == pS[1]
            } else {
                evt.date == dateSelectionnee
            }
        }.sortedBy { it.heure }

        adaptateur.majListe(listeFiltree)
    }

    private fun sauvegarderDonnees() {
        val sharedPreferences = getSharedPreferences("agenda_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(tousLesEvenements)
        editor.putString("liste_evenements", json)
        editor.apply()
    }

    private fun chargerDonnees() {
        val sharedPreferences = getSharedPreferences("agenda_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("liste_evenements", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<Evenement>>() {}.type
            val listeChargee: MutableList<Evenement> = gson.fromJson(json, type)
            tousLesEvenements.clear()
            tousLesEvenements.addAll(listeChargee)
        }
    }
}

class EvenementAdapter(private var listeEvenements: List<Evenement>) : RecyclerView.Adapter<EvenementAdapter.EvenementViewHolder>() {

    class EvenementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEventTitle: TextView = view.findViewById(R.id.tvEventTitle)
        val tvEventTime: TextView = view.findViewById(R.id.tvEventTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvenementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EvenementViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvenementViewHolder, position: Int) {
        val evt = listeEvenements[position]
        holder.tvEventTitle.text = evt.titre
        val suffixe = if (evt.estAnnuel) holder.itemView.context.getString(R.string.suffixe_annuel) else ""
        holder.tvEventTime.text = "${evt.heure}$suffixe"
    }

    override fun getItemCount() = listeEvenements.size

    fun majListe(nouvelleListe: List<Evenement>) {
        listeEvenements = nouvelleListe
        notifyDataSetChanged()
    }
}