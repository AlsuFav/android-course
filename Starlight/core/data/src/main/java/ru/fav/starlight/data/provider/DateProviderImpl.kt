package ru.fav.starlight.data.provider

import ru.fav.starlight.domain.provider.DateProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class DateProviderImpl @Inject constructor() : DateProvider {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun getCurrentDate(): Calendar {
        return Calendar.getInstance()
    }

    override fun formatDate(calendar: Calendar): String {
        return dateFormat.format(calendar.time)
    }

    override fun parseDate(dateString: String): Calendar {
        val date = dateFormat.parse(dateString)
        return Calendar.getInstance().apply {
            if (date != null) {
                time = date
            }
        }
    }
}
