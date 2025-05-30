package ru.fav.starlight.utils.extensions

import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.fav.starlight.presentation.R

fun Fragment.showErrorDialog(
    message: String,
    icon: Int = R.drawable.ic_error,
    title: String = getString(R.string.error_title),
    positiveAction: (() -> Unit)? = null
) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon)
        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            positiveAction?.invoke()
            dialog.dismiss()
        }
        .create()
        .show()
}
