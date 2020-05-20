package es.upsa.mimo.gamesviewer.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.CircleTransform
import es.upsa.mimo.gamesviewer.misc.PreferencesManager
import es.upsa.mimo.gamesviewer.misc.loadFromURL

class CircleImageView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs)
{
    private var imageView: ImageView
    init
    {
        inflate(context, R.layout.view_circle_imageview, this)

        imageView = findViewById(R.id.circleImageView)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.CircleImageView_circleImgSrc))
        imageView.contentDescription = attributes.getString(R.styleable.CircleImageView_circleImgDescription)

        attributes.recycle()
    }

    fun loadFromURL(url: String)
    {
        if (PreferencesManager.getBooleanConfig(context, R.string.config_lowdata_key))
            imageView.setImageDrawable(context.getDrawable(R.drawable.ic_gamepad_purple_200dp))
        else
        {
            Picasso.get().load(url)
                .placeholder(R.drawable.ic_gamepad_purple_200dp)
                .error(R.drawable.ic_gamepad_purple_200dp)
                .fit().centerCrop().into(imageView)
        }
    }
}
