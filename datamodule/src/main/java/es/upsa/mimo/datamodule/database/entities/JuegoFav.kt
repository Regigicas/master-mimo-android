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
    val name: String,
    val backgroundImage: String,
    val releaseDate: Date
) : Serializable
