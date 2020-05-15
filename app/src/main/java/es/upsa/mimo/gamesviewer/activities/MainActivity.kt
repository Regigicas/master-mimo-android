package es.upsa.mimo.gamesviewer.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.gamesviewer.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        var autoLoginOk = false;
        lifecycleScope.launch {
            if (UsuarioController.tryAutoLogin(this@MainActivity))
            {
                autoLoginOk = true;
                val intent = Intent(this@MainActivity, HomeActivity::class.java);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

        Handler().postDelayed({
            if (autoLoginOk)
                return@postDelayed;

            val intent = Intent(this@MainActivity, LoginActivity::class.java);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }, 1000);
    }
}
