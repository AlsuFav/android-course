package ru.fav.starlight.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.fav.starlight.search.R

class NasaImagesShimmerAdapter : RecyclerView.Adapter<NasaImagesShimmerAdapter.ShimmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nasa_image_shimmer, parent, false)
        return ShimmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = 6

    class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
