package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.MenuFragment
import es.upsa.mimo.gamesviewer.misc.RVItemClickListener
import es.upsa.mimo.gamesviewer.misc.Util
import es.upsa.mimo.gamesviewer.views.PlatformViewAdapter
import es.upsa.mimo.networkmodule.controllers.PlataformaNetworkController
import kotlinx.coroutines.launch


class PlatformsFragment : MenuFragment(R.string.app_platforms), RVItemClickListener<PlatformModel>
{
    private val plataformas: MutableList<PlatformModel> = mutableListOf();
    private var initialCreation = false;

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
        recyclerView.layoutManager = LinearLayoutManager(activity);

        lifecycleScope.launch {
            activity?.let {fragActivity ->
                PlataformaNetworkController.getListadoPlataformas(fragActivity) {
                    if (initialCreation) // Evitamos que se pueda llamar varias veces si falla la conexion
                        return@getListadoPlataformas;

                    initialCreation = true;
                    plataformas.addAll(it);
                    adapter.notifyDataSetChanged();
                    val progressLoad = view.findViewById<ProgressBar>(R.id.progressLoad);
                    progressLoad.visibility = View.GONE;
                }
            }
        }
    }

    override fun onItemClick(item: PlatformModel)
    {
        val nextFrag = PlatformInfoFragment(this);
        val bundle = Bundle();
        item.id?.let {
            bundle.putInt(PlatformInfoFragment.bundlePlatformInfoKey, it);
            nextFrag.arguments = bundle;
        };

        Util.launchChildFragment(this, nextFrag, activity!!.supportFragmentManager);
    }
}
