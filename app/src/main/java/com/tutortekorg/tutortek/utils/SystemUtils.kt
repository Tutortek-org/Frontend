package com.tutortekorg.tutortek.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.tutortekorg.tutortek.R

object SystemUtils {
    fun showConfirmDeleteDialog(
        context: Context,
        messageID: Int,
        name: String,
        requestFunction: () -> Unit
    ) {
        val message = context.getString(messageID, name)
        val alert = AlertDialog.Builder(context)
            .setTitle(R.string.confirm_delete)
            .setMessage(message)
            .setPositiveButton(R.string.btn_no) { _: DialogInterface, _: Int -> }
            .setNegativeButton(R.string.btn_yes) { _: DialogInterface, _: Int ->
                requestFunction()
            }
            .create()
        val color = context.getColor(R.color.color_primary)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color)
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color)
    }
}
