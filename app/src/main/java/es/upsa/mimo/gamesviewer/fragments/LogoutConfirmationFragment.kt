package es.upsa.mimo.gamesviewer.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.ConfigActivity

class LogoutConfirmationFragment : DialogFragment()
{
    companion object
    {
        @JvmStatic
        fun newInstance() : LogoutConfirmationFragment
        {
            return LogoutConfirmationFragment()
        }
    }

    lateinit var owner: ConfigActivity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        owner = activity as ConfigActivity // La vista solo puede ser creada por esta clase
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.logout_yes) { _, _ ->
                    owner.doLogout()
                }
                .setNegativeButton(R.string.logout_no) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.assert_needed_data_not_present))
    }
}
