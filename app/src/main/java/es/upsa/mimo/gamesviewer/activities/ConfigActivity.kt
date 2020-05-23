package es.upsa.mimo.gamesviewer.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.datamodule.enums.UsuarioResultEnum
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.fragments.ChangePasswordFragment
import es.upsa.mimo.gamesviewer.fragments.ConfirmationFragment
import es.upsa.mimo.gamesviewer.fragments.SettingsFragment
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar
import es.upsa.mimo.gamesviewer.notifications.NotificationMgr
import kotlinx.coroutines.launch
import java.util.*

class ConfigActivity : AppCompatActivityTopBar()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configView, SettingsFragment())
            .commit()

        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            ConfirmationFragment().show(supportFragmentManager, null)
        }

        val buttonChangePassword = findViewById<Button>(R.id.buttonChangePassword)
        buttonChangePassword.setOnClickListener {
            ChangePasswordFragment().show(supportFragmentManager, null)
        }

        var canFireListener = false
        val switchFavNoti = findViewById<SwitchCompat>(R.id.switchFavNoti)
        lifecycleScope.launch {
            switchFavNoti.isChecked = UsuarioController.hasNotifyFavRelease(this@ConfigActivity)
            canFireListener = true
        }

        switchFavNoti.setOnCheckedChangeListener { _, b ->
            if (!canFireListener)
                return@setOnCheckedChangeListener

            lifecycleScope.launch {
                UsuarioController.setUserNotifyFavRelease(this@ConfigActivity, b)

                if (b)
                {
                    val favs = UsuarioController.getFavoriteList(this@ConfigActivity)
                    val currentTime = Calendar.getInstance().getTime()
                    val notMgr = NotificationMgr(this@ConfigActivity)
                    for (fav in favs)
                    {
                        if (fav.releaseDate.after(currentTime))
                        {
                            val msDiff = (fav.releaseDate.time - currentTime.time)
                            notMgr.sendNotification(fav, msDiff)
                        }
                    }
                }
                else
                    NotificationMgr(this@ConfigActivity).cancelAllNotifications()
            }
        }

        val textViewUserName = findViewById<TextView>(R.id.textViewUserName)
        val viewLoggedUser = findViewById<ConstraintLayout>(R.id.viewLoggedUser)
        lifecycleScope.launch {
            val userInfo = UsuarioController.getActiveUser(this@ConfigActivity)
            if (userInfo == null)
            {
                viewLoggedUser.visibility = View.GONE
                buttonLogout.visibility = View.GONE
                buttonChangePassword.visibility = View.GONE
                val separator1 = findViewById<View>(R.id.separator1)
                separator1.visibility = View.GONE
                val separator2 = findViewById<View>(R.id.separator2)
                separator2.visibility = View.GONE
            }
            else
                textViewUserName.text = getString(R.string.user_name_email, userInfo.username, userInfo.email)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item != null)
        {
            when (item.itemId)
            {
                android.R.id.home ->
                {
                    finish()
                    return true
                }
                else -> {}
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun doLogout()
    {
        UsuarioController.logoutUser(this)
        val intent = Intent(this@ConfigActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun doChangePassword(oldPass: String, newPass: String, newPassRetype: String)
    {
        var errorMsg = R.string.password_change_ok
        if (oldPass == newPass)
            errorMsg = R.string.oldpass_newpass_equal
        else if (newPass != newPassRetype)
            errorMsg = R.string.error_pass_mismatch
        else if (newPass.length < 8)
            errorMsg = R.string.min_password_length

        if (errorMsg != R.string.password_change_ok)
        {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            val result = UsuarioController.changePassword(oldPass, newPass, this@ConfigActivity)
            var msg = result.stringValue()
            if (result == UsuarioResultEnum.ok)
            {
                doLogout()
                msg = R.string.password_change_ok
            }

            Toast.makeText(this@ConfigActivity, msg, Toast.LENGTH_LONG).show()
        }
    }
}
