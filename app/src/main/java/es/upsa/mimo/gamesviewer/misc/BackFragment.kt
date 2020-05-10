package es.upsa.mimo.gamesviewer.misc

import androidx.fragment.app.Fragment

abstract class BackFragment(private val ownerFragment: RVBackButtonClickListener) : Fragment()
{
    fun onFragmentBack()
    {
        ownerFragment.onFragmentBackClick(this);
    }
}
