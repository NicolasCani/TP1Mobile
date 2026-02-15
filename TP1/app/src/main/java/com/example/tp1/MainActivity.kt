package com.example.tp1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.graphics.toColorInt


class MainActivity : ComponentActivity() {
    //fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt() //pour transformer les px en dp

    private fun remplirFormulaire(intent: Intent, champs: Map<String, EditText>, spinner: Spinner) {
        if (!intent.hasExtra("nom")) return
        champs.forEach { (cle, view) ->
            view.setText(intent.getStringExtra(cle))
        }
        intent.getStringExtra("dom")?.let { dom ->
            val adapter = spinner.adapter as? ArrayAdapter<String>
            val position = adapter?.getPosition(dom) ?: 0
            spinner.setSelection(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INTERFACE UNIQUEMENT EN KOTLIN
        /*
        val tableLayout = TableLayout(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setPadding(30.dpToPx(), 40.dpToPx(), 30.dpToPx(), 0)
            isStretchAllColumns = true
            isShrinkAllColumns = true
        }

        val row0 = TableRow(this).apply {
            paddingBottom(40)
            addView(TextView(this@MainActivity).apply {
                id = R.id.textViewTitle
                text = getString(R.string.exercice_3)
                gravity = android.view.Gravity.CENTER
                textSize = 22f // textAppearanceLarge
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            })
        }
        tableLayout.addView(row0)

        val row1 = TableRow(this).apply {
            paddingBottom(40)
            addView(creerLabel(R.string.nom))
            addView(creerSaisie(R.id.editTextNom, R.string.nameHint, 12))
            addView(creerLabel(R.string.firstName))
            addView(creerSaisie(R.id.editTextPrenom, R.string.hintFirstName, 20))
        }
        tableLayout.addView(row1)

        val row2 = TableRow(this).apply {
            paddingBottom(40)
            addView(creerLabel(R.string.age))
            addView(creerSaisie(R.id.editTextAge, R.string.hintAge, 2, android.text.InputType.TYPE_CLASS_NUMBER))
        }
        tableLayout.addView(row2)

        val row3 = TableRow(this).apply {
            paddingBottom(40)
            addView(creerLabel(R.string.compet))
            val spinner = Spinner(this@MainActivity).apply {
                id = R.id.spinnerDomaine
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                val adapter = ArrayAdapter.createFromResource(
                    this@MainActivity,
                    R.array.metiers_array,
                    android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this.adapter = adapter
            }
            addView(spinner)
        }
        tableLayout.addView(row3)

        val row4 = TableRow(this).apply {
            paddingBottom(40)
            addView(creerLabel(R.string.phoneNumber))
            addView(creerSaisie(R.id.editTextPhone, R.string.hintPhoneNumber, inputType = android.text.InputType.TYPE_CLASS_PHONE))
        }
        tableLayout.addView(row4)

        val row5 = TableRow(this).apply {
            paddingBottom(40)
            val btn = Button(this@MainActivity).apply {
                id = R.id.button1
                text = getString(R.string.sendButton)
                layoutParams = TableRow.LayoutParams().apply {
                    column = 1
                    span = 2
                }
            }
            addView(btn)
        }
        tableLayout.addView(row5)

        setContentView(tableLayout)
    }

    private fun creerLabel(resId: Int) = TextView(this).apply {
        text = getString(resId)
        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
    }

    private fun creerSaisie(idView: Int, hintId: Int, maxLen: Int = -1, inputType: Int = android.text.InputType.TYPE_CLASS_TEXT) = EditText(this).apply {
        id = idView
        hint = getString(hintId)
        this.inputType = inputType
        if (maxLen > 0) {
            filters = arrayOf(android.text.InputFilter.LengthFilter(maxLen))
        }
        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun TableRow.paddingBottom(dp: Int) {
        setPadding(0, 0, 0, dp.dpToPx())
    }
        */


        setContentView(R.layout.activity_main)

        val editNom = findViewById<EditText>(R.id.editTextNom)
        val editPrenom = findViewById<EditText>(R.id.editTextPrenom)
        val editAge = findViewById<EditText>(R.id.editTextAge)
        val editPhone = findViewById<EditText>(R.id.editTextPhone)
        val spinnerDomaine = findViewById<Spinner>(R.id.spinnerDomaine)
        val btnVal = findViewById<Button>(R.id.button1)

        val mapChamps = mapOf(
            "nom" to editNom,
            "prenom" to editPrenom,
            "age" to editAge,
            "tel" to editPhone
        )

        remplirFormulaire(intent, mapChamps, spinnerDomaine)

        btnVal.setOnClickListener {
            val rougeErreur = "#F8D7DA".toColorInt()
            val vertSucces = "#D4EDDA".toColorInt()

            val validations = listOf(
                Triple(editNom, editNom.text.trim().isEmpty(), R.string.error_nom),
                Triple(editPrenom, editPrenom.text.trim().isEmpty(), R.string.error_prenom),
                Triple(editAge,
                    editAge.text.trim()
                        .let { it.isEmpty() || it.toString().toIntOrNull() !in 0..100 },
                    R.string.error_age
                ),
                Triple(editPhone, editPhone.text.trim().length != 10, R.string.error_tel)
            )

            var formEstValide = true

            validations.forEach { (view, estEnErreur, messageRes) ->
                if (estEnErreur) {
                    view.error = getString(messageRes)
                    view.setBackgroundColor(rougeErreur)
                    formEstValide = false
                } else {
                    view.setBackgroundColor(vertSucces)
                    view.error = null
                }
            }

            if (!formEstValide) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    val intent = Intent(this, ProfilActivity::class.java)

                    intent.putExtra("nom", editNom.text.toString())
                    intent.putExtra("prenom", editPrenom.text.toString())
                    intent.putExtra("age", editAge.text.toString())
                    intent.putExtra("dom", spinnerDomaine.selectedItem.toString())
                    intent.putExtra("tel", editPhone.text.toString())

                    startActivity(intent)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}
