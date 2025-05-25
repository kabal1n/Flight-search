package com.example.flightsearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.data.entity.AirportEntity

class AirportAdapter(
    private var airports: List<AirportEntity>,
    private val onItemClick: (AirportEntity) -> Unit
) : RecyclerView.Adapter<AirportAdapter.AirportViewHolder>() {

    inner class AirportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val code: TextView = itemView.findViewById(R.id.airportCode)
        val name: TextView = itemView.findViewById(R.id.airportName)

        init {
            itemView.setOnClickListener {
                onItemClick(airports[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_airport, parent, false)
        return AirportViewHolder(view)
    }

    override fun onBindViewHolder(holder: AirportViewHolder, position: Int) {
        val airport = airports[position]
        holder.code.text = airport.iataCode
        holder.name.text = airport.name
    }

    override fun getItemCount(): Int = airports.size

    fun updateData(newAirports: List<AirportEntity>) {
        airports = newAirports
        notifyDataSetChanged()
    }
}
