package es.upsa.mimo.gamesviewer.misc

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference

class IntegerEditPreference : EditTextPreference
{
    constructor(context: Context?) : super(context);
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs);
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle);

    override fun getPersistedString(defaultReturnValue: String?): String
    {
        return getPersistedInt(-1).toString();
    }

    override fun persistString(value: String?): Boolean
    {
        value?.let {
            try
            {
                return persistInt(Integer.valueOf(it));
            }
            catch (ex: Throwable)
            {
                return false;
            }
        }

        return false;
    }
}
