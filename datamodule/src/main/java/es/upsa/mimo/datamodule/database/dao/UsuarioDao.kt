package es.upsa.mimo.datamodule.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.upsa.mimo.datamodule.database.entities.Usuario

@Dao
interface UsuarioDao
{
    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun getUsuario(id: Int): Usuario?;

    @Query("SELECT * FROM usuario WHERE UPPER(username) = UPPER(:username)")
    suspend fun getUsuarioByUsername(username: String): Usuario?;

    @Query("SELECT * FROM usuario WHERE UPPER(email) = UPPER(:email)")
    suspend fun getUsuarioByEmail(email: String): Usuario?;

    @Insert
    suspend fun insertUsuario(usuario: Usuario);
}
