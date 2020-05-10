package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.RVBackButtonClickListener

class PlatformInfoFragment(private val ownerFragment: RVBackButtonClickListener) : BackFragment(ownerFragment)
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_platform_info, container, false);
    }
}
