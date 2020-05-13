package es.upsa.mimo.gamesviewer.misc

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class MenuFragment(val titleId: Int) : TitleFragment()
{
    companion object
    {
        val BackStackFragmentName = "MenuFragment";
    }

    override fun getFragmentTitle(context: Context): String
    {
        return context.getString(titleId);
    }
}
