package es.upsa.mimo.gamesviewer.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import es.upsa.mimo.gamesviewer.R

class PermissionDialogFragment : DialogFragment()
{
    companion object
    {
        val requestCode = 100
        val requestCodeOk = 101

        @JvmStatic
        fun newInstance(owner: Fragment) : PermissionDialogFragment
        {
            val frag = PermissionDialogFragment()
            frag.setTargetFragment(owner, requestCode)
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.store_perm_desc)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    targetFragment?.onActivityResult(requestCode, requestCodeOk, requireActivity().intent)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.assert_needed_data_not_present))
    }
}
