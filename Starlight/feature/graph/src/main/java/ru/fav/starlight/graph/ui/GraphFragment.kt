package ru.fav.starlight.graph.ui

import android.graphics.Rect
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import dagger.hilt.android.AndroidEntryPoint
import ru.fav.starlight.graph.R

@AndroidEntryPoint
class GraphFragment: Fragment(R.layout.fragment_graph) {

    private val graphViewModel: GraphViewModel by viewModels()

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

        composeView = ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFocusable = true
            isFocusableInTouchMode = true
            setContent {
                GraphScreen(viewModel = graphViewModel)
            }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) { // Клавиатура открыта
                view.setPadding(0, 0, 0, keypadHeight)
            } else { // Клавиатура закрыта
                view.setPadding(0, 0, 0, 0)
            }
        }

        (view as ViewGroup).addView(composeView)
    }

    override fun onDestroyView() {
        composeView = null
        super.onDestroyView()
    }
}
