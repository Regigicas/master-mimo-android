package es.upsa.mimo.datamodule.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.upsa.mimo.datamodule.database.dao.JuegoFavDao
import es.upsa.mimo.datamodule.database.dao.UsuarioDao
import es.upsa.mimo.datamodule.database.dao.UsuarioJuegoDao
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.database.entities.Usuario
import es.upsa.mimo.datamodule.database.entities.UsuariosJuegos

@Database(entities = arrayOf(Usuario::class, JuegoFav::class, UsuariosJuegos::class), version = 1)
@TypeConverters(Converters::class)
abstract class DatabaseInstance : RoomDatabase()
{
    companion object
    {
        @Volatile
        private var INSTANCE: DatabaseInstance? = null
        fun getInstance(context: Context): DatabaseInstance
        {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context, DatabaseInstance::class.java, "GamesViewerDB")
                    .build()

            return INSTANCE as DatabaseInstance
        }
    }

    abstract fun usuarioDao(): UsuarioDao
    abstract fun juegoFavDao(): JuegoFavDao
    abstract fun usuarioJuegoFavDao(): UsuarioJuegoDao
}
