package ru.fav.starlight.presentation.screen.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.R
import ru.fav.starlight.databinding.FragmentDetailsBinding
import ru.fav.starlight.domain.model.NasaImageDetailsModel
import ru.fav.starlight.presentation.util.ErrorDialogUtil
import ru.fav.starlight.presentation.util.observe
import kotlin.getValue
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.presentation.screen.details.state.DetailsEvent
import ru.fav.starlight.presentation.screen.details.state.NasaImageDetailsState

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

                    ErrorDialogUtil.showErrorDialog(
                        context = requireContext(),
                        message = state.message
                    )
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
                .error(R.drawable.ic_stars)
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
    override fun onDestroy() {
        super.onDestroy()
    }
}
