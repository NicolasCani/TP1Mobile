package com.example.tp1

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity

class AppelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appel)

        val txtTel = findViewById<TextView>(R.id.textTel)
        val btnAppeler = findViewById<Button>(R.id.btnAppeler)
        val numero = intent.getStringExtra("tel")

        txtTel.text = numero

        btnAppeler.setOnClickListener {
            val intentAppel = Intent(Intent.ACTION_DIAL)
            intentAppel.data = android.net.Uri.parse("tel:$numero")
            startActivity(intentAppel)
        }
    }
}