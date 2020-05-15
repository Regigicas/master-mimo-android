@file:JvmName("Utils")
package es.upsa.mimo.gamesviewer.misc

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamesviewer.R

fun Fragment.launchChildFragment(parentFragment: Fragment, fragment: BackFragment)
{
    if (parentFragment.activity != null)
    {
        parentFragment.activity!!.supportFragmentManager
            .beginTransaction()
            .hide(parentFragment)
            .add(R.id.fragmentFrame, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit();
    }
}

fun Fragment.findFragmentByClassName(name: String, fragmentManager: FragmentManager): TitleFragment?
{
    for (oldFragment in fragmentManager.fragments)
        if (oldFragment::class.java.name == name)
            return oldFragment as TitleFragment;

    return null;
}

fun Activity.hideKeyBoard()
{
    val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0);
}

fun ImageView.loadFromURL(url: String)
{
    Picasso.get().load(url).fit().centerCrop().into(this);
}
