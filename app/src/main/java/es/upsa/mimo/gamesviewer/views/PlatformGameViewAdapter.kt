package es.upsa.mimo.gamesviewer.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.GVItemClickListener

class PlatformGameViewAdapter(private val dataSet: List<JuegoModel>, private val listener: GVItemClickListener<JuegoModel>) : RecyclerView.Adapter<PlatformGameViewAdapter.ViewHolder>()
{
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        var imageView = view.findViewById<ImageView>(R.id.imageViewJuego);
        var textViewNombre = view.findViewById<TextView>(R.id.textViewNombreJuego);
        var textViewFecha = view.findViewById<TextView>(R.id.textViewFechaSalida);

        fun bind(juegoInfo: JuegoModel)
        {
            Picasso.get().load(juegoInfo.getBackgroundString()).fit().centerCrop().into(imageView);
            textViewNombre.text = juegoInfo.name;
            textViewFecha.text = juegoInfo.released;
            view.setOnClickListener {
                listener.onItemClick(juegoInfo);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_platform_game,
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
