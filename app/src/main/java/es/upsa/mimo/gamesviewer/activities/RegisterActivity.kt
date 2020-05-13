package es.upsa.mimo.gamesviewer.activities

import android.os.Bundle
import android.os.Handler
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
import es.upsa.mimo.gamesviewer.misc.Util
import kotlinx.coroutines.launch
import java.lang.AssertionError

class RegisterActivity : AppCompatActivity(), TextWatcher
{
    private var textEditUsername: EditText? = null;
    private var textEditEmail: EditText? = null;
    private var textEditPassword: EditText? = null;
    private var textEditPwdRpt: EditText? = null;
    private var buttonRegister: Button? = null;
    private var pendingInsert: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textEditUsername = findViewById(R.id.textEditUsername);
        textEditEmail = findViewById(R.id.textEditEmail);
        textEditPassword = findViewById(R.id.textEditPassword);
        textEditPwdRpt = findViewById(R.id.textEditPasswordRepeat);
        buttonRegister = findViewById(R.id.buttonRegister);

        if (textEditUsername == null || textEditEmail == null || textEditPassword == null ||
            textEditPwdRpt == null || buttonRegister == null)
            throw AssertionError(getString(R.string.assert_view_not_created));

        textEditUsername!!.addTextChangedListener(this)
        textEditEmail!!.addTextChangedListener(this)
        textEditPassword!!.addTextChangedListener(this)
        textEditPwdRpt!!.addTextChangedListener(this)

        textEditPwdRpt!!.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE)
            {
                registerUser();
                true
            }
            else
                false
        }

        buttonRegister!!.setOnClickListener {
            registerUser();
        }
    }

    fun registerUser()
    {
        Util.hideKeyBoard(this);
        if (pendingInsert)
            return;

        lifecycleScope.launch {
            val result = UsuarioController.registrarUsuario(textEditUsername!!.text.toString().trim(),
                textEditEmail!!.text.toString().trim(), textEditPassword!!.text.toString().trim(), this@RegisterActivity);

            val toastText = if (result == UsuarioResultEnum.ok) getString(R.string.register_ok_return_login) else getString(result.stringValue());
            Toast.makeText(this@RegisterActivity, toastText,
                Toast.LENGTH_SHORT).show();

            if (result == UsuarioResultEnum.ok)
            {
                buttonRegister?.isEnabled = false;
                Handler().postDelayed({
                    finish();
                }, 2000);
            }
            else
                pendingInsert = false;
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu)
    }

    fun validateAllFields(): Boolean
    {
        if (TextUtils.isEmpty(textEditUsername!!.text) || textEditUsername!!.text.length < 5)
            return false;

        if (TextUtils.isEmpty(textEditEmail!!.text) || UsuarioController.validateEmail(textEditEmail!!.text.toString()) == false)
            return false;

        if (TextUtils.isEmpty(textEditPassword!!.text) || textEditPassword!!.text.length < 8)
            return false;

        if (TextUtils.isEmpty(textEditPwdRpt!!.text) || textEditPassword!!.text.toString() != textEditPwdRpt!!.text.toString())
            return false;

        return true;
    }

    override fun afterTextChanged(p0: Editable?)
    {
        buttonRegister!!.isEnabled = validateAllFields();
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}
