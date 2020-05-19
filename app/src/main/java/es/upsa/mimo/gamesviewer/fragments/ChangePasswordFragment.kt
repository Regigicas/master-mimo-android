package es.upsa.mimo.gamesviewer.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.ConfigActivity

class ChangePasswordFragment : DialogFragment()
{
    companion object
    {
        @JvmStatic
        fun newInstance() : ChangePasswordFragment
        {
            return ChangePasswordFragment();
        }
    }

    private lateinit var owner: ConfigActivity;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            owner = activity as ConfigActivity; // La vista solo puede ser creada por esta clase
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it);
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.fragment_change_password, null))
                .setPositiveButton(R.string.change_password) { _, _ ->
                    dialog?.let { diag ->
                        val textEditOldPassword = diag.findViewById<TextView>(R.id.textEditOldPassword);
                        val textEditPassword = diag.findViewById<TextView>(R.id.textEditPassword);
                        val textEditPasswordRepeat = diag.findViewById<TextView>(R.id.textEditPasswordRepeat);
                        owner.doChangePassword(textEditOldPassword.text.toString(), textEditPassword.text.toString(),
                            textEditPasswordRepeat.text.toString());
                    }
                }
                .setNegativeButton(R.string.logout_no) { _, _ -> };
            builder.create();
        } ?: throw IllegalStateException(getString(R.string.assert_needed_data_not_present));
    }
}
