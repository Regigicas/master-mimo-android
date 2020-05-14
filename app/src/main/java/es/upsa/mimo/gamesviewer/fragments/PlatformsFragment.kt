package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.MenuFragment
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.gamesviewer.adapters.PlatformViewAdapter
import es.upsa.mimo.networkmodule.controllers.PlataformaNetworkController
import kotlinx.coroutines.launch
import java.io.Serializable


class PlatformsFragment : MenuFragment(R.string.app_platforms), RLItemClickListener<PlatformModel>
{
    private val plataformas: MutableList<PlatformModel> = mutableListOf();
    private var initialCreation = false;
    private val savePlatformsKey = "PlatformsLoadedData";
    private val saveStateKey = "PlatformsStateKey";
    private val layoutManager = LinearLayoutManager(activity);

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            val platformGuardadas = savedInstanceState.getSerializable(savePlatformsKey) as MutableList<*>;
            for (plat in platformGuardadas)
                plataformas.add(plat as PlatformModel);

            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(saveStateKey));
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_platforms, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);

        if (initialCreation) // Las plataformas no son algo que cambie, asi que las dejamos siempre cargadas
            return;

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlataformas);
        val adapter = PlatformViewAdapter(plataformas, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.adapter = adapter;
        recyclerView.layoutManager = layoutManager;
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        val progressLoad = view.findViewById<ProgressBar>(R.id.progressLoad);

        if (plataformas.size > 0)
        {
            initialCreation = false;
            progressLoad.visibility = View.GONE;
            return;
        }

        lifecycleScope.launch {
            activity?.let {fragActivity ->
                PlataformaNetworkController.getListadoPlataformas(fragActivity) {
                    if (initialCreation) // Evitamos que se pueda llamar varias veces si falla la conexion
                        return@getListadoPlataformas;

                    initialCreation = true;
                    plataformas.addAll(it);
                    adapter.notifyDataSetChanged();
                    progressLoad.visibility = View.GONE;
                }
            }
        }
    }

    override fun onItemClick(item: PlatformModel)
    {
        val bundle = Bundle();
        item.id?.let {
            bundle.putInt(PlatformInfoFragment.bundlePlatformInfoKey, it);
        };
        val nextFrag = PlatformInfoFragment.newInstance(this, bundle);
        Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(savePlatformsKey, plataformas as Serializable);
        outState.putParcelable(saveStateKey, layoutManager.onSaveInstanceState());
    }
}
