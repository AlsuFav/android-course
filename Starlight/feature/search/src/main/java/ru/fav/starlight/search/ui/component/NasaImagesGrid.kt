package ru.fav.starlight.search.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import ru.fav.starlight.domain.model.NasaImageModel

@Composable
fun NasaImagesGrid(
    images: List<NasaImageModel>,
    onImageClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            items = images,
            key = { it.date }
        ) { nasaImage ->
            NasaImageCard(
                nasaImage = nasaImage,
                onClick = remember { { onImageClick(nasaImage.date) } }
            )
        }
    }
}