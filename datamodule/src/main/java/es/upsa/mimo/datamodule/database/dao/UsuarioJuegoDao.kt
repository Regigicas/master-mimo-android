package es.upsa.mimo.datamodule.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.database.entities.UsuariosJuegos

@Dao
interface UsuarioJuegoDao
{
    @Query("""
               SELECT id, name, backgroundImage, releaseDate FROM juegofav
               INNER JOIN usuariosjuegos
               ON juegofav.id = usuariosjuegos.juegoId
               WHERE usuariosjuegos.usuarioId = :userId
               """)
    fun getJuegosFavsByUserIdLive(userId: Int): LiveData<List<JuegoFav>>

    @Query("""
               SELECT id, name, backgroundImage, releaseDate FROM juegofav
               INNER JOIN usuariosjuegos
               ON juegofav.id = usuariosjuegos.juegoId
               WHERE usuariosjuegos.usuarioId = :userId
               """)
    suspend fun getJuegosFavsByUserId(userId: Int): List<JuegoFav>

    @Query("""
               SELECT id, name, backgroundImage, releaseDate FROM juegofav
               INNER JOIN usuariosjuegos
               ON juegofav.id = usuariosjuegos.juegoId
               WHERE usuariosjuegos.usuarioId = :userId AND usuariosJuegos.juegoId = :gameId
               """)
    suspend fun getJuegosFavsByUserIdAndGameId(userId: Int, gameId: Int): JuegoFav?

    @Insert
    suspend fun insertJuegoFav(userFav: UsuariosJuegos)

    @Query("DELETE FROM usuariosjuegos WHERE usuarioId = :userId AND juegoId = :gameId")
    suspend fun deleteByUserIdAndGameId(userId: Int, gameId: Int)
}
