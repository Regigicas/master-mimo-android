package es.upsa.mimo.gamesviewer.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.IntegerEditPreference
import es.upsa.mimo.gamesviewer.misc.PreferencesManager

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
        updateSummary();
        val prefManager = PreferencesManager.getPreferences(requireActivity());
        prefManager.registerOnSharedPreferenceChangeListener(this);
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?)
    {
        if (p1 == getString(R.string.config_max_elements_in_list))
            updateSummary();
    }

    private fun updateSummary()
    {
        val maxElementPref = findPreference<IntegerEditPreference>(getString(R.string.config_max_elements_in_list));
        if (maxElementPref != null && activity != null)
        {
            val currentValue = PreferencesManager.getMaxElementsInList(requireActivity());
            if (currentValue != null)
                maxElementPref.summary = getString(R.string.max_element_list_override, currentValue);
        }
    }
}
