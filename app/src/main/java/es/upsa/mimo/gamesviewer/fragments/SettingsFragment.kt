package es.upsa.mimo.gamesviewer.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.IntegerEditPreference
import es.upsa.mimo.gamesviewer.misc.PreferencesManager

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)
        updateSummaryMaxElements()
        updateSummaryOrdering()
        val prefManager = PreferencesManager.getPreferences(requireActivity())
        prefManager.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?)
    {
        if (context != null)
        {
            if (p1 == requireContext().getString(R.string.config_max_elements_in_list))
                updateSummaryMaxElements()
            else if (p1 == requireContext().getString(R.string.config_home_sorting))
                updateSummaryOrdering()
        }
    }

    private fun updateSummaryMaxElements()
    {
        val maxElementPref = findPreference<IntegerEditPreference>(getString(R.string.config_max_elements_in_list))
        if (maxElementPref != null && activity != null)
        {
            val currentValue = PreferencesManager.getMaxElementsInList(requireActivity())
            maxElementPref.text = currentValue.toString()
            maxElementPref.summary = getString(R.string.max_element_list_override, currentValue)
        }
    }

    private fun updateSummaryOrdering()
    {
        val selectedOrdering = findPreference<ListPreference>(getString(R.string.config_home_sorting))
        if (selectedOrdering != null && activity != null)
        {
            val currentValue =
                PreferencesManager.getStringConfig(requireContext(), R.string.config_home_sorting)
                    ?.toInt() ?: 0
            val values = resources.getStringArray(R.array.spinner_values)
            selectedOrdering.summary = getString(R.string.summary_ordering, values.get(currentValue))
        }
    }
}
