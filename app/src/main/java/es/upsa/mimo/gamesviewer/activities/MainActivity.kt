package es.upsa.mimo.gamesviewer.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.PreferencesManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    private var wasInitialized = false;
    private var saveWasInitializedKey = "WasInitializedKey";

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        supportActionBar?.title = getString(R.string.title_loading);

        if (savedInstanceState != null)
            wasInitialized = savedInstanceState.getBoolean(saveWasInitializedKey);

        if (wasInitialized)
            return;

        wasInitialized = true;
        var autoLoginOk = false;
        MainScope().launch {
            if (PreferencesManager.isAutoLoginEnable(this@MainActivity) &&
                UsuarioController.tryAutoLogin(this@MainActivity))
            {
                autoLoginOk = true;
                val intent = Intent(this@MainActivity, HomeActivity::class.java);
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK;
                startActivity(intent);
            }
        }

        Handler().postDelayed({
            if (autoLoginOk)
                return@postDelayed;

            val intent = Intent(this@MainActivity, LoginActivity::class.java);
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK;
            startActivity(intent);
        }, 1000);
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(saveWasInitializedKey, wasInitialized);
    }
}
