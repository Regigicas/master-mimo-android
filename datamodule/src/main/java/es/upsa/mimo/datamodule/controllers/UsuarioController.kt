package es.upsa.mimo.datamodule.controllers

import android.content.Context
import android.util.Log
import com.jcloquell.androidsecurestorage.SecureStorage
import es.upsa.mimo.datamodule.database.DatabaseInstance
import es.upsa.mimo.datamodule.database.entities.Usuario
import es.upsa.mimo.datamodule.enums.UsuarioResultEnum
import java.security.MessageDigest

class UsuarioController
{
    companion object
    {
        private val usernameStoreFieldName = "acc_login_username";
        private val passwordStoreFieldname = "acc_login_password";

        @JvmStatic
        suspend fun registrarUsuario(username: String, email: String, password: String, context: Context): UsuarioResultEnum
        {
            if (!validateEmail(email))
                return UsuarioResultEnum.invalidEmail;

            val passHash = hashPassword("${username.toUpperCase()}:$password");
            val usuario = Usuario(null, username, email, 0, passHash);

            try
            {
                DatabaseInstance.getInstance(context).usuarioDao().insertUsuario(usuario);
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage);

                if (ex.localizedMessage.contains("Usuario.username"))
                    return UsuarioResultEnum.existingUser;

                if (ex.localizedMessage.contains("Usuario.email"))
                    return UsuarioResultEnum.existingEmail;
            }

            return UsuarioResultEnum.ok;
        }

        @JvmStatic
        fun validateEmail(email: String): Boolean
        {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        @JvmStatic
        fun hashPassword(password: String): String
        {
            val bytes = password.toString().toByteArray();
            val md = MessageDigest.getInstance("SHA-256");
            val digest = md.digest(bytes);
            return digest.fold("", { str, it -> str + "%02x".format(it) });
        }

        @JvmStatic
        suspend fun tryLogin(username: String, password: String, context: Context): Pair<UsuarioResultEnum, Usuario?>
        {
            val userLogin = DatabaseInstance.getInstance(context).usuarioDao().getUsuarioByUsername(username);
            if (userLogin == null)
                return Pair(UsuarioResultEnum.usernameNotFound, null);

            val passHash = hashPassword("${username.toUpperCase()}:$password");

            Log.d("info", "$passHash - ${userLogin.shaHashPass}")

            if (userLogin.shaHashPass != passHash)
                return Pair(UsuarioResultEnum.passwordMismatch, null);

            return Pair(UsuarioResultEnum.ok, userLogin);
        }

        @JvmStatic
        fun saveUserLoginData(username: String, password: String, context: Context): Boolean
        {
            val secureStorage = SecureStorage(context);
            try
            {
                secureStorage.storeObject(usernameStoreFieldName, username);
                secureStorage.storeObject(passwordStoreFieldname, password);
            }
            catch (ex: Throwable)
            {
                return false;
            }

            return true;
        }

        @JvmStatic
        suspend fun tryAutoLogin(context: Context): Boolean
        {
            val secureStorage = SecureStorage(context);
            try
            {
                val username = secureStorage.getObject(usernameStoreFieldName, String::class.java);
                val password = secureStorage.getObject(passwordStoreFieldname, String::class.java);
                if (username != null && password != null)
                {
                    val (result, _) = tryLogin(username, password, context);
                    if (result == UsuarioResultEnum.ok)
                        return true;
                }
            }
            catch (ex: Throwable)
            {
                return false;
            }

            return true;
        }
    }
}
