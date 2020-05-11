package es.upsa.mimo.gamesviewer.misc

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamesviewer.R


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

        @JvmStatic
        fun launchChildFragment(parentFragment: Fragment, fragment: BackFragment, fragmentManager: FragmentManager)
        {
            fragmentManager
                .beginTransaction()
                .hide(parentFragment)
                .add(R.id.fragmentFrame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        }
    }
}
