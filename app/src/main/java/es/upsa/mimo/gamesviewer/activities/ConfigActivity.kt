package es.upsa.mimo.gamesviewer.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar
import es.upsa.mimo.gamesviewer.views.LogoUpsaView

class ConfigActivity : AppCompatActivityTopBar()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
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
