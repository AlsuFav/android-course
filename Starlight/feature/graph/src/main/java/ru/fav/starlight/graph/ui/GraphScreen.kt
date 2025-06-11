package ru.fav.starlight.graph.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.fav.starlight.graph.R
import ru.fav.starlight.graph.ui.component.LineGraph
import ru.fav.starlight.graph.ui.state.GraphEvent

@Composable
fun GraphScreen(viewModel: GraphViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        OutlinedTextField(
            value = state.pointCount,
            onValueChange = { newValue ->
                viewModel.onEvent(GraphEvent.PointCountChanged(newValue))
            },
            label = { Text(stringResource(R.string.points_count)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.values,
            onValueChange = { newValue ->
                viewModel.onEvent(GraphEvent.ValuesChanged(newValue))
            },
            label = { Text(stringResource(R.string.values)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        )

        if (state.fieldError.isNotEmpty()) {
            Text(
                text = state.fieldError,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onEvent(GraphEvent.DrawGraph) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = state.pointCount.isNotEmpty() && state.values.isNotEmpty()
        ) {
            Text(stringResource(R.string.build_graph))
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (state.showGraph) {
            LineGraph(values = state.valuesList)
        }
    }
}
