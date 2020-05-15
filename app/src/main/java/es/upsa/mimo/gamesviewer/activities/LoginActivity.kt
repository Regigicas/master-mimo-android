package es.upsa.mimo.gamesviewer.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import es.upsa.mimo.datamodule.controllers.UsuarioController
import es.upsa.mimo.datamodule.enums.UsuarioResultEnum
import es.upsa.mimo.gamesviewer.R
import es.upsa.mimo.gamesviewer.misc.AppCompatActivityTopBar
import es.upsa.mimo.gamesviewer.misc.hideKeyBoard
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivityTopBar(), TextWatcher
{
    private lateinit var textEditUsername: EditText;
    private lateinit var textEditPassword: EditText;
    private lateinit var buttonLogin: Button;
    private var pendingLogin: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textEditUsername = findViewById(R.id.textEditUsername);
        textEditPassword = findViewById(R.id.textEditPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        val registerButton = findViewById<Button>(R.id.buttonRegister);
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java));
        }

        textEditPassword.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE)
            {
                tryLogin();
                true
            }
            else
                false
        }

        textEditUsername.addTextChangedListener(this)
        textEditPassword.addTextChangedListener(this)

        buttonLogin.setOnClickListener {
            tryLogin();
        }
    }

    fun tryLogin()
    {
        hideKeyBoard();
        if (pendingLogin)
            return;

        pendingLogin = true;
        lifecycleScope.launch {
            val result = UsuarioController.tryLogin(textEditUsername.text.toString().trim(),
                textEditPassword.text.toString().trim(), this@LoginActivity);

            if (result.first != UsuarioResultEnum.ok)
            {
                pendingLogin = false;
                Toast.makeText(this@LoginActivity, getString(result.first.stringValue()),
                    Toast.LENGTH_SHORT).show();
            }
            else
            {
                val storeResult = UsuarioController.saveUserLoginData(textEditUsername.text.toString().trim(),
                    textEditPassword.text.toString().trim(), this@LoginActivity);

                if (!storeResult)
                {
                    Toast.makeText(this@LoginActivity, getString(R.string.error_no_autologin),
                        Toast.LENGTH_LONG).show();
                }
                else
                    result.second!!.id?.let { UsuarioController.saveActiveUserId(it, this@LoginActivity) };


                pendingLogin = false;
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java));
            }
        }
    }

    override fun afterTextChanged(p0: Editable?)
    {
        buttonLogin.isEnabled = validateAllFields();
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    fun validateAllFields(): Boolean
    {
        if (TextUtils.isEmpty(textEditUsername.text) || textEditUsername.text.length < 5)
            return false;

        if (TextUtils.isEmpty(textEditPassword.text) || textEditPassword.text.length < 8)
            return false;

        return true;
    }
}
