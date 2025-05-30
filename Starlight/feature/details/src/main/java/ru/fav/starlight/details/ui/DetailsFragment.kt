package ru.fav.starlight.details.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.details.R
import ru.fav.starlight.domain.model.NasaImageDetailsModel
import kotlin.getValue
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.details.databinding.FragmentDetailsBinding
import ru.fav.starlight.details.ui.state.DetailsEffect
import ru.fav.starlight.details.ui.state.DetailsEvent
import ru.fav.starlight.details.ui.state.NasaImageDetailsState
import ru.fav.starlight.utils.extensions.observe
import ru.fav.starlight.utils.extensions.observeNotSuspend
import ru.fav.starlight.utils.extensions.showErrorDialog

@AndroidEntryPoint
class DetailsFragment: Fragment(R.layout.fragment_details) {

    private val viewBinding: FragmentDetailsBinding by viewBinding(FragmentDetailsBinding::bind)

    private val detailsViewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeViewModel()

        detailsViewModel.reduce(event = DetailsEvent.GetNasaImageDetails(args.date))
    }

    private fun initViews() = with(viewBinding) {
        this.buttonBack.setOnClickListener {
            detailsViewModel.reduce(event = DetailsEvent.OnBackClicked)
        }
    }

    private fun observeViewModel() {
        detailsViewModel.effect.observeNotSuspend(viewLifecycleOwner) { state ->
            when (state) {
                is DetailsEffect.ShowToast -> showToast(state.message)
                is DetailsEffect.ShowErrorDialog -> showErrorDialog(state.message)
            }
        }

        detailsViewModel.nasaImageDetailsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NasaImageDetailsState.Loading -> {
                    showLoading(true)
                }
                is NasaImageDetailsState.Success -> {
                    showLoading(false)
                    loadNasaImage(state.nasaImage)
                }

                is NasaImageDetailsState.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun loadNasaImage(nasaImage: NasaImageDetailsModel) = with(viewBinding) {
        this.apply {
            textViewNasaImageTitle.text = nasaImage.title
            textViewNasaImageDate.text = nasaImage.date
            textViewNasaImageExplanation.text = nasaImage.explanation

            shimmerImageLayout.visibility = View.VISIBLE
            shimmerImageLayout.startShimmer()
            imageViewNasaImage.visibility = View.INVISIBLE

            Glide.with(this@DetailsFragment)
                .load(nasaImage.hdImageUrl)
                .error(ru.fav.starlight.presentation.R.drawable.ic_stars)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        shimmerImageLayout.stopShimmer()
                        shimmerImageLayout.visibility = View.GONE
                        imageViewNasaImage.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        shimmerImageLayout.stopShimmer()
                        shimmerImageLayout.visibility = View.GONE
                        imageViewNasaImage.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(imageViewNasaImage)
        }
    }


    private fun showLoading(isLoading: Boolean) {
        viewBinding.apply {
            if (isLoading) {
                shimmerImageLayout.visibility = View.VISIBLE
                shimmerTextLayout.visibility = View.VISIBLE
                shimmerImageLayout.startShimmer()
                shimmerTextLayout.startShimmer()

                imageViewNasaImage.visibility = View.INVISIBLE
                textViewNasaImageTitle.visibility = View.INVISIBLE
                textViewNasaImageDate.visibility = View.INVISIBLE
                textViewNasaImageExplanation.visibility = View.INVISIBLE
            } else {
                shimmerImageLayout.stopShimmer()
                shimmerTextLayout.stopShimmer()
                shimmerImageLayout.visibility = View.GONE
                shimmerTextLayout.visibility = View.GONE

                imageViewNasaImage.visibility = View.VISIBLE
                textViewNasaImageTitle.visibility = View.VISIBLE
                textViewNasaImageDate.visibility = View.VISIBLE
                textViewNasaImageExplanation.visibility = View.VISIBLE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
