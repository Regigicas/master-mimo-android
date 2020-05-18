package es.upsa.mimo.gamesviewer.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.HomeActivity
import es.upsa.mimo.gamesviewer.adapters.GameSearchViewAdapter
import es.upsa.mimo.gamesviewer.misc.*
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController
import kotlinx.coroutines.launch
import java.io.Serializable

class PlatformGamesFragment : BackFragment(), RLItemClickListener<JuegoModel>
{
    companion object
    {
        val bundlePlatformGamesKey = "PlatformGamesFragmentPlatformInfo";

        @JvmStatic
        fun newInstance(owner: PlatformInfoFragment, bundle: Bundle?): PlatformGamesFragment
        {
            val nuevoFrag = PlatformGamesFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private lateinit var plataforma: PlatformModel;
    private var juegos: MutableList<JuegoModel> = mutableListOf();
    private var initialCreation = false;
    private var currentPage = 1;
    private val adapter = GameSearchViewAdapter(juegos, this);
    private lateinit var layoutJuegosPlat: SwipeRefreshLayout;
    private var loadingMoreData = false;
    private val layoutManager = LinearLayoutManager(activity);
    private var maxLoadedItems = 100;
    private val saveStateKey = "JuegosStateKey";
    private val saveJuegosKey = "JuegosListKey";
    private val saveJuegosCurrentPage = "JuegosListCurrentPage";

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            val juegosGuardados = savedInstanceState.getSerializable(saveJuegosKey) as MutableList<*>;
            for (juegoInfo in juegosGuardados)
                juegos.add(juegoInfo as JuegoModel);

            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(saveStateKey));
            if (activity != null)
                ownerFragment = findFragmentByClassName(PlatformInfoFragment::class.java.name, requireActivity().supportFragmentManager); // La vista solo puede ser creada por esta clase

            currentPage = savedInstanceState.getInt(saveJuegosCurrentPage, 1);
        }

        maxLoadedItems = PreferencesManager.getMaxElementsInList(requireContext());
    }

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        plataforma = arguments?.getSerializable(bundlePlatformGamesKey) as PlatformModel;
        if (!this::plataforma.isInitialized)
            throw AssertionError(R.string.assert_needed_data_not_present);

        return inflater.inflate(R.layout.fragment_platform_games, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);
        if (initialCreation)
        {
            val homeActivity = activity as? HomeActivity;
            homeActivity?.supportActionBar?.title = getString(R.string.platform_games_title, plataforma.name);
            return;
        }

        layoutJuegosPlat = view.findViewById(R.id.layoutJuegosPlat);

        if (juegos.size > 0)
        {
            setupView(view);
            initialCreation = true;
            return;
        }

        setupView(view);
        fetchGameData(true);
    }

    private fun setupView(view: View)
    {
        val homeActivity = activity as? HomeActivity;
        homeActivity?.supportActionBar?.title = getString(R.string.platform_games_title, plataforma.name);

        layoutJuegosPlat.setOnRefreshListener {
            fetchGameData(true);
        }

        val rvJuegosPlat = view.findViewById<RecyclerView>(R.id.rvJuegosPlat);
        rvJuegosPlat.setHasFixedSize(true);
        rvJuegosPlat.adapter = adapter;
        rvJuegosPlat.layoutManager = layoutManager;
        rvJuegosPlat.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

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
                        fetchGameData(false);
                    }
                }
            }
        });
    }

    private fun fetchGameData(reset: Boolean)
    {
        layoutJuegosPlat.isRefreshing = true; // Evitamos que se pueda actualizar
        if (reset)
            currentPage = 1;
        else
            currentPage += 1;

        lifecycleScope.launch {
            activity?.let {fragActivity ->
                JuegoNetworkController.getJuegosPlataforma(currentPage, plataforma.id, fragActivity) {
                    loadingMoreData = false;
                    initialCreation = true;
                    if (reset)
                        juegos.clear();
                    juegos.addAll(it);
                    adapter.notifyDataSetChanged();
                    layoutJuegosPlat.isRefreshing = false;
                }
            }
        }
    }

    override fun onItemClick(item: JuegoModel)
    {
        val bundle = Bundle();
        bundle.putInt(JuegoInfoFragment.bundleJuegoInfoKey, item.id);
        val nextFrag = JuegoInfoFragment.newInstance(this, bundle);
        launchChildFragment(this, nextFrag);
    }

    override fun getFragmentTitle(context: Context): String
    {
        return getString(R.string.platform_games_title, plataforma.name);
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(saveJuegosKey, juegos as Serializable);
        outState.putParcelable(saveStateKey, layoutManager.onSaveInstanceState());
        outState.putInt(saveJuegosCurrentPage, currentPage);
    }
}
