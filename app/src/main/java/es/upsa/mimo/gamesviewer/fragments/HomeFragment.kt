package es.upsa.mimo.gamesviewer.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import es.upsa.mimo.datamodule.controllers.JuegoController
import es.upsa.mimo.datamodule.enums.JuegoOrderEnum
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.datamodule.models.QRModel
import es.upsa.mimo.gamesviewer.BuildConfig
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.adapters.GameGridViewAdapter
import es.upsa.mimo.gamesviewer.misc.PreferencesManager
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.launchChildFragment
import es.upsa.mimo.networkmodule.controllers.JuegoNetworkController
import kotlinx.coroutines.launch
import java.io.Serializable


class HomeFragment : MenuFragment(R.string.app_home), RLItemClickListener<JuegoModel>
{
    private val juegosCargados: MutableList<JuegoModel> = mutableListOf()
    private var initialCreation = false
    private val saveJuegosCargadosKey = "JuegosCargadosData"
    private val saveStateKey = "JuegosCargadosStateKey"
    private val saveJuegosCargadosCurrentPageKey = "JuegosCargadosCurrentPageKey"
    private lateinit var layoutManager: GridLayoutManager
    private var currentPage = 1
    private var collectionOrdering = JuegoOrderEnum.byDefault
    private var loadingData = false
    private var maxLoadedItems = 100
    private lateinit var adapter: GameGridViewAdapter
    private lateinit var progressLoad: ProgressBar
    private lateinit var swipeRefreshSearch: SwipeRefreshLayout
    val spinnerValues = listOf(
        R.string.order_default,
        R.string.order_name,
        R.string.order_name_inverse,
        R.string.order_released,
        R.string.order_released_inverse,
        R.string.order_rating,
        R.string.order_rating_inverse)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            val juegosGuardados = savedInstanceState.getSerializable(saveJuegosCargadosKey) as MutableList<*>
            for (juego in juegosGuardados)
                juegosCargados.add(juego as JuegoModel)

            layoutManager = GridLayoutManager(activity, calcularNumeroColumnas(requireContext()))
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(saveStateKey))
            currentPage = savedInstanceState.getInt(saveJuegosCargadosCurrentPageKey, 1)
        }

        maxLoadedItems = PreferencesManager.getMaxElementsInList(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (initialCreation)
            return

        if (!this::layoutManager.isInitialized)
            layoutManager = GridLayoutManager(activity, calcularNumeroColumnas(requireContext()))

        val spinner = view.findViewById<Spinner>(R.id.spinnerOrder)

        PreferencesManager.getStringConfig(requireContext(), R.string.config_home_sorting)?.let {
            try
            {
                val pos = it.toInt()
                updateSelectedOrdering(spinnerValues.get(pos))
                spinner.setSelection(pos)
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage)
            }
        }

        progressLoad = view.findViewById(R.id.progressLoad)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvJuegosHome)
        adapter = GameGridViewAdapter(juegosCargados, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                if (loadingData)
                    return

                if (dy > 0)
                {
                    val visibleItemCount = layoutManager.getChildCount()
                    val totalItemCount = layoutManager.getItemCount()
                    if (totalItemCount >= maxLoadedItems)
                        return

                    val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                    {
                        loadingData = true
                        swipeRefreshSearch.isRefreshing = true // Evitamos que se pueda actualizar
                        fetchGameData(false)
                    }
                }
            }
        })

        swipeRefreshSearch = view.findViewById(R.id.swipeRefreshGrid)
        swipeRefreshSearch.setOnRefreshListener {
            fetchGameData(true)
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                if (!initialCreation)
                    return

                val oldValue = collectionOrdering
                updateSelectedOrdering(spinnerValues.get(position))

                if (oldValue != collectionOrdering)
                    fetchGameData(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val buttonScan = view.findViewById<Button>(R.id.buttonQR)
        if (BuildConfig.FLAVOR == "paid")
        {
            buttonScan.setOnClickListener {
                val integrator = IntentIntegrator(requireActivity())
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                integrator.setOrientationLocked(false)
                integrator.initiateScan()
            }
        }
        else
            buttonScan.visibility = View.GONE

        if (juegosCargados.size > 0)
        {
            initialCreation = false
            progressLoad.visibility = View.GONE
            return
        }

        fetchGameData(true)
    }

    private fun fetchGameData(reset: Boolean)
    {
        if (reset)
            currentPage = 1
        else
            currentPage += 1

        lifecycleScope.launch {
            activity?.let {fragActivity ->
                JuegoNetworkController.getJuegos(currentPage, collectionOrdering, fragActivity) {
                    loadingData = false
                    initialCreation = true
                    if (reset)
                        juegosCargados.clear()
                    juegosCargados.addAll(it)
                    adapter.notifyDataSetChanged()
                    progressLoad.visibility = View.GONE
                    swipeRefreshSearch.isRefreshing = false
                }
            }
        }
    }

    private fun launchGameInfoFragment(id: Int)
    {
        val bundle = Bundle()
        bundle.putInt(JuegoInfoFragment.bundleJuegoInfoKey, id)
        val nextFrag = JuegoInfoFragment.newInstance(this, bundle)
        launchChildFragment(this, nextFrag)
    }

    override fun onItemClick(item: JuegoModel)
    {
        launchGameInfoFragment(item.id)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putSerializable(saveJuegosCargadosKey, juegosCargados as Serializable)
        outState.putParcelable(saveStateKey, layoutManager.onSaveInstanceState())
        outState.putInt(saveJuegosCargadosCurrentPageKey, currentPage)
    }

    private fun calcularNumeroColumnas(context: Context): Int
    {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / 240.0f + 0.5f).toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null)
        {
            val value = result.contents
            if (value != null)
            {
                var qrModel: QRModel? = null
                try
                {
                    qrModel = Gson().fromJson(value, QRModel::class.java)
                }
                catch (ex: Throwable)
                {
                    Log.e("error", ex.localizedMessage)
                }

                if (qrModel != null)
                    launchGameInfoFragment(qrModel.id)
            }
        } else
        {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateSelectedOrdering(id: Int)
    {
        when (id)
        {
            R.string.order_default -> collectionOrdering = JuegoOrderEnum.byDefault
            R.string.order_name -> collectionOrdering = JuegoOrderEnum.byName
            R.string.order_name_inverse -> collectionOrdering = JuegoOrderEnum.byNameInverse
            R.string.order_released -> collectionOrdering = JuegoOrderEnum.byReleaseDate
            R.string.order_released_inverse -> collectionOrdering = JuegoOrderEnum.byReleaseDateInverse
            R.string.order_rating -> collectionOrdering = JuegoOrderEnum.byRating
            R.string.order_rating_inverse -> collectionOrdering = JuegoOrderEnum.byRatingInverse
        }
    }
}
