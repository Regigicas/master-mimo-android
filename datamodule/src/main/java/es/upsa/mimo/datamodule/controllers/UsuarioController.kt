package es.upsa.mimo.datamodule.controllers

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Database
import com.jcloquell.androidsecurestorage.SecureStorage
import es.upsa.mimo.datamodule.R
import es.upsa.mimo.datamodule.database.DatabaseInstance
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.database.entities.Usuario
import es.upsa.mimo.datamodule.database.entities.UsuariosJuegos
import es.upsa.mimo.datamodule.enums.UsuarioResultEnum
import es.upsa.mimo.datamodule.models.JuegoModel
import java.security.MessageDigest
import java.util.*

class UsuarioController
{
    companion object
    {
        private val usernameStoreFieldName = "acc_login_username"
        private val passwordStoreFieldname = "acc_login_password"
        private val activeUserId = "acc_active_user_id"

        @JvmStatic
        suspend fun registrarUsuario(username: String, email: String, password: String, context: Context): UsuarioResultEnum
        {
            if (!validateEmail(email))
                return UsuarioResultEnum.invalidEmail

            val passHash = hashPassword(username, password);
            val usuario = Usuario(null, username, email, passHash, false)

            try
            {
                DatabaseInstance.getInstance(context).usuarioDao().insertUsuario(usuario)
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage)

                if (ex.localizedMessage.contains("Usuario.username"))
                    return UsuarioResultEnum.existingUser

                if (ex.localizedMessage.contains("Usuario.email"))
                    return UsuarioResultEnum.existingEmail
            }

            return UsuarioResultEnum.ok
        }

        @JvmStatic
        fun validateEmail(email: String): Boolean
        {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        @JvmStatic
        fun hashPassword(username: String, password: String): String
        {
            val generatedPassword = "${username.toUpperCase(Locale.ROOT)}:${password}"
            val bytes = generatedPassword.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("", { str, it -> str + "%02x".format(it) })
        }

        @JvmStatic
        suspend fun tryLogin(username: String, password: String, context: Context): Pair<UsuarioResultEnum, Usuario?>
        {
            val userLogin = DatabaseInstance.getInstance(context).usuarioDao().getUsuarioByUsername(username)
            if (userLogin == null)
                return Pair(UsuarioResultEnum.usernameNotFound, null)

            val passHash = hashPassword(username, password)

            if (userLogin.shaHashPass != passHash)
                return Pair(UsuarioResultEnum.passwordMismatch, null)

            return Pair(UsuarioResultEnum.ok, userLogin)
        }

        @JvmStatic
        fun saveUserLoginData(username: String, password: String, context: Context): Boolean
        {
            try
            {
                val secureStorage = SecureStorage(context)
                secureStorage.storeObject(usernameStoreFieldName, username)
                secureStorage.storeObject(passwordStoreFieldname, password)
            }
            catch (ex: Throwable)
            {
                return false
            }

            return true
        }

        @JvmStatic
        fun saveActiveUserId(id: Int, context: Context)
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.edit().putInt(activeUserId, id).apply()
        }

