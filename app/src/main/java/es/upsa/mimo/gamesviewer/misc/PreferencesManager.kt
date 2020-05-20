package es.upsa.mimo.gamesviewer.misc

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import es.upsa.mimo.gamesviewer.R

class PreferencesManager
{
    companion object
    {
        fun getPreferences(context: Context): SharedPreferences
        {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }

        fun getMaxElementsInList(context: Context): Int
        {
            val preferences = getPreferences(context);
            val default = context.getString(R.string.max_elements_num_default).toInt();
            return preferences.getInt(context.getString(R.string.config_max_elements_in_list), default);
        }

        fun getBooleanConfig(context: Context, key: Int): Boolean
        {
            val preferences = getPreferences(context);
            return preferences.getBoolean(context.getString(key), false);
        }

        fun isAutoLoginEnable(context: Context): Boolean
        {
            val preferences = getPreferences(context);
            return preferences.getBoolean(context.getString(R.string.config_autologin_status), true);
        }

        fun getStringConfig(context: Context, key: Int): String?
        {
            val preferences = getPreferences(context);
            return preferences.getString(context.getString(key), null);
        }
    }
}
