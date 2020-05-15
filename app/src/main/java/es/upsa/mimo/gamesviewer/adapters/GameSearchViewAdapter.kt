package es.upsa.mimo.gamesviewer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.loadFromURL

class GameSearchViewAdapter(private val dataSet: List<JuegoModel>, private val listener: RLItemClickListener<JuegoModel>) : RecyclerView.Adapter<GameSearchViewAdapter.ViewHolder>()
{
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        var imageView = view.findViewById<ImageView>(R.id.imageViewJuego);
        var textViewNombre = view.findViewById<TextView>(R.id.textViewNombreJuego);
        var textViewFecha = view.findViewById<TextView>(R.id.textViewFechaSalida);

        fun bind(juegoInfo: JuegoModel)
        {
            imageView.loadFromURL(juegoInfo.getBackgroundString());
            textViewNombre.text = juegoInfo.name;
            textViewFecha.text = juegoInfo.released;
            view.setOnClickListener {
                listener.onItemClick(juegoInfo);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_search_game,
                parent, false));
    }

    override fun getItemCount(): Int
    {
        return dataSet.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val juegoInfo = dataSet.get(position);
        holder.bind(juegoInfo);
    };
}
