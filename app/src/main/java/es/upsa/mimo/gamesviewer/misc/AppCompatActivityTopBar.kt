package es.upsa.mimo.gamesviewer.misc

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.activities.AboutActivity
import es.upsa.mimo.gamesviewer.activities.ConfigActivity

abstract class AppCompatActivityTopBar : AppCompatActivity()
{
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item != null)
        {
            when (item.itemId)
            {
                R.id.actBarInfo ->
                {
                    if (this is AboutActivity)
                        return super.onOptionsItemSelected(item)

                    if (this is ConfigActivity)
                        finish()
                    startActivity(Intent(this, AboutActivity::class.java))
                    return true
                }
                R.id.actBarConfig ->
                {
                    if (this is ConfigActivity)
                        return super.onOptionsItemSelected(item)

                    if (this is AboutActivity)
                        finish()
                    startActivity(Intent(this, ConfigActivity::class.java))
                    return true
                }
                else -> {}
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
