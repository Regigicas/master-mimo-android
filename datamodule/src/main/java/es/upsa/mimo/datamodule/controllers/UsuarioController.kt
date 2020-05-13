package es.upsa.mimo.datamodule.controllers

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.room.Database
import com.jcloquell.androidsecurestorage.SecureStorage
import es.upsa.mimo.datamodule.database.DatabaseInstance
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.database.entities.Usuario
import es.upsa.mimo.datamodule.database.entities.UsuariosJuegos
import es.upsa.mimo.datamodule.enums.UsuarioResultEnum
import es.upsa.mimo.datamodule.models.JuegoModel
import java.security.MessageDigest
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class UsuarioController
{
    companion object
    {
        private val usernameStoreFieldName = "acc_login_username";
        private val passwordStoreFieldname = "acc_login_password";
        private val activeUserId = "acc_active_user_id";
        private val sharedPreferencesAccessName = "gamesViewerPreferences";

        @JvmStatic
        suspend fun registrarUsuario(username: String, email: String, password: String, context: Context): UsuarioResultEnum
        {
            if (!validateEmail(email))
                return UsuarioResultEnum.invalidEmail;

            val passHash = hashPassword("${username.toUpperCase(Locale.ROOT)}:$password");
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

            val passHash = hashPassword("${username.toUpperCase(Locale.ROOT)}:$password");

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
        fun saveActiveUserId(id: Int, context: Context)
        {
            val sharedPreferences = context.getSharedPreferences(sharedPreferencesAccessName, Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt(activeUserId, id).apply();
        }

        @JvmStatic
        fun getActiveUserId(context: Context): Int
        {
            val sharedPreferences = context.getSharedPreferences(sharedPreferencesAccessName, Context.MODE_PRIVATE);
            val userId = sharedPreferences.getInt(activeUserId, -1);
            return userId;
        }

        @JvmStatic
        suspend fun getActiveUser(context: Context): Usuario?
        {
            val userId = getActiveUserId(context);
            if (userId == -1)
                return null;

            return DatabaseInstance.getInstance(context).usuarioDao().getUsuario(userId);
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

            return false;
        }

        @JvmStatic
        suspend fun addJuegoFavorito(juegoModel: JuegoModel, context: Context): Boolean
        {
            val usuario = getActiveUser(context);
            if (usuario == null)
                return false;

            val favoritos = DatabaseInstance.getInstance(context).usuarioJuegoFavDao().getJuegosFavsByUserId(usuario.id!!);
            if (favoritos.find { it.id == juegoModel.id } != null)
                return false;

            // Si no tiene el juego en favorito miramos si lo tenemos ya registrado en la DB
            var juegoFav = DatabaseInstance.getInstance(context).juegoFavDao().getJuegoFav(juegoModel.id!!);
            if (juegoFav == null)
                juegoFav = JuegoController.insertNewGameFav(juegoModel, context);
            if (juegoFav == null) // Si tampoco se ha creado en DB retornamos ya false
                return false;

            val userFav = UsuariosJuegos(usuario.id, juegoFav.id);
            try
            {
                DatabaseInstance.getInstance(context).usuarioJuegoFavDao().insertJuegoFav(userFav);
                return true;
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage);
            }

            return false;
        }

        @JvmStatic
        suspend fun removeJuegoFavorito(juegoId: Int, context: Context): Boolean
        {
            val usuario = getActiveUser(context);
            if (usuario == null)
                return false;

            val favoritos = DatabaseInstance.getInstance(context).usuarioJuegoFavDao().getJuegosFavsByUserId(usuario.id!!);
            if (favoritos.find { it.id == juegoId } == null)
                return false;

            try
            {
                DatabaseInstance.getInstance(context).usuarioJuegoFavDao().deleteByUserIdAndGameId(usuario.id, juegoId);
                return true;
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage);
            }

            return false;
        }

        @JvmStatic
        suspend fun hasFavorite(juegoId: Int, context: Context): Boolean
        {
            val usuario = getActiveUser(context);
            if (usuario == null)
                return false;

            val favoritos = DatabaseInstance.getInstance(context).usuarioJuegoFavDao().getJuegosFavsByUserId(usuario.id!!);
            if (favoritos.find { it.id == juegoId } == null)
                return false;

            return true;
        }
    }
}
