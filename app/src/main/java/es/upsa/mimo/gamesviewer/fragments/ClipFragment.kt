package es.upsa.mimo.gamesviewer.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ui.PlayerView
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.findFragmentByClassName
import es.upsa.mimo.gamesviewer.services.ClipService

class ClipFragment : BackFragment()
{
    companion object
    {
        val bundleJuegoInfoKey = "ClipFragmentJuegoInfo"

        fun newInstance(owner: JuegoInfoFragment?, bundle: Bundle?): ClipFragment
        {
            val nuevoFrag = ClipFragment();
            nuevoFrag.arguments = bundle;
            nuevoFrag.ownerFragment = owner;
            return nuevoFrag;
        }
    }

    private val saveJuegoClipInfoKey = "JuegoClipsInfoKey"
    private lateinit var juegoInfo: JuegoModel;
    private lateinit var playerView: PlayerView;
    private var exoIntent: Intent? = null;
    private var isBinded = false;

    private val connection = object : ServiceConnection
    {
        override fun onServiceDisconnected(name: ComponentName?)
        {
            playerView.player?.release();
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?)
        {
            if (service is ClipService.ClipServiceBinder)
                playerView.player = service.getExoPlayer();
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            juegoInfo = savedInstanceState.getSerializable(saveJuegoClipInfoKey) as JuegoModel
            if (activity != null)
                ownerFragment = this.findFragmentByClassName(JuegoInfoFragment::class.java.name, requireActivity().supportFragmentManager) // La vista solo puede ser creada por esta clase
        }
    }

    override fun onCreateChildView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        if (!this::juegoInfo.isInitialized && arguments != null)
            juegoInfo = requireArguments().getSerializable(bundleJuegoInfoKey) as JuegoModel
        if (!this::juegoInfo.isInitialized)
            throw AssertionError(R.string.assert_needed_data_not_present);
        return inflater.inflate(R.layout.fragment_clip, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState);
        playerView = view.findViewById(R.id.clipPlayerView);
        exoIntent = juegoInfo.clip?.clips?.p640?.let { ClipService.newIntent(it, requireContext()); }
        if (exoIntent != null)
        {
            isBinded = true;
            requireActivity().bindService(exoIntent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    override fun onStop()
    {
        super.onStop();
        if (exoIntent != null && isBinded)
        {
            isBinded = false;
            requireActivity().unbindService(connection);
        }
    }

    override fun getFragmentTitle(context: Context): String
    {
        return juegoInfo.name;
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putSerializable(saveJuegoClipInfoKey, juegoInfo)
    }
}
