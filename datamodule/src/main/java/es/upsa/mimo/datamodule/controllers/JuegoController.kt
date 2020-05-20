package es.upsa.mimo.datamodule.controllers

import android.content.Context
import android.util.Log
import es.upsa.mimo.datamodule.database.DatabaseInstance
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.models.JuegoModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class JuegoController
{
    companion object
    {
        @JvmStatic
        suspend fun insertNewGameFav(juegoModel: JuegoModel, context: Context): JuegoFav?
        {
            try
            {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                val dateParsed = dateFormat.parse(juegoModel.released!!)
                val date = Date(dateParsed.time)
                val juegoBackground = juegoModel.background_image ?: ""
                val juegoFav = JuegoFav(juegoModel.id, juegoModel.name, juegoBackground, date)
                DatabaseInstance.getInstance(context).juegoFavDao().insertJuegoFav(juegoFav)
                return juegoFav
            }
            catch (ex: Throwable)
            {
                Log.e("error", ex.localizedMessage)
            }

            return  null
        }
    }
}
