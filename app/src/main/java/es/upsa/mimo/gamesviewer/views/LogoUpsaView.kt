package es.upsa.mimo.gamesviewer.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import es.upsa.mimo.gamesviewer.R

class LogoUpsaView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs)
{
    init
    {
        inflate(context, R.layout.view_logo_upsa, this)

        val textView: TextView = findViewById(R.id.textViewDescription)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LogoUpsaView)
        textView.text = attributes.getString(R.styleable.LogoUpsaView_text)
        attributes.recycle()
    }
}
