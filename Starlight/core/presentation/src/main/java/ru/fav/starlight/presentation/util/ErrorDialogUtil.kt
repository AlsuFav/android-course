package ru.fav.starlight.presentation.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.fav.starlight.presentation.R

object ErrorDialogUtil {
    fun showErrorDialog(
        context: Context,
        message: String,
        icon: Int = R.drawable.ic_error,
        title: String = context.getString(R.string.error_title),
        positiveAction: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setIcon(icon)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
                positiveAction?.invoke()
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
