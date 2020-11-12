package ru.buylist.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import ru.buylist.R

fun View.showKeyboard() {

    this.post {
        this.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.hideKeyboard() {
    this.post {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}


/**
 *  Extension that triggers a snackbar message when the value contained by snackbarEvent is modified.
 */

fun View.showSnackbar(message: String, btnText: String, listener: View.OnClickListener) {
    Snackbar
            .make(this, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(btnText, listener)
            .setActionTextColor(resources.getColor(R.color.colorPrimary))
            .show()
}


fun View.setupSnackbar(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<Int>>
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(
                    context.getString(it),
                    context.getString(R.string.snackbar_action_ok),
                    View.OnClickListener {  })
        }
    })
}