package com.tutortekorg.tutortek

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment(private val viewToSet: EditText) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if(viewToSet.text.isNullOrBlank()) return setDateToCurrentDay()
        return setDateToCurrentInput()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val dayToSet = if(day < 10) "0$day" else day.toString()
        val monthToSet = if(month < 9) "0${month + 1}" else (month + 1).toString()
        viewToSet.setText(getString(R.string.birthdate_value, year, monthToSet, dayToSet))
    }

    private fun setDateToCurrentDay(): DatePickerDialog {
        val calendar = Calendar.getInstance()
        return getDatePickerDialog(calendar)
    }

    private fun setDateToCurrentInput(): DatePickerDialog {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = parser.parse(viewToSet.text.toString())
        val calendar = Calendar.getInstance()
        calendar.time = parsedDate!!
        return getDatePickerDialog(calendar)
    }

    private fun getDatePickerDialog(calendar: Calendar): DatePickerDialog {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), R.style.customDatePickerStyle, this, year, month, day)
    }
}
