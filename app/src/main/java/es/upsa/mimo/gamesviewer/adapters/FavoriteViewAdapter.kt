package es.upsa.mimo.gamesviewer.adapters

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.gamesviewer.fragments.FavoriteItemFragment
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener

class FavoriteViewAdapter(private var dataSet: List<JuegoFav>, private val listener: RLItemClickListener<JuegoFav>,
                          fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
{
    private var fragments = arrayListOf<FavoriteItemFragment>()

    override fun getCount(): Int
    {
        return dataSet.size
    }

    override fun getItem(position: Int): Fragment
    {
        var frag: FavoriteItemFragment? = fragments.getOrNull(position)
        if (frag != null)
        {
            val juegoInfo = dataSet.get(position)
            frag.updateData(juegoInfo)
            return frag
        }

        frag = FavoriteItemFragment.newInstance(dataSet.get(position), listener)
        fragments.add(position, frag)
        return frag
    }

    override fun getItemPosition(`object`: Any): Int
    {
        if (`object` is FavoriteItemFragment)
        {
            val index = fragments.indexOf(`object`)
            if (index > -1)
            {
                val juegoInfo = dataSet.getOrNull(index)
                if (juegoInfo != null)
                    `object`.updateData(juegoInfo)
                else
                    return PagerAdapter.POSITION_NONE
            }
        }
        return super.getItemPosition(`object`)
    }

    override fun getPageWidth(position: Int): Float
    {
        if ((listener as Fragment).resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 0.8f

        return 1.0f
    }
}
