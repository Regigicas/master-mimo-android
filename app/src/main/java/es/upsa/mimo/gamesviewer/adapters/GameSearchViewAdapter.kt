package es.upsa.mimo.gamesviewer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener
import es.upsa.mimo.gamesviewer.misc.loadFromURL
import es.upsa.mimo.gamesviewer.views.CircleImageView

class GameSearchViewAdapter(private val dataSet: List<JuegoModel>, private val listener: RLItemClickListener<JuegoModel>) : RecyclerView.Adapter<GameSearchViewAdapter.ViewHolder>()
{
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        val imageView = view.findViewById<CircleImageView>(R.id.imageViewJuego);
        val textViewNombre = view.findViewById<TextView>(R.id.textViewNombreJuego);
        val textViewFecha = view.findViewById<TextView>(R.id.textViewFechaSalida);
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar);

        fun bind(juegoInfo: JuegoModel)
        {
            imageView.loadFromURL(juegoInfo.getBackgroundString());
            textViewNombre.text = juegoInfo.name;
            textViewFecha.text = juegoInfo.released;
            juegoInfo.rating?.let {
                ratingBar.rating = it;
            }
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
