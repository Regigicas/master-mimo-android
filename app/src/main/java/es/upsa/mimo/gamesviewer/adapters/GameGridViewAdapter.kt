package es.upsa.mimo.gamesviewer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.loadFromURL

class GameGridViewAdapter(private val dataSet: List<JuegoModel>, private val listener: RLItemClickListener<JuegoModel>) : RecyclerView.Adapter<GameGridViewAdapter.ViewHolder>()
{
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        val imageViewJuego = view.findViewById<ImageView>(R.id.imageViewJuego)
        val textViewNombreJuego = view.findViewById<TextView>(R.id.textViewNombreJuego)

        fun bind(juegoInfo: JuegoModel)
        {
            imageViewJuego.loadFromURL(juegoInfo.getBackgroundString())
            imageViewJuego.clipToOutline = true
            textViewNombreJuego.text = juegoInfo.name
            view.setOnClickListener {
                listener.onItemClick(juegoInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_grid_game, parent, false))
    }

    override fun getItemCount(): Int
    {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val juegoInfo = dataSet.get(position)
        holder.bind(juegoInfo)
    }
}
