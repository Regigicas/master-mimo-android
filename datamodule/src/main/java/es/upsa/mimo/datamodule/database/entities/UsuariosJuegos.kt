package es.upsa.mimo.datamodule.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import es.upsa.mimo.datamodule.database.entities.JuegoFav
import es.upsa.mimo.datamodule.database.entities.Usuario

@Entity( foreignKeys = arrayOf(
    ForeignKey(entity = Usuario::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("usuarioId")),

    ForeignKey(entity = JuegoFav::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("juegoId"))),
primaryKeys = arrayOf("usuarioId", "juegoId"))
data class UsuariosJuegos(
    val usuarioId: Int,
    @ColumnInfo(index = true)
    val juegoId: Int
)
