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
import es.upsa.mimo.gamesviewer.fragments.BackFragment
import es.upsa.mimo.gamesviewer.fragments.TitleFragment

fun Fragment.launchChildFragment(parentFragment: Fragment, fragment: BackFragment)
{
    if (parentFragment.activity != null)
    {
        parentFragment.requireActivity().supportFragmentManager
            .beginTransaction()
            .hide(parentFragment)
            .add(R.id.fragmentFrame, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }
}

fun Fragment.findFragmentByClassName(name: String, fragmentManager: FragmentManager): TitleFragment?
{
    for (oldFragment in fragmentManager.fragments)
        if (oldFragment::class.java.name == name)
            return oldFragment as TitleFragment

    return null
}

fun Activity.hideKeyBoard()
{
    val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun ImageView.loadFromURL(url: String)
{
    if (PreferencesManager.getBooleanConfig(context, R.string.config_lowdata_key))
        setImageDrawable(context.getDrawable(R.drawable.ic_gamepad_purple_200dp))
    else
    {
        Picasso.get().load(url)
            .placeholder(R.drawable.ic_gamepad_purple_200dp)
            .error(R.drawable.ic_gamepad_purple_200dp)
            .fit().centerCrop().into(this)
    }
}