        @JvmStatic
        fun getActiveUserId(context: Context): Int
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(activeUserId, -1)
        }

        @JvmStatic
        suspend fun getActiveUser(context: Context): Usuario?
        {
            val userId = getActiveUserId(context)
            if (userId == -1)
                return null

            return DatabaseInstance.getInstance(context).usuarioDao().getUsuario(userId)
        }

        @JvmStatic
        suspend fun tryAutoLogin(context: Context): Boolean
        {
            try
            {
                val secureStorage = SecureStorage(context)
                val username = secureStorage.getObject(usernameStoreFieldName, String::class.java)
                val password = secureStorage.getObject(passwordStoreFieldname, String::class.java)
                if (username != null && password != null)
                {
                    val (result, usuario) = tryLogin(username, password, context)
                    if (result == UsuarioResultEnum.ok)
                    {
                        if (usuario != null)
                            saveActiveUserId(usuario.id!!, context)
                        return true
                    }
                }
            }
            catch (ex: Throwable)
            {
                return false
            }

            return false
        }

        @JvmStatic
        suspend fun addJuegoFavorito(juegoModel: JuegoModel, context: Context): Boolean
        {
            val usuario = getActiveUser(context)
            if (usuario == null)
                return false

            if (hasFavorite(juegoModel.id, usuario, context)) // Si lo tiene en favoritos no hay nada mas que hacer
                return false

            // Si no tiene el juego en favorito miramos si lo tenemos ya registrado en la DB
            var juegoFav = JuegoController.getGameFav(juegoModel.id, context)
            if (juegoFav == null)
                juegoFav = JuegoController.insertNewGameFav(juegoModel, context)
            if (juegoFav == null) // Si tampoco se ha creado en DB retornamos ya false
                return false

            val userFav = UsuariosJuegos(usuario.id!!, juegoFav.id)
            try
            {
                DatabaseInstance.getInstance(context).usuarioJuegoFavDao().insertJuegoFav(userFav)
                return true
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage)
            }

            return false
        }

        @JvmStatic
        suspend fun removeJuegoFavorito(juegoId: Int, context: Context): Boolean
        {
            val usuario = getActiveUser(context)
            if (usuario == null)
                return false

            if (!hasFavorite(juegoId, usuario, context)) // Si no lo tiene en favoritos no hay nada mas que hacer
                return false

            try
            {
                DatabaseInstance.getInstance(context).usuarioJuegoFavDao().deleteByUserIdAndGameId(usuario.id!!, juegoId)
                return true
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage)
            }

            return false
        }

        @JvmStatic
        suspend fun hasFavorite(juegoId: Int, usuarioActivo: Usuario?, context: Context): Boolean
        {
            val usuario = usuarioActivo ?: getActiveUser(context)
            if (usuario == null)
                return false

            val favorito = DatabaseInstance.getInstance(context).usuarioJuegoFavDao().getJuegosFavsByUserIdAndGameId(usuario.id!!, juegoId)
            if (favorito == null)
                return false

            return true
        }

        @JvmStatic
        suspend fun getFavoriteList(context: Context): List<JuegoFav>
        {
            val usuario = getActiveUser(context)
            if (usuario == null)
                return listOf()

            return DatabaseInstance.getInstance(context).usuarioJuegoFavDao().getJuegosFavsByUserId(usuario.id!!)
        }

        @JvmStatic
        fun getObservableOfFavorites(context: Context): LiveData<List<JuegoFav>>
        {
            return DatabaseInstance.getInstance(context).usuarioJuegoFavDao()
                .getJuegosFavsByUserIdLive(getActiveUserId(context))
        }

        @JvmStatic
        fun logoutUser(context: Context)
        {
            try
            {
                val secureStorage = SecureStorage(context)
                secureStorage.removeObject(usernameStoreFieldName)
                secureStorage.removeObject(passwordStoreFieldname)
                secureStorage.removeObject(activeUserId)
            }
            catch (ex: Throwable) {}
        }

        @JvmStatic
        suspend fun changePassword(oldPass: String, newPass: String, context: Context): UsuarioResultEnum
        {
            val usuario = getActiveUser(context)
            if (usuario == null)
                return UsuarioResultEnum.usernameNotFound

            val upperName = usuario.username.toUpperCase(Locale.ROOT)
            val oldPassHash = hashPassword(upperName, oldPass)
            val newPassHash = hashPassword(upperName, newPass)
            if (oldPassHash == newPassHash)
                return UsuarioResultEnum.oldNewPasswordSame

            if (oldPassHash != usuario.shaHashPass)
                return UsuarioResultEnum.oldPasswordMismatch

            usuario.shaHashPass = newPassHash
            DatabaseInstance.getInstance(context).usuarioDao().updateUser(usuario)
            return UsuarioResultEnum.ok
        }

        @JvmStatic
        suspend fun hasNotifyFavRelease(context: Context): Boolean
        {
            val usuario = getActiveUser(context)
            return usuario?.favoriteNotification ?: false
        }

        @JvmStatic
        suspend fun setUserNotifyFavRelease(context: Context, status: Boolean)
        {
            val usuario = getActiveUser(context)
            if (usuario == null)
                return

            usuario.favoriteNotification = status
            DatabaseInstance.getInstance(context).usuarioDao().updateUser(usuario)
        }
    }
}
