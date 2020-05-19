package es.upsa.mimo.datamodule.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = arrayOf(Index(value = ["username"], unique = true),
    Index(value = ["email"], unique = true)))
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var username: String,
    var email: String,
    var preferencias: Int,
    var shaHashPass: String
)
