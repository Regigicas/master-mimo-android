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
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

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
        imageView = layout.findViewById(R.id.favViewImage)
        textView = layout.findViewById(R.id.favTextView)
        imageView.clipToOutline = true // No me deja establecer por xml y sin esto no se ajustan las esquinas
        layout.clipToOutline = true
        layout.setOnClickListener {
            listener.onItemClick(juegoInfo)
        }
        updateData()
        return layout
    }

    fun updateData()
    {
        textView.text = juegoInfo.name
        imageView.loadFromURL(juegoInfo.backgroundImage)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putSerializable(saveJuegoFavInfoKey, juegoInfo)
    }
}
