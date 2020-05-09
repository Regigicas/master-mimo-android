package es.upsa.mimo.gamesviewer.misc

import android.app.Activity
import android.view.inputmethod.InputMethodManager


class Util
{
    companion object
    {
        @JvmStatic
        fun hideKeyBoard(activity: Activity): Unit
        {
            val inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0);
        }
    }
}
