package ru.fav.starlight.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.fav.starlight.domain.model.NasaImageModel
import ru.fav.starlight.presentation.R
import ru.fav.starlight.search.databinding.ItemNasaImageBinding
import ru.fav.starlight.search.ui.util.NasaImageDiffUtil

class NasaImagesAdapter (
    private val onNasaImageClick: (NasaImageModel) -> Unit
) : RecyclerView.Adapter<NasaImagesAdapter.NasaImageViewHolder>() {

    private var nasaImages = mutableListOf<NasaImageModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NasaImageViewHolder {
        val binding = ItemNasaImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NasaImageViewHolder(binding)
    }

    override fun getItemCount(): Int = nasaImages.size

    override fun onBindViewHolder(holder: NasaImageViewHolder, position: Int) {
        holder.bind(nasaImages[position])
    }

    inner class NasaImageViewHolder(private val binding: ItemNasaImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nasaImage: NasaImageModel) {
            binding.textViewNasaImageTitle.text = nasaImage.title
            binding.textViewNasaImageDate.text = nasaImage.date

            nasaImage.imageUrl.let { url ->
                Glide.with(binding.imageViewNasaImage.context)
                    .load(url)
                    .error(R.drawable.ic_stars)
                    .into(binding.imageViewNasaImage)
            }

            itemView.setOnClickListener {
                onNasaImageClick(nasaImage)
            }
        }
    }

    fun updateData(list: MutableList<NasaImageModel>) {
        val diff = NasaImageDiffUtil(oldList = nasaImages, newList = list)
        val diffResult = DiffUtil.calculateDiff(diff)
        nasaImages.clear()
        nasaImages.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(nasaImage: NasaImageModel): Int? {
        val position = nasaImages.indexOfFirst { it.date == nasaImage.date }
        if (position != -1) {
            nasaImages.removeAt(position)
            return position
        }
        return null
    }

    fun getItem(position: Int): NasaImageModel? {
        return nasaImages.getOrNull(position)
    }
}
