package com.kodekolektif.notiflistener.utils

import android.app.AlertDialog
import android.content.Context

class DialogManager {

    companion object {

        /**
         * Shows a simple alert dialog with an OK button
         *
         * @param title The title of the alert dialog
         * @param message The message to display in the dialog
         * @param onDismiss Optional callback when the dialog is dismissed
         */
        @JvmStatic
        fun showAlertDialog(
            context: Context,
            title: String,
            message: String,
            onDismiss: (() -> Unit)? = null
        ) {
            val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog = builder.create()

            // Set a dismiss listener if a callback is provided
            alertDialog.setOnDismissListener {
                onDismiss?.invoke()
            }

            alertDialog.show()
        }

        /**
         * Shows a confirm dialog with Yes and No options
         *
         * @param title The title of the confirm dialog
         * @param message The message to display in the dialog
         * @param onConfirm Callback for when the user confirms (presses Yes)
         * @param onCancel Callback for when the user cancels (presses No)
         * @param onDismiss Optional callback when the dialog is dismissed
         */
        @JvmStatic
        fun showConfirmDialog(
            context: Context,
            title: String,
            message: String,
            onConfirm: () -> Unit,
            onCancel: () -> Unit,
            onDismiss: (() -> Unit)? = null
        ) {
            val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes") { dialog, _ ->
                    onConfirm()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    onCancel()
                    dialog.dismiss()
                }

            val confirmDialog = builder.create()

            // Set a dismiss listener if a callback is provided
            confirmDialog.setOnDismissListener {
                onDismiss?.invoke()
            }

            confirmDialog.show()
        }
    }
}
