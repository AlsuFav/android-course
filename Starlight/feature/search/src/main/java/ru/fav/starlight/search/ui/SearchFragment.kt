package ru.fav.starlight.search.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.search.R
import ru.fav.starlight.search.ui.state.SearchEvent

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {

    private val searchViewModel: SearchViewModel by viewModels()

    private var composeView: ComposeView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView = ComposeView(requireContext())
        (view as ViewGroup).addView(composeView)
        composeView?.setContent{
            SearchScreen(viewModel = searchViewModel)
        }
    }

    override fun onDestroyView() {
        composeView = null
        super.onDestroyView()
    }
}
