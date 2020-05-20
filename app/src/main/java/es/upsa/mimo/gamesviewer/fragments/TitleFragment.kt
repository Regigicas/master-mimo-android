package es.upsa.mimo.gamesviewer.fragments

import android.content.Context
import androidx.fragment.app.Fragment

abstract class TitleFragment : Fragment()
{
    abstract fun getFragmentTitle(context: Context): String
}
