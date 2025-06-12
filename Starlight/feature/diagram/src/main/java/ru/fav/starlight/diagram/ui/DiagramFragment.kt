package ru.fav.starlight.diagram.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.fav.starlight.diagram.R
import ru.fav.starlight.diagram.databinding.FragmentDiagramBinding
import ru.fav.starlight.diagram.ui.state.DiagramEvent
import ru.fav.starlight.diagram.ui.state.DiagramState
import ru.fav.starlight.utils.extensions.observeNotSuspend

@AndroidEntryPoint
class DiagramFragment: Fragment(R.layout.fragment_diagram) {

    private val viewBinding: FragmentDiagramBinding by viewBinding(FragmentDiagramBinding::bind)
    private val diagramViewModel: DiagramViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.generateButton.setOnClickListener {
            diagramViewModel.reduce(DiagramEvent.OnButtonClicked)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        diagramViewModel.state.observeNotSuspend(viewLifecycleOwner) { state ->
            when (state) {
                is DiagramState.Initial ->
                    viewBinding.textViewTitle.text = getString(R.string.data_from_attrs)
                is DiagramState.Success -> {
                    viewBinding.diagramView.updateData(state.values, state.colors)
                    viewBinding.textViewTitle.text = getString(R.string.generated_data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}