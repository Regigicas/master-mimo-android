package es.upsa.mimo.gamesviewer.adapters

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.FavoriteItemFragment
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener

class FavoriteViewAdapter(private var dataSet: List<JuegoFav>, private val listener: RLItemClickListener<JuegoFav>,
                          fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
{
    override fun getCount(): Int
    {
        return dataSet.size
    }

    override fun getItem(position: Int): Fragment
    {
        return FavoriteItemFragment.newInstance(dataSet.get(position), listener)
    }

    override fun getItemPosition(`object`: Any): Int
    {
        val fragment = `object` as FavoriteItemFragment
        return if (dataSet.find { it.id == fragment.juegoInfo.id } == null)
            PagerAdapter.POSITION_NONE
        else
            super.getItemPosition(`object`)
    }

    override fun getPageWidth(position: Int): Float
    {
        if ((listener as Fragment).resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 0.8f

        return 1.0f
    }
}
