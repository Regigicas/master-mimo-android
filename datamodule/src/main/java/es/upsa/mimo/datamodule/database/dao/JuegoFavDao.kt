package es.upsa.mimo.datamodule.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.upsa.mimo.datamodule.database.entities.JuegoFav

@Dao
interface JuegoFavDao
{
    @Query("SELECT * FROM juegofav WHERE id = :id")
    suspend fun getJuegoFav(id: Int): JuegoFav?

    @Query("SELECT * FROM juegofav WHERE UPPER(name) = UPPER(:name)")
    suspend fun getJuegoFavByName(name: String): JuegoFav?

    @Insert
    suspend fun insertJuegoFav(juegofav: JuegoFav)
}
