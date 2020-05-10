package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.MenuFragment

class FavoriteFragment : MenuFragment(R.string.app_favorites)
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
}
