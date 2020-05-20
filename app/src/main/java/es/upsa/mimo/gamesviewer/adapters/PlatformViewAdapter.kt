package es.upsa.mimo.gamesviewer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.RLItemClickListener

class PlatformViewAdapter(private val dataSet: List<PlatformModel>, private val listener: RLItemClickListener<PlatformModel>) : RecyclerView.Adapter<PlatformViewAdapter.ViewHolder>()
{
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view)
    {
        val imageView = view.findViewById<ImageView>(R.id.imageViewLogo)
        val textView = view.findViewById<TextView>(R.id.textViewPlatformName)

        fun bind(platformInfo: PlatformModel)
        {
            textView.text = platformInfo.name
            imageView.setImageResource(platformInfo.getImgFile(true))
            view.setOnClickListener {
                listener.onItemClick(platformInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_platform,
            parent, false))
    }

    override fun getItemCount(): Int
    {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val platformInfo = dataSet.get(position)
        holder.bind(platformInfo)
    }
}
