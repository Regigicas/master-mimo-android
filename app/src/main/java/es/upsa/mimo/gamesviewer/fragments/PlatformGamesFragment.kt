package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.BackFragment

class PlatformGamesFragment(ownerFragment: Fragment) : BackFragment(ownerFragment)
{
    companion object
    {
        val bundlePlatformGamesKey = "PlatformGamesFragmentPlatformId";
    }

    private var platformId: Int? = null;
    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        platformId = arguments?.getInt(bundlePlatformGamesKey);
        if (platformId == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_platform_games, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);
        val textPlataforma = view.findViewById<TextView>(R.id.tempIdDeleteMe);
        textPlataforma.text = platformId.toString();
    }
}
