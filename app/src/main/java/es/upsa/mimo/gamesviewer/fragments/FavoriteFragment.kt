package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.datamodule.database.DatabaseInstance
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.adapters.FavoriteViewAdapter
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.launchChildFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : MenuFragment(R.string.app_favorites), RLItemClickListener<JuegoFav>
{
    private var favs: MutableList<JuegoFav> = mutableListOf()
    private lateinit var adapter: FavoriteViewAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoriteViewAdapter(favs, this, childFragmentManager)

        viewPager = view.findViewById(R.id.favViewPager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
        {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int)
            {
                updateCurrentPage(position + 1, favs.size)
            }
        })

        textView = view.findViewById(R.id.favActualPage)
        updateCurrentPage(0, 0)

        lifecycleScope.launch {
            UsuarioController.getObservableOfFavorites(requireContext())
                .observe(viewLifecycleOwner, Observer { t ->
                    if (t != null && t.isNotEmpty())
                    {
                        favs.clear()
                        favs.addAll(t)
                        adapter.notifyDataSetChanged()
                        viewPager.invalidate()
                        updateCurrentPage(viewPager.currentItem + 1, favs.size)
                    }
                })
        }
    }

    private fun updateCurrentPage(x: Int, y: Int)
    {
        textView.text = getString(R.string.page_x_of_y, x, y)
    }

    fun fragmentBackPressed()
    {
        if (viewPager.currentItem == 0)
            requireActivity().moveTaskToBack(false)
        else
            viewPager.currentItem = viewPager.currentItem - 1
    }

    override fun onItemClick(item: JuegoFav)
    {
        val bundle = Bundle()
        bundle.putInt(JuegoInfoFragment.bundleJuegoInfoKey, item.id)
        val nextFrag = JuegoInfoFragment.newInstance(this, bundle)
        launchChildFragment(this, nextFrag)
    }
}
