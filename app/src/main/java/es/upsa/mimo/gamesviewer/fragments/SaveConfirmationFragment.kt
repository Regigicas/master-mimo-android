package es.upsa.mimo.gamesviewer.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import es.upsa.mimo.gamesviewer.R

class SaveConfirmationFragment : DialogFragment()
{
    companion object
    {
        val requestCode = 102
        val requestCodeOk = 104

        @JvmStatic
        fun newInstance(owner: Fragment) : SaveConfirmationFragment
        {
            val frag = SaveConfirmationFragment()
            frag.setTargetFragment(owner, requestCode)
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.save_img_confirm)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    targetFragment?.onActivityResult(requestCode, requestCodeOk, requireActivity().intent)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.assert_needed_data_not_present))
    }
}
