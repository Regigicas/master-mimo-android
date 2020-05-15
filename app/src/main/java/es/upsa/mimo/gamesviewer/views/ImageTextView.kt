package es.upsa.mimo.gamesviewer.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import es.upsa.mimo.gamesviewer.R

class ImageTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs)
{
    init
    {
        inflate(context, R.layout.view_logo_view, this);

        val textView: TextView = findViewById(R.id.textViewDescription);
        val imageView: ImageView = findViewById(R.id.imageViewLogo);
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        textView.text = attributes.getString(R.styleable.ImageTextView_text);
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.ImageTextView_imgSrc));
        imageView.contentDescription = attributes.getString(R.styleable.ImageTextView_imgDecription);

        attributes.recycle()
    }
}
