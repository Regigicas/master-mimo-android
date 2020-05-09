package es.upsa.mimo.datamodule.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.upsa.mimo.datamodule.database.entities.JuegoFav

@Dao
interface JuegoFavDao
{
    @Query("SELECT * FROM juegofav WHERE id = :id")
    fun getJuegoFav(id: Int): JuegoFav?;

    @Query("SELECT * FROM juegofav WHERE UPPER(name) = UPPER(:name)")
    fun getJuegoFavByName(name: String): JuegoFav?;

    @Insert
    fun insertJuegoFav(juegofav: JuegoFav);
}
