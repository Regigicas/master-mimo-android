package es.upsa.mimo.datamodule.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Date

@Entity
data class JuegoFav(
    @PrimaryKey()
    val id: Int,
    var name: String,
    var backgroundImage: String,
    var releaseDate: Date
) : Serializable
