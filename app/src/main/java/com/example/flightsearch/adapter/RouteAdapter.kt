package com.example.flightsearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightsearch.R
import com.example.flightsearch.data.entity.AirportEntity

class RouteAdapter(
    private var routes: List<Pair<AirportEntity, AirportEntity>>,
    private val isFavorite: (String, String, (Boolean) -> Unit) -> Unit,
    private val toggleFavorite: (String, String) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromText: TextView = itemView.findViewById(R.id.fromText)
        val toText: TextView = itemView.findViewById(R.id.toText)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val (from, to) = routes[position]
        holder.fromText.text = "From: ${from.iataCode} (${from.name})"
        holder.toText.text = "To: ${to.iataCode} (${to.name})"

        isFavorite(from.iataCode, to.iataCode) { fav ->
            holder.favoriteIcon.setImageResource(
                if (fav) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }

        holder.favoriteIcon.setOnClickListener {
            toggleFavorite(from.iataCode, to.iataCode)
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int = routes.size

    fun updateData(newRoutes: List<Pair<AirportEntity, AirportEntity>>) {
        routes = newRoutes
        notifyDataSetChanged()
    }
}
