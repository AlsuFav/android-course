package ru.fav.starlight.search.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.fav.starlight.domain.model.NasaImageModel
import ru.fav.starlight.presentation.R

@Composable
fun NasaImageCard(
    nasaImage: NasaImageModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = Role.Button
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(nasaImage.imageUrl)
                    .diskCacheKey(nasaImage.imageUrl)
                    .memoryCacheKey(nasaImage.imageUrl)
                    .build(),
                contentDescription = stringResource(ru.fav.starlight.search.R.string.nasa_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_stars)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = nasaImage.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Text(
                text = nasaImage.date,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
