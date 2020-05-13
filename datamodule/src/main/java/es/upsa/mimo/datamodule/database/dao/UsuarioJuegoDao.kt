package es.upsa.mimo.datamodule.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.database.entities.UsuariosJuegos

@Dao
interface UsuarioJuegoDao
{
    @Query("""
               SELECT * FROM juegofav
               INNER JOIN usuariosjuegos
               ON juegofav.id = usuariosjuegos.juegoId
               WHERE usuariosjuegos.usuarioId = :userId
               """)
    suspend fun getJuegosFavsByUserId(userId: Int): List<JuegoFav>

    @Insert
    suspend fun insertJuegoFav(userFav: UsuariosJuegos);

    @Query("DELETE FROM usuariosjuegos WHERE usuarioId = :userId AND juegoId = :gameId")
    suspend fun deleteByUserIdAndGameId(userId: Int, gameId: Int);
}
