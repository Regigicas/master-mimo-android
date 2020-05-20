package es.upsa.mimo.gamesviewer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.PreferencesManager
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.findFragmentByClassName
import es.upsa.mimo.gamesviewer.misc.loadFromURL

class FavoriteItemFragment : Fragment()
{
    companion object
    {
        @JvmStatic
        fun newInstance(juegoInfo: JuegoFav, listener: RLItemClickListener<JuegoFav>) : FavoriteItemFragment
        {
            val frag = FavoriteItemFragment()
            frag.juegoInfo = juegoInfo
            frag.listener = listener
            return frag
        }
    }

    lateinit var juegoInfo: JuegoFav
    lateinit var listener: RLItemClickListener<JuegoFav>
    private val saveJuegoFavInfoKey = "JuegoFavInfoKey"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
        {
            juegoInfo = savedInstanceState.getSerializable(saveJuegoFavInfoKey) as JuegoFav
            if (activity != null)
                listener = findFragmentByClassName(FavoriteFragment::class.java.name, requireActivity().supportFragmentManager) as FavoriteFragment // La vista solo puede ser creada por esta clase
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val layout =  inflater.inflate(R.layout.view_game_card, container, false)
        val imageView = layout.findViewById<ImageView>(R.id.favViewImage)
        val textView = layout.findViewById<TextView>(R.id.favTextView)
        textView.text = juegoInfo.name
        imageView.loadFromURL(juegoInfo.backgroundImage)
        imageView.clipToOutline = true // No me deja establecer por xml y sin esto no se adjustan las esquinas
        layout.clipToOutline = true
        layout.setOnClickListener {
            listener.onItemClick(juegoInfo)
        }
        return layout
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putSerializable(saveJuegoFavInfoKey, juegoInfo)
    }
}
