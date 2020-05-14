package es.upsa.mimo.gamesviewer.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.datamodule.enums.UsuarioResultEnum
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar
import es.upsa.mimo.gamesviewer.misc.Util
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivityTopBar(), TextWatcher
{
    private var textEditUsername: EditText? = null;
    private var textEditPassword: EditText? = null;
    private var buttonLogin: Button? = null;
    private var pendingLogin: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lifecycleScope.launch {
            if (UsuarioController.tryAutoLogin(this@MainActivity))
            {
                val intent = Intent(this@MainActivity, HomeActivity::class.java);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

        textEditUsername = findViewById(R.id.textEditUsername);
        textEditPassword = findViewById(R.id.textEditPassword);
        buttonLogin = findViewById<Button>(R.id.buttonLogin);

        if (textEditUsername == null || textEditPassword == null || buttonLogin == null)
            throw AssertionError(getString(R.string.assert_view_not_created));

        val registerButton = findViewById<Button>(R.id.buttonRegister);
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java));
        }

        textEditPassword!!.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE)
            {
                tryLogin();
                true
            }
            else
                false
        }

        textEditUsername!!.addTextChangedListener(this)
        textEditPassword!!.addTextChangedListener(this)

        buttonLogin!!.setOnClickListener {
            tryLogin();
        }
    }

    fun tryLogin()
    {
        Util.hideKeyBoard(this);
        if (pendingLogin)
            return;

        pendingLogin = true;
        lifecycleScope.launch {
            val result = UsuarioController.tryLogin(textEditUsername!!.text.toString().trim(),
                textEditPassword!!.text.toString().trim(), this@MainActivity);

            if (result.first != UsuarioResultEnum.ok)
            {
                pendingLogin = false;
                Toast.makeText(this@MainActivity, getString(result.first.stringValue()),
                    Toast.LENGTH_SHORT).show();
            }
            else
            {
                val storeResult = UsuarioController.saveUserLoginData(textEditUsername!!.text.toString().trim(),
                    textEditPassword!!.text.toString().trim(), this@MainActivity);

                UsuarioController.saveActiveUserId(result.second!!.id!!, this@MainActivity);
                if (storeResult == false)
                    Toast.makeText(this@MainActivity, getString(R.string.error_no_autologin),
                        Toast.LENGTH_LONG).show();

                pendingLogin = false;
                startActivity(Intent(this@MainActivity, HomeActivity::class.java));
            }
        }
    }

    override fun afterTextChanged(p0: Editable?)
    {
        buttonLogin!!.isEnabled = validateAllFields();
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    fun validateAllFields(): Boolean
    {
        if (TextUtils.isEmpty(textEditUsername!!.text) || textEditUsername!!.text.length < 5)
            return false;

        if (TextUtils.isEmpty(textEditPassword!!.text) || textEditPassword!!.text.length < 8)
            return false;

        return true;
    }
}
