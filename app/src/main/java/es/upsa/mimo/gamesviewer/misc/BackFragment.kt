package es.upsa.mimo.gamesviewer.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import kotlin.reflect.KClass

abstract class BackFragment : TitleFragment()
{
    var ownerFragment: TitleFragment? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        if (ownerFragment == null)
            throw AssertionError(getString(R.string.assert_needed_data_not_present));

        val homeActivity = activity as? HomeActivity;
        homeActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        return onCreateChildView(inflater, container, savedInstanceState);
    }

    abstract fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?;

    override fun onDestroyView()
    {
        if (ownerFragment != null && !(ownerFragment is BackFragment))
        {
            val homeActivity = activity as? HomeActivity;
            homeActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false);
            setHasOptionsMenu(false);
        }

        super.onDestroyView();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            android.R.id.home ->
            {
                onFragmentBack();
                true
            }
            else -> super.onOptionsItemSelected(item);
        }
    }

    fun onFragmentBack()
    {
        val homeActivity = activity as? HomeActivity;
        homeActivity?.supportActionBar?.title = ownerFragment?.getFragmentTitle(ownerFragment!!.context!!);

        activity!!.supportFragmentManager
            .beginTransaction()
            .remove(this)
            .show(ownerFragment!!)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit();
    }
}
