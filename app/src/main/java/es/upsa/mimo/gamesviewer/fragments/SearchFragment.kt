package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.MenuFragment
import es.upsa.mimo.gamesviewer.adapters.GameSearchViewAdapter
import es.upsa.mimo.gamesviewer.misc.PreferencesManager
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.launchChildFragment
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController
import kotlinx.coroutines.launch
import java.io.Serializable


class SearchFragment : MenuFragment(R.string.app_search), RLItemClickListener<JuegoModel>
{
    private val juegosCargados: MutableList<JuegoModel> = mutableListOf();
    private val layoutManager = LinearLayoutManager(activity);
    private val saveJuegosCargadosKey = "JuegosSearchLoadedData";
    private val saveStateKey = "JuegosSearchStateKey";
    private val saveSearchQueryKey = "JuegosSearchQueryKey";
    private var loadingData = false;
    private lateinit var swipeRefreshSearch: SwipeRefreshLayout;
    private var maxLoadedItems = 100;
    private var currentPage = 1;
    private var lastSearchTerm = "";
    private val adapter = GameSearchViewAdapter(juegosCargados, this);

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            val juegosGuardados = savedInstanceState.getSerializable(saveJuegosCargadosKey) as MutableList<*>;
            for (juego in juegosGuardados)
                juegosCargados.add(juego as JuegoModel);

            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(saveStateKey));
            lastSearchTerm = savedInstanceState.getString(saveSearchQueryKey, "");
        }

        maxLoadedItems = PreferencesManager.getMaxElementsInList(requireContext());
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private fun setupView(view: View)
    {
        val searchBar = view.findViewById<SearchView>(R.id.searchViewGames);
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(p0: String?): Boolean
            {
                if (p0 != null)
                    clickSubmit(p0);
                searchBar.clearFocus();
                return true;
            }

            override fun onQueryTextChange(p0: String?): Boolean
            {
                return false;
            }
        });

        swipeRefreshSearch = view.findViewById(R.id.swipeRefreshSearch);
        swipeRefreshSearch.setOnRefreshListener {
            fetchGameData(true);
        }

        val rvJuegosPlat = view.findViewById<RecyclerView>(R.id.rvJuegosSearch);
        rvJuegosPlat.setHasFixedSize(true);
        rvJuegosPlat.adapter = adapter;
        rvJuegosPlat.layoutManager = layoutManager;
        rvJuegosPlat.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        rvJuegosPlat.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy);

                if (loadingData)
                    return;

                if (dy > 0)
                {
                    searchBar.clearFocus();

                    val visibleItemCount = layoutManager.getChildCount();
                    val totalItemCount = layoutManager.getItemCount();
                    if (totalItemCount >= maxLoadedItems)
                        return;

                    val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                    {
                        loadingData = true;
                        fetchGameData(false);
                    }
                }
            }
        });
    }

    private fun fetchGameData(reset: Boolean)
    {
        if (reset)
        {
            juegosCargados.clear();
            currentPage = 1;
        }
        else
            currentPage += 1;

        lifecycleScope.launch {
            activity?.let {fragActivity ->
                JuegoNetworkController.getJuegosQuery(currentPage, lastSearchTerm, fragActivity) {
                    loadingData = false;
                    if (it.size > 0)
                    {
                        juegosCargados.addAll(it);
                        adapter.notifyDataSetChanged();
                        swipeRefreshSearch.isRefreshing = false;
                    }
                }
            }
        }
    }

    private fun clickSubmit(nombre: String)
    {
        if (loadingData)
            return;

        lastSearchTerm = nombre;
        if (TextUtils.isEmpty(nombre))
            return;

        loadingData = true;
        fetchGameData(true);
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(saveJuegosCargadosKey, juegosCargados as Serializable);
        outState.putParcelable(saveStateKey, layoutManager.onSaveInstanceState());
        outState.putString(saveSearchQueryKey, lastSearchTerm);
    }

    override fun onItemClick(item: JuegoModel)
    {
        val bundle = Bundle();
        bundle.putInt(JuegoInfoFragment.bundleJuegoInfoKey, item.id);
        val nextFrag = JuegoInfoFragment.newInstance(this, bundle);
        launchChildFragment(this, nextFrag);
    }
}
