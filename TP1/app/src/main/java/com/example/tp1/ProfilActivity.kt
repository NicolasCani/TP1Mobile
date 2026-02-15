package com.example.tp1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ProfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val txtNom = findViewById<TextView>(R.id.textNomValide)
        val txtPrenom = findViewById<TextView>(R.id.textPrenomValide)
        val txtAge = findViewById<TextView>(R.id.textAgeValide)
        val txtCompet = findViewById<TextView>(R.id.textViewCompetValide)
        val txtPhone = findViewById<TextView>(R.id.textPhoneValide)
        val btnRetour = findViewById<Button>(R.id.button1)
        val btnSend = findViewById<Button>(R.id.button2)

        txtNom.text = intent.getStringExtra("nom")
        txtPrenom.text = intent.getStringExtra("prenom")
        txtAge.text = intent.getStringExtra("age")
        txtCompet.text = intent.getStringExtra("dom")
        txtPhone.text = intent.getStringExtra("tel")

        btnSend.setOnClickListener {
            val intentAppel = Intent(this, AppelActivity::class.java)
            intentAppel.putExtra("tel", txtPhone.text.toString())
            startActivity(intentAppel)
        }

        btnRetour.setOnClickListener {
            val intentMain = Intent(this, MainActivity::class.java)

            intentMain.putExtra("nom", txtNom.text.toString())
            intentMain.putExtra("prenom", txtPrenom.text.toString())
            intentMain.putExtra("age", txtAge.text.toString())
            intentMain.putExtra("dom", txtCompet.text.toString())
            intentMain.putExtra("tel", txtPhone.text.toString())

            startActivity(intentMain)
            finish()
        }
    }
}