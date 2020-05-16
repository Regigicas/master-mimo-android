package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
import android.view.MenuItem
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.SettingsFragment
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar

class ConfigActivity : AppCompatActivityTopBar()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configView, SettingsFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item != null)
        {
            when (item.itemId)
            {
                android.R.id.home ->
                {
                    finish();
                    return true;
                }
                else -> {}
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
