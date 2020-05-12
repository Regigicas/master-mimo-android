package es.upsa.mimo.gamesviewer.misc

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class MenuFragment(val titleId: Int) : Fragment()
{
    companion object
    {
        val BackStackFragmentName = "MenuFragment";
    }
}
