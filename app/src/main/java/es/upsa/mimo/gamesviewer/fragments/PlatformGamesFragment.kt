package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.misc.BackFragment
import es.upsa.mimo.gamesviewer.misc.GVItemClickListener
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.gamesviewer.views.PlatformGameViewAdapter
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController
import kotlinx.coroutines.launch

class PlatformGamesFragment : BackFragment(), GVItemClickListener<JuegoModel>
{
    companion object
    {
        val bundlePlatformGamesKey = "PlatformGamesFragmentPlatformInfo";

        @JvmStatic
        fun newInstance(owner: Fragment, bundle: Bundle?): PlatformGamesFragment
        {
            val nuevoFrag = PlatformGamesFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private var plataforma: PlatformModel? = null;
    private var juegos: MutableList<JuegoModel> = mutableListOf();
    private var initialCreation = false;
    private var currentPage = 1;
    private val adapter = PlatformGameViewAdapter(juegos, this);
    private var layoutJuegosPlat: SwipeRefreshLayout? = null;
    private var loadingMoreData = false;
    private val layoutManager = LinearLayoutManager(activity);
    private val maxLoadedItems = 100;

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        plataforma = arguments?.getSerializable(bundlePlatformGamesKey) as PlatformModel?;
        if (plataforma == null)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_platform_games, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);
        if (initialCreation)
        {
            val homeActivity = activity as? HomeActivity;
            homeActivity?.supportActionBar?.title = getString(R.string.platform_games_title, plataforma?.name);
            return;
        }

        layoutJuegosPlat = view.findViewById(R.id.layoutJuegosPlat);
        if (layoutJuegosPlat == null)
            throw AssertionError(getString(R.string.assert_view_not_created));

        layoutJuegosPlat!!.isRefreshing = true;
        layoutJuegosPlat!!.setOnRefreshListener {
            refreshDatos();
        }

        val rvJuegosPlat = view.findViewById<RecyclerView>(R.id.rvJuegosPlat);
        rvJuegosPlat.setHasFixedSize(true);
        rvJuegosPlat.adapter = adapter;
        rvJuegosPlat.layoutManager = layoutManager;
        rvJuegosPlat.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        refreshDatos();

        rvJuegosPlat.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (loadingMoreData)
                    return;

                if (dy > 0)
                {
                    val visibleItemCount = layoutManager.getChildCount();
                    val totalItemCount = layoutManager.getItemCount();
                    if (totalItemCount >= maxLoadedItems)
                        return;

                    val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                    {
                        loadingMoreData = true;
                        loadMoreData();
                    }
                }
            }
        });
    }

    private fun refreshDatos()
    {
        juegos.clear();
        currentPage = 1;
        lifecycleScope.launch {
            activity?.let {fragActivity ->
                JuegoNetworkController.getJuegosPlataforma(currentPage, plataforma!!.id!!, fragActivity) {
                    initialCreation = true;
                    juegos.addAll(it);
                    adapter.notifyDataSetChanged();
                    layoutJuegosPlat!!.isRefreshing = false;
                }
            }
        }
    }

    private fun loadMoreData()
    {
        layoutJuegosPlat!!.isRefreshing = true; // Evitamos que se pueda actualizar
        currentPage += 1;
        lifecycleScope.launch {
            activity?.let {fragActivity ->
                JuegoNetworkController.getJuegosPlataforma(currentPage, plataforma!!.id!!, fragActivity) {
                    loadingMoreData = false;
                    juegos.addAll(it);
                    adapter.notifyDataSetChanged();
                    layoutJuegosPlat!!.isRefreshing = false;
                }
            }
        }
    }

    override fun onItemClick(item: JuegoModel)
    {
        val bundle = Bundle();
        item.id?.let {
            bundle.putInt(JuegoInfoFragment.bundleJuegoInfoKey, it);
        };
        val nextFrag = JuegoInfoFragment.newInstance(this, bundle);
        Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
    }
}
