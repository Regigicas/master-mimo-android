package es.upsa.mimo.gamesviewer.fragments

import android.content.Context

abstract class MenuFragment(val titleId: Int) : TitleFragment()
{
    companion object
    {
        val BackStackFragmentName = "MenuFragment"
    }

    override fun getFragmentTitle(context: Context): String
    {
        return context.getString(titleId)
    }
}
