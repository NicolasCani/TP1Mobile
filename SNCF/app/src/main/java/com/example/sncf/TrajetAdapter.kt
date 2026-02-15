package com.example.sncf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrajetAdapter(private val trajets: List<Journey>) : RecyclerView.Adapter<TrajetAdapter.TrajetViewHolder>() {

    class TrajetViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val hDep: TextView = v.findViewById(R.id.tvHeureDepart)
        val hArr: TextView = v.findViewById(R.id.tvHeureArrivee)
        val duree: TextView = v.findViewById(R.id.tvDuree)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrajetViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_trajet, parent, false)
        return TrajetViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TrajetViewHolder, position: Int) {
        val t = trajets[position]

        holder.hDep.text = t.departure_date_time.substring(9, 11) + ":" + t.departure_date_time.substring(11, 13)
        holder.hArr.text = t.arrival_date_time.substring(9, 11) + ":" + t.arrival_date_time.substring(11, 13)

        val h = t.duration / 3600
        val m = (t.duration % 3600) / 60
        holder.duree.text = "${h}h ${m}min"
    }

    override fun getItemCount() = trajets.size
}