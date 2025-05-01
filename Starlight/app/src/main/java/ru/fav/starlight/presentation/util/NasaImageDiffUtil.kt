package ru.fav.starlight.presentation.util

import androidx.recyclerview.widget.DiffUtil
import ru.fav.starlight.domain.model.NasaImageModel

class NasaImageDiffUtil (
    private var oldList: List<NasaImageModel>,
    private var newList: List<NasaImageModel>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].date == (newList[newItemPosition]).date)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
